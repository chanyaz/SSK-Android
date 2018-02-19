package base.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
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
import base.app.util.ui.show
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class MainActivity : BaseActivityWithPush(),
        LoginStateReceiver.LoginStateListener,
        SideMenuAdapter.IDrawerCloseSideMenu,
        MenuAdapter.IDrawerClose {

    private lateinit var sideMenuAdapter: SideMenuAdapter
    private lateinit var menuAdapter: MenuAdapter
    private var screenWidth: Int = 0

    private val popupContainerFragments: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}
    private val popupLeftFragments: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}
    private val popupDialogFragments: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}
    private val youtubePlayer: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}
    private val youtubeList: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}
    private val radioList: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}
    private val radioPlayerList: ArrayList<Class<*>> by lazy {ArrayList<Class<*>>()}

    @OnClick(R.id.notificationsButton)
    fun notificationOpen() {
    }

    @OnClick(R.id.friendsButton)
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

        logoImageView.show(R.drawable.club_logo)
        splashBackgroundImage.show(R.drawable.video_chat_background)
    }

    private fun updateTopBar() {
        val visibility = if (Model.getInstance().isRealUser) View.VISIBLE else View.GONE
        friendsButton.visibility = visibility
        notificationsButton.visibility = visibility
    }

    private fun setToolbar() {
        NavigationDrawerItems.getInstance().generateList(1)
        menuAdapter = MenuAdapter(this, this)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = (displayMetrics.widthPixels * 0.5).toInt()
        sideMenuAdapter = SideMenuAdapter(this, this)
        bottomNavigationRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = sideMenuAdapter
        }
        drawerContainer.layoutParams = RelativeLayout.LayoutParams(screenWidth, DrawerLayout.LayoutParams.MATCH_PARENT)
        var space = Utility.getDisplayWidth(this).toDouble()
        space *= 0.005
        menuRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(LinearItemDecoration(space.toInt(), true, false))
            adapter = menuAdapter
        }

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

        val mainContainerFragments = listOf(
                WallFragment::class.java,
                ChatFragment::class.java,
                NewsFragment::class.java,
                SocialFragment::class.java,
                StatisticsFragment::class.java,
                RumoursFragment::class.java,
                StoreFragment::class.java,
                VideoChatFragment::class.java,
                TicketsFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragmentHolder, mainContainerFragments)

        popupContainerFragments.addAll(listOf(
                ProfileFragment::class.java,
                StashFragment::class.java,
                YourStatementFragment::class.java,
                WalletFragment::class.java,
                LanguageFragment::class.java,
                FriendsFragment::class.java,
                FriendRequestsFragment::class.java,
                StartingNewCallFragment::class.java,
                EditProfileFragment::class.java,
                LoginFragment::class.java,
                SignUpFragment::class.java,
                FriendFragment::class.java,
                FollowersFragment::class.java,
                FollowingFragment::class.java,
                AddFriendFragment::class.java,
                InviteFriendFragment::class.java,
                SignUpLoginFragment::class.java,
                CreateChatFragment::class.java))
        fragmentOrganizer.setUpContainer(R.id.popupFragmentHolder, popupContainerFragments, true)

        popupDialogFragments.add(AlertDialogFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragment_dialog, popupDialogFragments, true)

        popupLeftFragments.add(EditChatFragment::class.java)
        popupLeftFragments.add(JoinChatFragment::class.java)
        popupLeftFragments.add(WallItemFragment::class.java)
        popupLeftFragments.add(NewsItemFragment::class.java)
        popupLeftFragments.add(PostCreateFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.fragmentLeftPopupHolder, popupLeftFragments, true)

        youtubeList.add(ClubTVFragment::class.java)
        youtubeList.add(ClubTvPlaylistFragment::class.java)
        youtubeList.add(ClubRadioFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.play_list_holder, youtubeList, false)

        radioList.add(ClubRadioFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.radio_list_holder, radioList, false)

        youtubePlayer.add(YoutubePlayerFragment::class.java)
        fragmentOrganizer.setUpContainer(R.id.youtube_holder, youtubePlayer, true)

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
            tvFragmentContainer.visibility = View.VISIBLE
        } else {
            tvFragmentContainer.visibility = View.GONE
        }

        // make radio container show or hidden
        if (radioList.contains(event.type) || radioPlayerList.contains(event.type)) {
            radioFragmentContainer.visibility = View.VISIBLE
        } else {
            radioFragmentContainer.visibility = View.GONE
        }

        if (popupLeftFragments.contains(event.type)) {
            // this is popup event - coming from left
            fragmentLeftPopupHolder.visibility = View.VISIBLE
            leftTopBarContainer.visibility = View.INVISIBLE
            popupFragmentHolder.visibility = View.INVISIBLE
        } else if (popupContainerFragments.contains(event.type)) {
            // this is popup event
            fragmentLeftPopupHolder.visibility = View.INVISIBLE
            leftTopBarContainer.visibility = View.VISIBLE
            popupFragmentHolder.visibility = View.VISIBLE
        } else if (popupDialogFragments.contains(event.type)) {
            // this is popup event - Alert Dialog
            if (fragmentLeftPopupHolder.visibility == View.VISIBLE) {
                toggleBlur(true, fragmentLeftPopupHolder)
            } else if (popupFragmentHolder.visibility == View.VISIBLE) {
                //fragmentLeftPopupHolder is show so we need to take photo from that layout
                toggleBlur(true, popupFragmentHolder)
            } else {
                //main fragment view
                toggleBlur(true, fragmentHolder)
            }
        } else {
            //main fragments
            fragmentLeftPopupHolder.visibility = View.INVISIBLE
            leftTopBarContainer.visibility = View.VISIBLE
            popupFragmentHolder.visibility = View.INVISIBLE
        }
    }

    fun toggleBlur(visible: Boolean, fragmentView: View?) {
        if (visible) {
            blurredBackground.visibility = View.VISIBLE
        } else {
            blurredBackground.visibility = View.GONE
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
            fragmentLeftPopupHolder.visibility = View.VISIBLE
        } else {
            fragmentLeftPopupHolder.visibility = View.INVISIBLE
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
            tvFragmentContainer.visibility = View.VISIBLE
        } else {
            tvFragmentContainer.visibility = View.GONE
        }

        // make radio container show or hidden
        if (radioList.contains(previousFragment) || radioPlayerList.contains(previousFragment)) {
            radioFragmentContainer.visibility = View.VISIBLE
        } else {
            radioFragmentContainer.visibility = View.GONE
        }

        if (previousFragment == ClubRadioStationFragment::class.java && penultimateFragment == ClubRadioFragment::class.java) {
            NavigationDrawerItems.getInstance().setByPosition(5)
            fragmentOrganizer.currentFragment!!.fragmentManager!!.popBackStack()
            menuAdapter.notifyDataSetChanged()
            sideMenuAdapter.notifyDataSetChanged()
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END)
            tvFragmentContainer.visibility = View.VISIBLE
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
            tvFragmentContainer.visibility = View.VISIBLE
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
                    radioFragmentContainer.visibility = View.VISIBLE
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
                    tvFragmentContainer.visibility = View.VISIBLE
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
        if (popupFragmentHolder.visibility == View.VISIBLE) {
            popupFragmentHolder.visibility = View.INVISIBLE
        }
        if (leftTopBarContainer.visibility != View.VISIBLE) {
            leftTopBarContainer.visibility = View.VISIBLE
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
        coinsTextView.text = value
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
        splash.visibility = View.GONE
    }

    override fun onLogin(user: UserInfo) {
        if (Model.getInstance().isRealUser) {
            val imgUri = "drawable://" + resources.getIdentifier("blank_profile_rounded", "drawable", this.packageName)
            if (user.circularAvatarUrl != null) {
                ImageLoader.displayRoundImage(user.circularAvatarUrl, profileImage)
            }
            setYourCoinsValue(Model.getInstance().userInfo.currency.toString())
            userLevelTextView.visibility = View.VISIBLE
            userLevelTextView.text = user.progress.toInt().toString()
            userLevelBackground.visibility = View.VISIBLE
            userLevelProgress.visibility = View.VISIBLE
            userLevelProgress.progress = (user.progress * userLevelProgress.max).toInt()
            TutorialModel.getInstance().userId = Model.getInstance().userInfo.userId
        } else {
            resetUserDetails()
        }
        updateTopBar()
        splash.visibility = View.GONE
    }

    private fun resetUserDetails() {
        setYourCoinsValue(0.toString())
        userLevelTextView.visibility = View.INVISIBLE
        userLevelBackground.visibility = View.INVISIBLE
        userLevelProgress.visibility = View.INVISIBLE
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
        const val TAG = "Lounge Activity"
    }
}