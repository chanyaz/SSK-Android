package base.app.activity

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.widget.TextView
import base.app.Constant
import base.app.GSAndroidPlatform
import base.app.R
import base.app.events.NotificationReceivedEvent
import base.app.fragment.FragmentEvent
import base.app.fragment.FragmentOrganizer
import base.app.fragment.instance.VideoChatFragment
import base.app.fragment.instance.WallItemFragment
import base.app.fragment.popup.FollowersFragment
import base.app.fragment.popup.FriendsFragment
import base.app.helper.handleStartingIntent
import base.app.model.purchases.PurchaseModel
import base.app.model.sharing.NativeShareEvent
import base.app.model.ticker.NewsTickerInfo
import base.app.model.ticker.NextMatchModel
import base.app.model.ticker.NextMatchUpdateEvent
import base.app.model.user.LoginStateReceiver
import base.app.model.videoChat.VideoChatEvent
import base.app.model.videoChat.VideoChatModel
import base.app.util.ContextWrapper
import base.app.util.Utility
import base.app.util.Utility.CHOSEN_LANGUAGE
import butterknife.BindView
import com.facebook.CallbackManager
import com.facebook.internal.CallbackManagerImpl
import com.pixplicity.easyprefs.library.Prefs
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    protected var fragmentOrganizer: FragmentOrganizer? = null
    protected var loginStateReceiver: LoginStateReceiver? = null
    private lateinit var callbackManager: CallbackManager

    var newsTimer: Timer? = null
    var count: Int = 0

    @BindView(R.id.scrolling_news_title)
    var newsLabel: TextView? = null
    @BindView(R.id.caption)
    var captionLabel: TextView? = null

    /** Apply custom font */
    override fun attachBaseContext(newBase: Context) {
        val language = Prefs.getString(CHOSEN_LANGUAGE, "en")
        val base = ContextWrapper.wrap(newBase, Locale(language))
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        makeStatusBarTransparent()
    }

    private fun makeStatusBarTransparent() {
        if (SDK_INT >= KITKAT) {
            window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onResume() {
        super.onResume()
        NextMatchModel.getInstance().getNextMatchInfo()
        handleStartingIntent(intent)
    }

    @Subscribe
    fun onTickerUpdate(newsTickerInfo: NewsTickerInfo) {
        newsLabel!!.text = newsTickerInfo.news[0]
        captionLabel!!.text = newsTickerInfo.title
        startNewsTimer(newsTickerInfo, newsLabel!!)
    }


    protected fun startNewsTimer(newsTickerInfo: NewsTickerInfo, newsLabel: TextView) {
        count = 0
        if (newsTimer != null) {
            newsTimer!!.cancel()
        }
        newsTimer = Timer()
        newsTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    // update next match info
                    EventBus.getDefault().post(NextMatchUpdateEvent())
                    // update news label
                    newsLabel.text = newsTickerInfo.news[count]
                    if (++count == newsTickerInfo.news.size) {
                        count = 0
                    }
                }
            }
        }, 0, Constant.LOGIN_TEXT_TIME.toLong())

    }

    public override fun onStart() {
        super.onStart()
        GSAndroidPlatform.gs().start()

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Share.toRequestCode()) {// share to facebook
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) { // sign up Fragment - Continue with facebook
            fragmentOrganizer!!.onActivityResult(requestCode, resultCode, data)
        }
        PurchaseModel.getInstance().onActivityResult(requestCode, resultCode, data)
    }

    public override fun onDestroy() {
        fragmentOrganizer!!.freeUpResources()
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().unregister(loginStateReceiver)
        PurchaseModel.getInstance().onDestroy()
        super.onDestroy()
    }

    @Subscribe
    fun onVideoChatEvent(event: VideoChatEvent) {
        val currentFragment = fragmentOrganizer!!.currentFragment
        if (currentFragment !is VideoChatFragment && event.type == VideoChatEvent.Type.onSelfInvited) {
            EventBus.getDefault().post(FragmentEvent(VideoChatFragment::class.java))
            VideoChatModel.getInstance().videoChatEvent = event
        }
    }

    @Subscribe
    fun onNewNotification(event: NotificationReceivedEvent) {
        val vi = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = vi.inflate(R.layout.notification_row, null)

        val title = v.findViewById<TextView>(R.id.notification_title)
        title.text = event.title

        val description = v.findViewById<TextView>(R.id.notification_description)
        description.text = event.description

        when (event.type) {
            NotificationReceivedEvent.Type.FRIEND_REQUESTS -> v.setOnClickListener { EventBus.getDefault().post(FragmentEvent(FriendsFragment::class.java)) }
            NotificationReceivedEvent.Type.FOLLOWERS -> v.setOnClickListener {
                if (Utility.isTablet(applicationContext)) {
                    EventBus.getDefault().post(FragmentEvent(FollowersFragment::class.java))
                }
            }
            NotificationReceivedEvent.Type.LIKES -> v.setOnClickListener {
                val postId = event.postId
                if (postId != null) {
                    val fe = FragmentEvent(WallItemFragment::class.java)
                    fe.id = postId
                    EventBus.getDefault().post(fe)
                }
            }
        }
        // TODO: Show notification system using Notification Manager
    }

    @Subscribe
    fun onShareOnTwitterEvent(event: TweetComposer.Builder) {
        event.show()
    }

    @Subscribe
    fun onShareNativeEvent(event: NativeShareEvent) {
        startActivity(Intent.createChooser(event.intent, resources.getString(R.string.share_using)))
    }

    companion object {
        val TAG = "Base Activity"
    }
}