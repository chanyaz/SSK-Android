package base.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import base.app.Constant;
import base.app.GSAndroidPlatform;
import base.app.R;
import base.app.events.NotificationReceivedEvent;
import base.app.fragment.FragmentEvent;
import base.app.fragment.FragmentOrganizer;
import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallFragment;
import base.app.fragment.popup.FollowersFragment;
import base.app.fragment.popup.FriendsFragment;
import base.app.fragment.popup.WalletFragment;
import base.app.model.Model;
import base.app.model.notifications.ExternalNotificationEvent;
import base.app.model.notifications.InternalNotificationManager;
import base.app.model.purchases.PurchaseModel;
import base.app.model.sharing.NativeShareEvent;
import base.app.model.sharing.SharingManager;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.ticker.NextMatchModel;
import base.app.model.user.LoginStateReceiver;
import base.app.model.videoChat.VideoChatEvent;
import base.app.model.videoChat.VideoChatModel;
import base.app.util.ui.ThemeManager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static base.app.util.Utility.checkIfBundlesAreEqual;


public class BaseActivity extends AppCompatActivity  {

    public static final String TAG = "Base Activity";
    protected FragmentOrganizer fragmentOrganizer;
    protected LoginStateReceiver loginStateReceiver;
    protected   CallbackManager callbackManager;
    protected ShareDialog facebookShareDialog;
    RelativeLayout notificationContainer;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

    }

    Timer newsTimer;
    int count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().assignTheme(this);
        setContentView(R.layout.activity_base); // TODO @Aleksandar - Do we need this layout at all?
        callbackManager = CallbackManager.Factory.create();
        Model.getInstance().initialize(this);
        VideoChatModel.getInstance();
        facebookShareDialog = new ShareDialog(this);
        // internal notifications initialization
        InternalNotificationManager.getInstance();
        // this part is optional
        facebookShareDialog.registerCallback(callbackManager, SharingManager.getInstance());
        notificationContainer=(RelativeLayout) findViewById(R.id.left_notification_container);
        PurchaseModel.getInstance().onCreate(this);
    }

    protected Bundle savedIntentData = null;
    protected void checkAndEmitBackgroundNotification() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            if (savedIntentData == null) {
                savedIntentData = new Bundle();
            }
            if (!checkIfBundlesAreEqual(savedIntentData, extras)) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> notificationData = mapper.convertValue(extras.getString(Constant.NOTIFICATION_DATA, ""), new TypeReference<Map<String, String>>() {
                });
                handleNotificationEvent(new ExternalNotificationEvent(notificationData, false));
                savedIntentData = getIntent().getExtras();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NextMatchModel.getInstance().getNextMatchInfo();
        checkAndEmitBackgroundNotification();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void handleNotificationEvent(ExternalNotificationEvent event) {
        Map<String, String> notificationData = event.getData();
        if (event.isFromBackground()) { // we ignore notifications that are received while app is active
            if (notificationData.containsKey("chatId")) {
                EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
            } else if (notificationData.containsKey("wallId")) {
                EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
            }
        }
    }

    protected void startNewsTimer(final NewsTickerInfo newsTickerInfo,final TextView newsLabel) {
        count = 0;
        if (newsTimer != null) {
            newsTimer.cancel();
        }
        newsTimer = new Timer();
        newsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsLabel.setText(newsTickerInfo.getNews().get(count));
                        if (++count == newsTickerInfo.getNews().size()) {
                            count = 0;
                        }
                    }
                });
            }
        }, 0, Constant.LOGIN_TEXT_TIME);

    }

    @Override
    public void onStart() {
        super.onStart();
        GSAndroidPlatform.gs().start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Share.toRequestCode()) // share to facebook
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) // sign up Fragment - Continue with facebook
        {
            fragmentOrganizer.getOpenFragment().onActivityResult(requestCode, resultCode, data);
        }

        PurchaseModel.getInstance().onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        fragmentOrganizer.freeUpResources();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(loginStateReceiver);
        PurchaseModel.getInstance().onDestroy();
        super.onDestroy();
    }

    public FragmentOrganizer getFragmentOrganizer() {
        return fragmentOrganizer;
    }

    @Subscribe
    public void onVideoChatEvent(VideoChatEvent event) {
        if (!(fragmentOrganizer.getOpenFragment() instanceof VideoChatFragment)) {
            EventBus.getDefault().post(new FragmentEvent(VideoChatFragment.class));
            VideoChatModel.getInstance().setVideoChatEvent(event);
        } else {
            ((VideoChatFragment) fragmentOrganizer.getOpenFragment()).onVideoChatEvent(event);
        }
    }

    protected void hideNotification(final View v) {
        Animation animationHide = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
        v.startAnimation(animationHide);
        animationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                notificationContainer=(RelativeLayout) findViewById(R.id.left_notification_container);
                notificationContainer .removeView(v);
                v.setVisibility(View.GONE);
            }
        });
    }

    protected void showNotification(final View v, int time) {
        notificationContainer=(RelativeLayout) findViewById(R.id.left_notification_container);
        notificationContainer.addView(v, 0);

        Animation animationShow = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        v.startAnimation(animationShow);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                hideNotification(v);
            }
        }, time * 1000);
    }

    @Subscribe
    public void onNewNotification(NotificationReceivedEvent event) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.notification_row, null);

        TextView title = (TextView) v.findViewById(R.id.notification_title);
        title.setText(event.getTitle());

        TextView description = (TextView) v.findViewById(R.id.notification_description);
        description.setText(event.getDescription());

        switch (event.getType()) {
            case 1:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
                    }
                });
                break;
            case 2:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(FollowersFragment.class));
                    }
                });
                break;
            case 3:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
                    }
                });
                break;
        }
        showNotification(v, event.getCloseTime());
    }

    @Subscribe
    public void onShareOnFacebookEvent(ShareLinkContent linkContent) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            facebookShareDialog.show(linkContent);
        }
    }

    @Subscribe
    public void onShareOnTwitterEvent(TweetComposer.Builder event) {
        event.show();
    }

    @Subscribe
    public void onShareNativeEvent(NativeShareEvent event) {
        startActivity(Intent.createChooser(event.getIntent(), getResources().getString(R.string.share_using)));
    }


}