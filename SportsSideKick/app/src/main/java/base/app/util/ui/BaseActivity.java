package base.app.util.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.pixplicity.easyprefs.library.Prefs;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import base.app.R;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.commons.GSAndroidPlatform;
import base.app.data.user.notifications.ExternalNotificationEvent;
import base.app.data.user.notifications.InternalNotificationManager;
import base.app.data.user.purchases.PurchaseModel;
import base.app.util.events.NativeShareEvent;
import base.app.data.content.wall.nextmatch.NewsTickerInfo;
import base.app.data.content.wall.nextmatch.NextMatchModel;
import base.app.util.events.NextMatchUpdateEvent;
import base.app.data.user.LoginStateReceiver;
import base.app.data.chat.videochat.VideoChatEvent;
import base.app.data.chat.videochat.VideoChatModel;
import base.app.util.events.FragmentEvent;
import base.app.ui.fragment.base.FragmentOrganizer;
import base.app.ui.fragment.content.news.NewsDetailFragment;
import base.app.ui.fragment.content.news.NewsFragment;
import base.app.ui.fragment.content.news.RumoursFragment;
import base.app.ui.fragment.content.wall.DetailFragment;
import base.app.ui.fragment.other.ChatFragment;
import base.app.ui.fragment.other.StatisticsFragment;
import base.app.ui.fragment.popup.FollowersFragment;
import base.app.ui.fragment.popup.FriendsFragment;
import base.app.ui.fragment.stream.VideoChatFragment;
import base.app.util.commons.Constants;
import base.app.util.commons.LocalizableContext;
import base.app.util.commons.Utility;
import base.app.util.events.NotificationEvent;
import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;
import static base.app.util.commons.Utility.checkIfBundlesAreEqual;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "Base Activity";
    protected FragmentOrganizer fragmentOrganizer;
    protected LoginStateReceiver loginStateReceiver;
    protected CallbackManager callbackManager;
    protected ShareDialog facebookShareDialog;
    RelativeLayout notificationContainer;

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = Prefs.getString(CHOSEN_LANGUAGE, "en");
        Locale newLocale = new Locale(language);
        Context context = LocalizableContext.wrap(newBase, newLocale);
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    Timer newsTimer;
    int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        LoginApi.getInstance().initialize(this);
        VideoChatModel.getInstance();
        facebookShareDialog = new ShareDialog(this);
        // internal notifications initialization
        InternalNotificationManager.getInstance();
        notificationContainer = findViewById(R.id.left_notification_container);
        PurchaseModel.getInstance().onCreate(this);

        makeStatusBarTransparent();
    }

    private void makeStatusBarTransparent() {
        getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
    }

    protected Bundle savedIntentData = null;

    protected void handleStartingIntent(Intent intent) {
        boolean isFistTimeStartingApp = Prefs.getBoolean(Constants.IS_FIRST_TIME, true);
        Bundle extras = intent.getExtras();
        if (isFistTimeStartingApp) {
            // in case we are starting this app for first time, ignore intent's data
            Prefs.putBoolean(Constants.IS_FIRST_TIME, false);
        } else if (extras != null && !extras.isEmpty()) {
            if (savedIntentData == null) {
                savedIntentData = new Bundle();
            }
            // make sure we are not handling the same intent
            if (!checkIfBundlesAreEqual(savedIntentData, extras)) {
                ObjectMapper mapper = new ObjectMapper();
                String notificationData = extras.getString(Constants.NOTIFICATION_DATA, "");
                try {
                    Map<String, String> dataMap = mapper.readValue(
                            notificationData, new TypeReference<Map<String, String>>() {
                            });
                    handleNotificationEvent(new ExternalNotificationEvent(dataMap, true));
                    savedIntentData = extras;
                } catch (IOException e) {
                    Log.e(TAG, "Error parsing notification data!");
                    e.printStackTrace();
                }
            }
        }
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri deeplink = intent.getData();
            Log.d(TAG, "deeplink : " + deeplink.toString());
            handleDeepLink(deeplink);
        }
    }

    private void handleDeepLink(Uri uri) {
        if (uri != null) {
            String lastPathSegment = uri.getLastPathSegment();
            String[] parts = StringUtils.split(lastPathSegment, ":");
            if (parts != null && parts.length == 3) {
                String postType = parts[1];
                String postId = parts[0];

                FragmentEvent wallItemFragmentEvent = new FragmentEvent(DetailFragment.class);
                wallItemFragmentEvent.setItemId(postId + "$$$");
                EventBus.getDefault().post(wallItemFragmentEvent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NextMatchModel.getInstance().getNextMatchInfo();
        handleStartingIntent(getIntent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void handleNotificationEvent(ExternalNotificationEvent event) {
        Map<String, String> notificationData = event.getData();
        if (event.isFromBackground()) {
            if (notificationData.containsKey("chatId")) {
                EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
            } else if (notificationData.containsKey("wallId") && notificationData.containsKey("postId")) {
                String postId = notificationData.get("postId");
                String wallId = notificationData.get("wallId");
                FragmentEvent wallItemFragmentEvent = new FragmentEvent(DetailFragment.class);
                wallItemFragmentEvent.setItemId(postId + "$$$" + wallId);
                // TODO - Load wall item before displaying it ( or this is handled in fragment? )
                EventBus.getDefault().post(wallItemFragmentEvent);
            } else if (notificationData.containsKey("newsItem") && notificationData.containsKey("newsType")) {
                if ("newsOfficial".equals(notificationData.get("newsType"))) {
                    EventBus.getDefault().post(NewsFragment.class);
                } else {
                    EventBus.getDefault().post(RumoursFragment.class);
                }
                if (!"-1".equals(notificationData.get("newsItem"))) {
                    FragmentEvent fe = new FragmentEvent(NewsDetailFragment.class);
                    String id = notificationData.get("newsItem");
                    if ("newsOfficial".equals(notificationData.get("newsType"))) {
                        fe.setItemId("UNOFFICIAL$$$" + id);
                    } else {
                        fe.setItemId(id);
                    }
                    // TODO - Load news item before displaying it
                    //EventBus.getDefault().savePost(fe);
                }

            } else if (notificationData.containsKey("statsItem")) {
                EventBus.getDefault().post(StatisticsFragment.class);
            } else if (notificationData.containsKey("conferenceId")) {
                String conferenceId = notificationData.get("conferenceId");
                FragmentEvent fe = new FragmentEvent(VideoChatFragment.class);
                fe.setItemId(conferenceId);
                EventBus.getDefault().post(fe);
            }
        } else {
            // TODO Handle Push notifications while app is active
        }
    }

    @BindView(R.id.scrolling_news_title)
    TextView newsLabel;
    @BindView(R.id.caption)
    TextView captionLabel;

    @Subscribe
    public void onTickerUpdate(NewsTickerInfo newsTickerInfo) {
        newsLabel.setText(newsTickerInfo.getNews().get(0));
        captionLabel.setText(newsTickerInfo.getTitle());
        startNewsTimer(newsTickerInfo, newsLabel);
    }


    protected void startNewsTimer(final NewsTickerInfo newsTickerInfo, final TextView newsLabel) {
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
                        // update next match info
                        EventBus.getDefault().post(new NextMatchUpdateEvent());
                        // update news label
                        newsLabel.setText(newsTickerInfo.getNews().get(count));
                        if (++count == newsTickerInfo.getNews().size()) {
                            count = 0;
                        }
                    }
                });
            }
        }, 0, Constants.LOGIN_TEXT_TIME);

    }

    @Override
    public void onStart() {
        super.onStart();
        GSAndroidPlatform.getInstance().start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Share.toRequestCode()) {// share to facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) { // sign up Fragment - Continue with facebook
            fragmentOrganizer.onActivityResult(requestCode, resultCode, data);
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

    @Subscribe
    public void onVideoChatEvent(VideoChatEvent event) {
        Fragment currentFragment = fragmentOrganizer.getCurrentFragment();
        if (!(currentFragment instanceof VideoChatFragment) && event.getType().equals(VideoChatEvent.Type.onSelfInvited)) {
            EventBus.getDefault().post(new FragmentEvent(VideoChatFragment.class));
            VideoChatModel.getInstance().setVideoChatEvent(event);
        }
    }

    protected void hideNotification(final View notificationView) {
        Animation animationHide = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
        notificationView.startAnimation(animationHide);
        animationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                notificationContainer = findViewById(R.id.left_notification_container);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        notificationView.setVisibility(View.GONE);
                        notificationContainer.removeView(notificationView);
                    }
                }, 100);
            }
        });
    }

    protected void showNotification(final View v, int time) {
        notificationContainer = findViewById(R.id.left_notification_container);
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
    public void onNewNotification(final NotificationEvent event) {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.notification_row, null);

        TextView title = v.findViewById(R.id.notification_title);
        title.setText(event.getTitle());

        TextView description = v.findViewById(R.id.notification_description);
        description.setText(event.getDescription());

        switch (event.getType()) {
            case FRIEND_REQUESTS:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
                    }
                });
                break;
            case FOLLOWERS:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isTablet(getApplicationContext())) {
                            EventBus.getDefault().post(new FragmentEvent(FollowersFragment.class));
                        }
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
    public void onShareNativeEvent(NativeShareEvent event) {
        startActivity(Intent.createChooser(event.getIntent(), getResources().getString(R.string.share)));
    }
}