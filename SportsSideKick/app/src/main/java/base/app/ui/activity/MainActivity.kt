package base.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import base.app.R
import base.app.data.Model
import base.app.data.tutorial.TutorialModel
import base.app.data.user.LoginStateReceiver
import base.app.data.user.UserEvent
import base.app.data.user.UserInfo
import base.app.ui.adapter.menu.MenuAdapter
import base.app.ui.adapter.menu.SideMenuAdapter
import base.app.ui.fragment.base.ActivityEvent
import base.app.ui.fragment.base.BaseFragment
import base.app.ui.fragment.base.FragmentEvent
import base.app.ui.fragment.base.FragmentOrganizer
import base.app.ui.fragment.content.*
import base.app.ui.fragment.other.ChatFragment
import base.app.ui.fragment.other.StatisticsFragment
import base.app.ui.fragment.popup.*
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.ui.fragment.stream.*
import base.app.util.commons.Constant
import base.app.util.commons.Constant.REQUEST_CODE_CHAT_AUDIO_CAPTURE
import base.app.util.commons.SoundEffects
import base.app.util.commons.Utility
import base.app.util.events.chat.AudioRecordedEvent
import base.app.util.ui.ImageLoader
import base.app.util.ui.LinearItemDecoration
import base.app.util.ui.NavigationDrawerItems
import base.app.util.ui.NoScrollRecycler
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class MainActivity : BaseActivityWithPush(),
        LoginStateReceiver.LoginStateListener,
        SideMenuAdapter.IDrawerCloseSideMenu,
        MenuAdapter.IDrawerClose {

    @BindView(R.id.activity_main)
    internal var rootView: View? = null
    @BindView(R.id.bottom_menu_recycler_view)
    internal var sideMenuRecycler: NoScrollRecycler? = null
    @BindView(R.id.logo)
    internal var logoImageView: ImageView? = null
    @BindView(R.id.fragment_popup_holder)
    internal var popupHolder: View? = null
    @BindView(R.id.user_level_progress)
    internal var userLevelProgress: ProgressBar? = null
    @BindView(R.id.your_coins_value)
    internal var yourCoinsValue: TextView? = null
    @BindView(R.id.user_level)
    internal var yourLevel: TextView? = null
    @BindView(R.id.left_top_bar_container)
    internal var barContainer: RelativeLayout? = null
    @BindView(R.id.fragment_tv_container)
    internal var tvContainer: LinearLayout? = null
    @BindView(R.id.fragment_radio_container)
    internal var radioContainer: LinearLayout? = null
    @BindView(R.id.notification_open)
    internal var notificationIcon: ImageView? = null
    @BindView(R.id.friends_open)
    internal var friendsIcon: ImageView? = null

    private lateinit var sideMenuAdapter: SideMenuAdapter
    private lateinit var menuAdapter: MenuAdapter
    private var screenWidth: Int = 0

    private lateinit var popupContainerFragments: ArrayList<Class<*>>
    private lateinit var popupLeftFragments: ArrayList<Class<*>>
    private lateinit var popupDialogFragments: ArrayList<Class<*>>
    private lateinit var youtubePlayer: ArrayList<Class<*>>
    private lateinit var youtubeList: ArrayList<Class<*>>
    private lateinit var radioList: ArrayList<Class<*>>
    private lateinit var radioPlayerList: ArrayList<Class<*>>

    @OnClick(R.id.notification_open)
    fun notificationOpen() {
    }

    @OnClick(R.id.friends_open)
    fun friendsOpen() {
        EventBus.getDefault().post(FragmentEvent(FriendsFragment::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        loginStateReceiver = LoginStateReceiver(this)

        setupFragments()
        setToolbar()
        updateTopBar()

        Glide.with(this).load(R.drawable.sportingportugal).into(logoImageView!!)
        Glide.with(this).load(R.drawable.video_chat_background).into(splashBackgroundImage!!)
    }

    fun updateTopBar() {
        val visibility = if (Model.getInstance().isRealUser) View.VISIBLE else View.GONE
        friendsIcon!!.visibility = visibility
        notificationIcon!!.visibility = visibility
    }

    private fun setToolbar() {
        NavigationDrawerItems.getInstance().generateList(1)
        menuAdapter = MenuAdapter(this, this)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val displaymetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displaymetrics)
        screenWidth = (displaymetrics.widthPixels * 0.5).toInt()
        sideMenuAdapter = SideMenuAdapter(this, this)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sideMenuRecycler!!.layoutManager = layoutManager
        sideMenuRecycler!!.adapter = sideMenuAdapter
        drawerContainer!!.layoutParams = RelativeLayout.LayoutParams(screenWidth, DrawerLayout.LayoutParams.MATCH_PARENT)
        val gridLayoutManager = GridLayoutManager(this, 2)
        var space = Utility.getDisplayWidth(this).toDouble()
        space = space * 0.005
        menuRecyclerView!!.addItemDecoration(LinearItemDecoration(space.toInt(), true, false))
        menuRecyclerView!!.layoutManager = gridLayoutManager
        menuRecyclerView!!.adapter = menuAdapter

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    private fun setupFragments() {
        fragmentOrganizer = FragmentOrganizer(supportFragmentManager, WallFragment::class.java)

        val mainContainerFragments = ArrayList<Class<*>>()
        mainContainerFragments.add(WallFragment::class.java)
        mainContainerFragments.add(ChatFragment::class.java)
        mainContainerFragments.add(NewsFragment::class.java)
        mainContainerFragments.add(SocialFragment::class.java)
        mainContainerFragments.add(StatisticsFragment::class.java)
        mainContainerFragments.add(RumoursFragment::class.java)
        mainContainerFragments.add(StoreFragment::class.java)
        mainContainerFragments.add(VideoChatFragment::class.java)
        mainContainerFragments.add(TicketsFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragmentHolder, mainContainerFragments)

        popupContainerFragments = ArrayList()
        popupContainerFragments.add(ProfileFragment::class.java)
        popupContainerFragments.add(StashFragment::class.java)
        popupContainerFragments.add(YourStatementFragment::class.java)
        popupContainerFragments.add(WalletFragment::class.java)
        popupContainerFragments.add(LanguageFragment::class.java)
        popupContainerFragments.add(FriendsFragment::class.java)
        popupContainerFragments.add(FriendRequestsFragment::class.java)
        popupContainerFragments.add(StartingNewCallFragment::class.java)
        popupContainerFragments.add(EditProfileFragment::class.java)
        popupContainerFragments.add(LoginFragment::class.java)
        popupContainerFragments.add(SignUpFragment::class.java)
        popupContainerFragments.add(FriendFragment::class.java)
        popupContainerFragments.add(FollowersFragment::class.java)
        popupContainerFragments.add(FollowingFragment::class.java)
        popupContainerFragments.add(AddFriendFragment::class.java)
        popupContainerFragments.add(InviteFriendFragment::class.java)
        popupContainerFragments.add(SignUpLoginFragment::class.java)
        popupContainerFragments.add(CreateChatFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragment_popup_holder, popupContainerFragments, true)

        popupDialogFragments = ArrayList()
        popupDialogFragments.add(AlertDialogFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragment_dialog, popupDialogFragments, true)

        popupLeftFragments = ArrayList()
        popupLeftFragments.add(EditChatFragment::class.java)
        popupLeftFragments.add(JoinChatFragment::class.java)
        popupLeftFragments.add(WallItemFragment::class.java)
        popupLeftFragments.add(NewsItemFragment::class.java)
        popupLeftFragments.add(PostCreateFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragmentLeftPopupHolder, popupLeftFragments, true)

        youtubeList = ArrayList()
        youtubeList.add(ClubTVFragment::class.java)
        youtubeList.add(ClubTvPlaylistFragment::class.java)
        youtubeList.add(ClubRadioFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.play_list_holder, youtubeList, false)

        radioList = ArrayList()
        radioList.add(ClubRadioFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.radio_list_holder, radioList, false)

        youtubePlayer = ArrayList()
        youtubePlayer.add(YoutubePlayerFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.youtube_holder, youtubePlayer, true)

        radioPlayerList = ArrayList()
        radioPlayerList.add(ClubRadioStationFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.radio_holder, radioPlayerList, true)
        // FIXME This will trigger sound?
        EventBus.getDefault().post(FragmentEvent(WallFragment::class.java))
    }

    @Subscribe
    fun onActivityEvent(event: ActivityEvent) {
        startActivity(Intent(this, event.type))
    }

    @Subscribe
    fun onFragmentEvent(event: FragmentEvent) {
        // play different sound on fragment transition
        if (event.isReturning) {
            SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER)
        } else {
            SoundEffects.getDefault().playSound(SoundEffects.SUBTLE)
        }

        // stop playing radio when fragment is changed ( TODO Wrong place to do this? )
        if (fragmentOrganizer.currentFragment!!.javaClass == ClubRadioStationFragment::class.java) {
            (fragmentOrganizer.currentFragment as ClubRadioStationFragment).stopPlaying()
        }

        // make tv container show or hidden
        if (youtubeList.contains(event.type) || youtubePlayer.contains(event.type)) {
            tvContainer!!.visibility = View.VISIBLE
        } else {
            tvContainer!!.visibility = View.GONE
        }

        // make radio container show or hidden
        if (radioList.contains(event.type) || radioPlayerList.contains(event.type)) {
            radioContainer!!.visibility = View.VISIBLE
        } else {
            radioContainer!!.visibility = View.GONE
        }

        if (popupLeftFragments.contains(event.type)) {
            // this is popup event - coming from left
            fragmentLeftPopupHolder!!.visibility = View.VISIBLE
            barContainer!!.visibility = View.INVISIBLE
            popupHolder!!.visibility = View.INVISIBLE
        } else if (popupContainerFragments.contains(event.type)) {
            // this is popup event
            fragmentLeftPopupHolder!!.visibility = View.INVISIBLE
            barContainer!!.visibility = View.VISIBLE
            popupHolder!!.visibility = View.VISIBLE
        } else if (popupDialogFragments.contains(event.type)) {
            // this is popup event - Alert Dialog
            if (fragmentLeftPopupHolder!!.visibility == View.VISIBLE) {
                toggleBlur(true, fragmentLeftPopupHolder)
            } else if (popupHolder!!.visibility == View.VISIBLE) {
                //fragmentLeftPopupHolder is show so we need to take photo from that layout
                toggleBlur(true, popupHolder)
            } else {
                //main fragment view
                toggleBlur(true, fragmentHolder)
            }
        } else {
            //main fragments
            fragmentLeftPopupHolder!!.visibility = View.INVISIBLE
            barContainer!!.visibility = View.VISIBLE
            popupHolder!!.visibility = View.INVISIBLE
        }
    }

    fun toggleBlur(visible: Boolean, fragmentView: View?) {
        if (visible) {
            blurredBackground!!.visibility = View.VISIBLE
        } else {
            blurredBackground!!.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
            return
        }
        if (fragmentOrganizer.previousFragment == null) return

        val previousFragment = fragmentOrganizer.previousFragment!!.javaClass
        val penultimateFragment = fragmentOrganizer.penultimateFragment!!.javaClass
        val currentFragment = fragmentOrganizer.currentFragment

        if (currentFragment!!.javaClass == NewsItemFragment::class.java) {
            if (previousFragment == ClubRadioFragment::class.java) {
                val fragment = fragmentOrganizer.currentFragment
                val overlay = fragment!!.view!!.findViewById<View>(R.id.commentInputOverlay)
                if (overlay.visibility == View.VISIBLE) {
                    overlay.visibility = View.GONE
                    return
                }
            }
        }

        toggleBlur(false, null) // hide blurred view;
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER)

        if (currentFragment is FriendFragment) {
            val initiator = (currentFragment as BaseFragment).initiator
            EventBus.getDefault().post(FragmentEvent(initiator, true))
            return
        }

        // Hiding fragments - TODO Instead of popping them from back stack?
        if (popupLeftFragments.contains(previousFragment)) {
            fragmentLeftPopupHolder!!.visibility = View.VISIBLE
        } else {
            fragmentLeftPopupHolder!!.visibility = View.INVISIBLE
        }

        if (previousFragment == YoutubePlayerFragment::class.java) {
            fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
        }

        // Hiding terms on Sign up fragment - TODO Should be handled on fragment itself
        if (currentFragment is SignUpFragment) {
            val termsHolder = currentFragment.termsHolder
            if (termsHolder.visibility == View.VISIBLE) {
                termsHolder.visibility = View.INVISIBLE
                return
            }
        }

        // make tv container show or hidden
        if (youtubeList.contains(previousFragment) || youtubePlayer.contains(previousFragment)) {
            tvContainer!!.visibility = View.VISIBLE
        } else {
            tvContainer!!.visibility = View.GONE
        }

        // make radio container show or hidden
        if (radioList.contains(previousFragment) || radioPlayerList.contains(previousFragment)) {
            radioContainer!!.visibility = View.VISIBLE
        } else {
            radioContainer!!.visibility = View.GONE
        }

        if (previousFragment == ClubRadioStationFragment::class.java && penultimateFragment == ClubRadioFragment::class.java) {
            NavigationDrawerItems.getInstance().setByPosition(5)
            fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
            menuAdapter.notifyDataSetChanged()
            sideMenuAdapter.notifyDataSetChanged()
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END)
            tvContainer!!.visibility = View.VISIBLE
            return
        }

        if (previousFragment == YoutubePlayerFragment::class.java
                && penultimateFragment == ClubTVFragment::class.java
                && currentFragment is YoutubePlayerFragment) {
            NavigationDrawerItems.getInstance().setByPosition(7)
            menuAdapter.notifyDataSetChanged()
            sideMenuAdapter.notifyDataSetChanged()
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
            tvContainer!!.visibility = View.VISIBLE
            return
        }

        if (currentFragment is YoutubePlayerFragment) {
            val youtubePlayerFragment = currentFragment as YoutubePlayerFragment?
            if (youtubePlayerFragment!!.isFullScreen) {
                youtubePlayerFragment.isFullScreen = false
                return
            }

            if (previousFragment == ClubTVFragment::class.java) {
                fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
                fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
                if (penultimateFragment == ClubRadioStationFragment::class.java) {
                    radioContainer!!.visibility = View.VISIBLE
                    NavigationDrawerItems.getInstance().setByPosition(5)
                    menuAdapter.notifyDataSetChanged()
                    sideMenuAdapter.notifyDataSetChanged()
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                    return
                }
                for (i in Constant.PHONE_MENU_OPTIONS.indices) {
                    if (penultimateFragment == Constant.PHONE_MENU_OPTIONS[i]) {
                        NavigationDrawerItems.getInstance().setByPosition(i)
                        menuAdapter.notifyDataSetChanged()
                        sideMenuAdapter.notifyDataSetChanged()
                        return
                    }
                }
            }
        }

        if (currentFragment.javaClass == ClubRadioStationFragment::class.java) {
            if (previousFragment == ClubRadioFragment::class.java) {
                fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
                fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
                if (penultimateFragment == YoutubePlayerFragment::class.java) {
                    tvContainer!!.visibility = View.VISIBLE
                    NavigationDrawerItems.getInstance().setByPosition(7)
                    menuAdapter.notifyDataSetChanged()
                    sideMenuAdapter.notifyDataSetChanged()
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                }
                for (i in Constant.PHONE_MENU_OPTIONS.indices) {
                    if (penultimateFragment == Constant.PHONE_MENU_OPTIONS[i]) {
                        NavigationDrawerItems.getInstance().setByPosition(i)
                        menuAdapter.notifyDataSetChanged()
                        sideMenuAdapter.notifyDataSetChanged()
                        return
                    }
                }
            }
        }

        if (previousFragment == SignUpLoginFragment::class.java) {
            //EventBus.getDefault().createPost(new FragmentEvent(WallFragment.class, true));
            return
        }
        if (popupHolder!!.visibility == View.VISIBLE) {
            popupHolder!!.visibility = View.INVISIBLE
        }
        if (barContainer!!.visibility != View.VISIBLE) {
            barContainer!!.visibility = View.VISIBLE
        }
        if (fragmentOrganizer.handleNavigationFragment()) {
            menuAdapter.notifyDataSetChanged()
            sideMenuAdapter.notifyDataSetChanged()
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        } else {
            finish()
        }
    }

    private fun setYourCoinsValue(value: String) {
        yourCoinsValue!!.text = value
    }

    fun onProfileClicked(view: View) {
        drawerLayout.closeDrawer(GravityCompat.END)
        if (Model.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(ProfileFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }

    override fun onLoginError(error: Error) {}

    @Subscribe
    fun updateUserName(event: UserEvent) {
        if (event.type == UserEvent.Type.onDetailsUpdated) {
            setYourCoinsValue(Model.getInstance().userInfo.currency.toString())
        }

    }

    override fun closeDrawerMenu(position: Int, goodPosition: Boolean) {
        if (goodPosition) {
            NavigationDrawerItems.getInstance().setByPosition(position)
            drawerLayout.closeDrawer(GravityCompat.END)
            sideMenuAdapter.notifyDataSetChanged()
            if (position > 3) {
                menuAdapter.notifyItemRangeChanged(0, 3)
            }
        } else {
            drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    override fun closeDrawerSideMenu(position: Int, openDrawer: Boolean) {
        if (openDrawer) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            drawerLayout.openDrawer(GravityCompat.END)
        } else {
            NavigationDrawerItems.getInstance().setByPosition(position)
            menuAdapter.notifyDataSetChanged()
        }
    }

    override fun onLogout() {
        resetUserDetails()
        updateTopBar()
    }

    override fun onLoginAnonymously() {
        resetUserDetails()
        updateTopBar()
        splash!!.visibility = View.GONE
    }

    override fun onLogin(user: UserInfo) {
        if (Model.getInstance().isRealUser) {
            val imgUri = "drawable://" + resources.getIdentifier("blank_profile_rounded", "drawable", this.packageName)
            if (user.circularAvatarUrl != null) {
                ImageLoader.displayRoundImage(user.circularAvatarUrl, profileImage)
            }
            setYourCoinsValue(Model.getInstance().userInfo.currency.toString())
            yourLevel!!.visibility = View.VISIBLE
            yourLevel!!.text = user.progress.toInt().toString()
            userLevelBackground!!.visibility = View.VISIBLE
            userLevelProgress!!.visibility = View.VISIBLE
            userLevelProgress!!.progress = (user.progress * userLevelProgress!!.max).toInt()
            TutorialModel.getInstance().userId = Model.getInstance().userInfo.userId
        } else {
            resetUserDetails()
        }
        updateTopBar()
        splash!!.visibility = View.GONE
    }

    private fun resetUserDetails() {
        setYourCoinsValue(0.toString())
        yourLevel!!.visibility = View.INVISIBLE
        userLevelBackground!!.visibility = View.INVISIBLE
        userLevelProgress!!.visibility = View.INVISIBLE
        val imgUri = "drawable://" + resources.getIdentifier("blank_profile_rounded", "drawable", this.packageName)
        ImageLoader.displayImage(imgUri, profileImage, R.drawable.blank_profile_rounded)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHAT_AUDIO_CAPTURE) {
            EventBus.getDefault().post(AudioRecordedEvent())
        }
    }

    companion object {

        val TAG = "Lounge Activity"
    }
}