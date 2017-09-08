package base.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Locale;
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
import base.app.fragment.instance.NewsFragment;
import base.app.fragment.instance.NewsItemFragment;
import base.app.fragment.instance.RumoursFragment;
import base.app.fragment.instance.StatisticsFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallItemFragment;
import base.app.fragment.popup.FollowersFragment;
import base.app.fragment.popup.FriendsFragment;
import base.app.model.Model;
import base.app.model.notifications.ExternalNotificationEvent;
import base.app.model.notifications.InternalNotificationManager;
import base.app.model.purchases.PurchaseModel;
import base.app.model.sharing.NativeShareEvent;
import base.app.model.sharing.SharingManager;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.ticker.NextMatchModel;
import base.app.model.ticker.NextMatchUpdateEvent;
import base.app.model.user.LoginStateReceiver;
import base.app.model.videoChat.VideoChatEvent;
import base.app.model.videoChat.VideoChatModel;
import base.app.util.Utility;
import base.app.util.ui.ThemeManager;
import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static base.app.util.Utility.CHOSEN_LANGUAGE;
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
        String language = Prefs.getString(CHOSEN_LANGUAGE,"en");
        Locale newLocale = new Locale(language);
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
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
        notificationContainer= findViewById(R.id.left_notification_container);
        PurchaseModel.getInstance().onCreate(this);
    }

    protected Bundle savedIntentData = null;
    protected void handleStartingIntent(Intent intent) {
        boolean isFistTimeStartingApp = Prefs.getBoolean(Constant.IS_FIRST_TIME, true);
        Bundle extras = intent.getExtras();
        if (isFistTimeStartingApp) {
            // in case we are starting this app for first time, ignore intent's data
            Prefs.putBoolean(Constant.IS_FIRST_TIME, false);
            Uri deeplink = intent.getData();
            handleDeepLink(deeplink);
        } else  if (extras != null && !extras.isEmpty()) {

            if (savedIntentData == null) {
                savedIntentData = new Bundle();
            }
            // make sure we are not handling the same intent
            if (!checkIfBundlesAreEqual(savedIntentData, extras)) {
                String action = intent.getAction();
                if(Intent.ACTION_VIEW.equals(action)){
                    Uri deeplink = intent.getData();
                    handleDeepLink(deeplink);
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    String notificationData = extras.getString(Constant.NOTIFICATION_DATA, "");
                    try {
                        Map<String, String> dataMap = mapper.readValue(
                                notificationData, new TypeReference<Map<String, String>>() {});
                        handleNotificationEvent(new ExternalNotificationEvent(dataMap, true));
                        savedIntentData = extras;
                    } catch (IOException e) {
                        Log.e(TAG, "Error parsing notification data!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void handleDeepLink(Uri uri){
        if(uri!=null){
            String lastPathSegment =uri.getLastPathSegment();
            String[] parts = StringUtils.split(lastPathSegment, ":");
            if(parts!=null && parts.length==3){
                String clubId = parts[2];
                String postType = parts[1];
                String postId = parts[0]; // WallPost ?
                Log.d(TAG,"Post id is : " + postId);
                if(SharingManager.ItemType.WallPost.name().equals(postType)){
                    FragmentEvent wallItemFragmentEvent = new FragmentEvent(WallItemFragment.class);
                    wallItemFragmentEvent.setId(postId + "$$$");
                    // TODO - Load wall item before displaying it ( or this is handled in fragment? )
                    EventBus.getDefault().post(wallItemFragmentEvent);
                } else if(SharingManager.ItemType.News.name().equals(postType)){
                    FragmentEvent newsItemFragmentEvent = new FragmentEvent(NewsItemFragment.class);
                    newsItemFragmentEvent.setId(postId);
                    // TODO - Load news item before displaying it ( or this is handled in fragment? )
                    EventBus.getDefault().post(newsItemFragmentEvent);
                }
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
                FragmentEvent wallItemFragmentEvent = new FragmentEvent(WallItemFragment.class);
                wallItemFragmentEvent.setId(postId + "$$$" + wallId );
                // TODO - Load wall item before displaying it ( or this is handled in fragment? )
                EventBus.getDefault().post(wallItemFragmentEvent);
            } else if(notificationData.containsKey("newsItem") && notificationData.containsKey("newsType")){
                if( "official".equals(notificationData.get("newsType"))){
                    EventBus.getDefault().post(NewsFragment.class);
                } else {
                    EventBus.getDefault().post(RumoursFragment.class);
                }
                if(!"-1".equals(notificationData.get("newsItem"))){
                    FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                    String id = notificationData.get("newsItem");
                    if("official".equals(notificationData.get("newsType"))){
                        fe.setId("UNOFFICIAL$$$" + id);
                    } else {
                        fe.setId(id);
                    }
                    // TODO - Load news item before displaying it
                    //EventBus.getDefault().post(fe);
                }

            }else if (notificationData.containsKey("statsItem")){
                EventBus.getDefault().post(StatisticsFragment.class);
            } else if(notificationData.containsKey("conferenceId")){
                String conferenceId = notificationData.get("conferenceId");
                FragmentEvent fe = new FragmentEvent(VideoChatFragment.class);
                fe.setId(conferenceId);
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
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Share.toRequestCode()){// share to facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){ // sign up Fragment - Continue with facebook
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
        Fragment currentFragment =  fragmentOrganizer.getCurrentFragment();
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
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                notificationContainer=findViewById(R.id.left_notification_container);

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
        notificationContainer=findViewById(R.id.left_notification_container);
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
    public void onNewNotification(final NotificationReceivedEvent event) {
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
                        if(Utility.isTablet(getApplicationContext())) {
                            EventBus.getDefault().post(new FragmentEvent(FollowersFragment.class));
                        }
                    }
                });
                break;
            case LIKES:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String postId = event.getPostId();
                        if(postId!=null){
                            FragmentEvent fe = new FragmentEvent(WallItemFragment.class);
                            fe.setId(postId);
                            EventBus.getDefault().post(fe);
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
    public void onShareOnTwitterEvent(TweetComposer.Builder event) {
        event.show();
    }

    @Subscribe
    public void onShareNativeEvent(NativeShareEvent event) {
        startActivity(Intent.createChooser(event.getIntent(), getResources().getString(R.string.share_using)));
    }


}