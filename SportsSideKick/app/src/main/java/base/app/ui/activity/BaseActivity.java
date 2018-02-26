package base.app.ui.activity;

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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.internal.CallbackManagerImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaeger.library.StatusBarUtil;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import base.app.BuildConfig;
import base.app.R;
import base.app.data.Model;
import base.app.data._unused.purchases.PurchaseModel;
import base.app.data._unused.videoChat.VideoChatEvent;
import base.app.data._unused.videoChat.VideoChatModel;
import base.app.data.notifications.ExternalNotificationEvent;
import base.app.data.user.LoginStateReceiver;
import base.app.data.wall.sharing.NativeShareEvent;
import base.app.data.wall.sharing.SharingManager;
import base.app.data.wall.ticker.NewsTickerInfo;
import base.app.data.wall.ticker.NextMatchModel;
import base.app.data.wall.ticker.NextMatchUpdateEvent;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.FragmentOrganizer;
import base.app.ui.fragment.content.NewsFragment;
import base.app.ui.fragment.content.NewsItemFragment;
import base.app.ui.fragment.content.RumoursFragment;
import base.app.ui.fragment.content.WallItemFragment;
import base.app.ui.fragment.other.ChatFragment;
import base.app.ui.fragment.other.StatsFragment;
import base.app.ui.fragment.popup.FollowersFragment;
import base.app.ui.fragment.popup.FriendsFragment;
import base.app.ui.fragment.stream.VideoChatFragment;
import base.app.util.GSAndroidPlatform;
import base.app.util.commons.Constant;
import base.app.util.commons.ContextWrapper;
import base.app.util.commons.Utility;
import base.app.util.events.notify.NotificationEvent;
import butterknife.BindView;
import io.fabric.sdk.android.Fabric;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;
import static base.app.util.commons.Utility.checkIfBundlesAreEqual;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "Base Activity";
    public FragmentOrganizer fragmentOrganizer;
    protected LoginStateReceiver loginStateReceiver;
    ViewGroup notificationContainer;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = Prefs.getString(CHOSEN_LANGUAGE, null);

        String systemLanguage = Locale.getDefault().getLanguage();
        if (systemLanguage.contains("_")) {
            systemLanguage = systemLanguage.split("_")[0];
        }

        if (savedLanguage == null) {
            savedLanguage = systemLanguage;
            Prefs.putString(CHOSEN_LANGUAGE, systemLanguage);
        }

        Locale newLocale = new Locale(savedLanguage);
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Model.getInstance().initialize(this);
        notificationContainer = (ViewGroup) findViewById(R.id.left_notification_container);
        // PurchaseModel.getInstance().onCreate(this);

        StatusBarUtil.setTransparent(this);

        handleStartingIntent(getIntent());

        initCrashlytics();

        NextMatchModel.getInstance().requestNextMatchInfo();
    }

    protected Bundle savedIntentData = null;

    protected void handleStartingIntent(Intent intent) {
        boolean isFistTimeStartingApp = Prefs.getBoolean(Constant.IS_FIRST_TIME, true);
        Bundle extras = intent.getExtras();
        if (isFistTimeStartingApp) {
            // in case we are starting this app for first time, ignore intent's data
            Prefs.putBoolean(Constant.IS_FIRST_TIME, false);
        } else if (extras != null && !extras.isEmpty()) {
            if (savedIntentData == null) {
                savedIntentData = new Bundle();
            }
            // make sure we are not handling the same intent
            if (!checkIfBundlesAreEqual(savedIntentData, extras)) {
                ObjectMapper mapper = new ObjectMapper();
                String notificationData = extras.getString(Constant.NOTIFICATION_DATA, "");
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
            StringTokenizer parts = new StringTokenizer(lastPathSegment, ":");
            if (parts.countTokens() == 3) {
                String postId = parts.nextToken(); // WallPost ?
                String postType = parts.nextToken();
                String clubId = parts.nextToken();
                Log.d(TAG, "Post id is : " + postId);
                if (SharingManager.ItemType.WallPost.name().equals(postType)) {
                    FragmentEvent wallItemFragmentEvent = new FragmentEvent(WallItemFragment.class);
                    wallItemFragmentEvent.setId(postId + "$$$");
                    EventBus.getDefault().post(wallItemFragmentEvent);
                } else if (SharingManager.ItemType.News.name().equals(postType)) {
                    FragmentEvent newsItemFragmentEvent = new FragmentEvent(NewsItemFragment.class);
                    newsItemFragmentEvent.setId(postId);
                    EventBus.getDefault().post(newsItemFragmentEvent);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNotificationEvent(ExternalNotificationEvent event) {
        Map<String, String> notificationData = event.getData();
        if (event.isFromBackground()) {
            if (notificationData.containsKey("chatId")) {
                EventBus.getDefault().post(new FragmentEvent(ChatFragment.class));
            } else if (notificationData.containsKey("wallId") && notificationData.containsKey("postId")) {
                String postId = notificationData.get("postId");
                String wallId = notificationData.get("wallId");
                FragmentEvent wallItemFragmentEvent = new FragmentEvent(WallItemFragment.class);
                wallItemFragmentEvent.setId(postId + "$$$" + wallId);
                EventBus.getDefault().post(wallItemFragmentEvent);
            } else if (notificationData.containsKey("newsItem") && notificationData.containsKey("newsType")) {
                if ("newsOfficial".equals(notificationData.get("newsType"))) {
                    EventBus.getDefault().post(NewsFragment.class);
                } else {
                    EventBus.getDefault().post(RumoursFragment.class);
                }
                if (!"-1".equals(notificationData.get("newsItem"))) {
                    FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                    String id = notificationData.get("newsItem");
                    if ("newsOfficial".equals(notificationData.get("newsType"))) {
                        fe.setId("UNOFFICIAL$$$" + id);
                    } else {
                        fe.setId(id);
                    }
                }

            } else if (notificationData.containsKey("statsItem")) {
                EventBus.getDefault().post(StatsFragment.class);
            } else if (notificationData.containsKey("conferenceId")) {
                String conferenceId = notificationData.get("conferenceId");
                FragmentEvent fe = new FragmentEvent(VideoChatFragment.class);
                fe.setId(conferenceId);
                EventBus.getDefault().post(fe);
            }
        }
    }

    @BindView(R.id.scrolling_news_title)
    TextView newsLabel;
    @BindView(R.id.scrollingNewsSwitcher)
    TextSwitcher scrollingNewsSwitcher;
    @BindView(R.id.caption)
    TextView captionLabel;

    @Subscribe
    public void onTickerUpdate(NewsTickerInfo newsTickerInfo) {
        newsLabel.setText(newsTickerInfo.getNews().get(0));
        captionLabel.setText(newsTickerInfo.getTitle());
        startNewsTimer(newsTickerInfo, newsLabel);
    }

    protected void startNewsTimer(final NewsTickerInfo newsTickerInfo, final TextView newsLabel) {
        // update next match info
        EventBus.getDefault().post(new NextMatchUpdateEvent());

        count = 0;

        disposables.add(Observable.interval(
                Constant.SPLASH_DURATION,
                Constant.SPLASH_DURATION,
                TimeUnit.MILLISECONDS)
                .observeOn(mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        // update news label
                        newsLabel.setText(newsTickerInfo.getNews().get(count));
                        if (++count == newsTickerInfo.getNews().size()) {
                            count = 0;
                        }
                    }
                }));
    }

    @Override
    public void onStart() {
        super.onStart();
        GSAndroidPlatform.gs().start();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        scrollingNewsSwitcher.setInAnimation(this, android.R.anim.fade_in);
        scrollingNewsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) { // sign up Fragment - Continue with facebook
            fragmentOrganizer.onActivityResult(requestCode, resultCode, data);
        }
        PurchaseModel.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(loginStateReceiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // PurchaseModel.getInstance().onDestroy();
        fragmentOrganizer.freeUpResources();
        disposables.clear();
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
                notificationContainer = (ViewGroup) findViewById(R.id.left_notification_container);

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
        notificationContainer = (ViewGroup) findViewById(R.id.left_notification_container);
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
            case LIKES:
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String postId = event.getPostId();
                        if (postId != null) {
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
    public void onShareNativeEvent(NativeShareEvent event) {
        startActivity(Intent.createChooser(event.getIntent(), getResources().getString(R.string.share)));
    }

    private void initCrashlytics() {
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit);
    }
}