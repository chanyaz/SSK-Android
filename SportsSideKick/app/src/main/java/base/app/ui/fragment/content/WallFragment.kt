package base.app.ui.fragment.content

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import base.app.R
import base.app.data.Model
import base.app.data.friendship.FriendsListChangedEvent
import base.app.data.news.NewsModel
import base.app.data.news.NewsModel.NewsType
import base.app.data.user.LoginStateReceiver
import base.app.data.user.UserInfo
import base.app.data.wall.WallBase
import base.app.data.wall.WallModel
import base.app.data.wall.WallPost
import base.app.ui.adapter.content.WallAdapter
import base.app.ui.fragment.base.BaseFragment
import base.app.ui.fragment.base.FragmentEvent
import base.app.ui.fragment.base.IgnoreBackHandling
import base.app.ui.fragment.popup.SignUpLoginFragment
import base.app.ui.fragment.popup.post.PostCreateFragment
import base.app.util.commons.Utility
import base.app.util.events.comment.CommentReceiveEvent
import base.app.util.events.post.AutoTranslateEvent
import base.app.util.events.post.ItemUpdateEvent
import base.app.util.events.post.PostDeletedEvent
import base.app.util.events.post.WallLikeUpdateEvent
import base.app.util.ui.LinearItemDecoration
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.tasks.TaskCompletionSource
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

@IgnoreBackHandling
class WallFragment : BaseFragment(), LoginStateReceiver.LoginStateListener {

    internal val adapter: WallAdapter by lazy { WallAdapter(context) }

    @BindView(R.id.fragment_wall_recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.wall_top_image_container)
    lateinit var nextMatchContainer: RelativeLayout
    @BindView(R.id.wall_top_image)
    lateinit var wallTopImage: ImageView
    @BindView(R.id.wall_team_left_name)
    lateinit var wallLeftTeamName: TextView
    @BindView(R.id.wall_team_left_image)
    lateinit var wallLeftTeamImage: ImageView
    @BindView(R.id.wall_team_right_image)
    lateinit var wallRightTeamImage: ImageView
    @BindView(R.id.wall_team_right_name)
    lateinit var wallRightTeamName: TextView
    @BindView(R.id.wall_team_time)
    lateinit var wallTeamTime: TextView
    @BindView(R.id.topCaption)
    lateinit var topCaption: View
    @BindView(R.id.wall_top_info_container)
    lateinit var wallTopInfoContainer: View
    @BindView(R.id.swipeRefreshLayout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    internal var wallItems: MutableList<WallBase> = ArrayList()
    private var loginStateReceiver: LoginStateReceiver? = null

    internal var offset = 0
    internal var pageSize = 100
    internal var fetchingPageOfPosts = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wall, container, false)
        ButterKnife.bind(this, view)

        loginStateReceiver = LoginStateReceiver(this)

        wallItems.addAll(WallBase.getCache().values)

        if (recyclerView != null) {
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            recyclerView!!.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
            val space = resources.getDimension(R.dimen.item_spacing_wall).toInt()
            recyclerView!!.addItemDecoration(LinearItemDecoration(space))

            refreshAdapter(false)
        }
        swipeRefreshLayout!!.setOnRefreshListener {
            if (!fetchingPageOfPosts) {
                fetchingPageOfPosts = true
                val competition = TaskCompletionSource<List<WallBase>>()
                competition.task.addOnCompleteListener { fetchingPageOfPosts = false }
                loadWallItemsPage(false, competition)
            }
        }
        swipeRefreshLayout!!.setProgressViewOffset(false, 0, Utility.dpToPixels(248f))
        if (wallItems.size > 0) {
            swipeRefreshLayout!!.isRefreshing = false
        }

        return view
    }

    protected fun scrollUp() {
        recyclerView!!.smoothScrollToPosition(0)
    }

    @OnClick(R.id.fab)
    fun fabOnClick() {
        if (Model.getInstance().isRealUser) {
            EventBus.getDefault().post(FragmentEvent(PostCreateFragment::class.java))
        } else {
            EventBus.getDefault().post(FragmentEvent(SignUpLoginFragment::class.java))
        }
    }

    @Subscribe
    fun onItemUpdate(event: ItemUpdateEvent) {
        val post = event.post
        for (item in wallItems) {
            if (item.wallId == post.wallId && item.postId == post.postId) {
                item.setEqualTo(post)
                refreshAdapter()
                return
            }
        }
        if (post.poster == null && post is WallPost) {
            Model.getInstance().getUserInfoById(post.getWallId())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            post.setPoster(task.result)
                            wallItems.add(post)
                        }
                        refreshAdapter()
                    }
        } else {
            wallItems.add(post)
            refreshAdapter()
        }
        swipeRefreshLayout!!.isRefreshing = false
    }

    @Subscribe
    fun onUpdateLikeCount(event: WallLikeUpdateEvent) {
        if (event.wallId != null) {
            for (item in wallItems) {
                if (event.wallId == item.wallId && event.postId == item.postId) {
                    item.likeCount = event.count
                    refreshAdapter()
                    return
                }
            }
        }
    }

    @Subscribe
    fun onUpdateComment(event: CommentReceiveEvent) {
        if (event.wallItem != null) {
            for (item in wallItems) {
                if (event.wallItem.wallId == item.wallId && event.wallItem.postId == item.postId) {
                    item.commentsCount = event.wallItem.commentsCount
                    refreshAdapter()
                    return
                }
            }
        }
    }

    @JvmOverloads
    fun refreshAdapter(animateRefresh: Boolean = true) {
        if (wallItems.isEmpty()) return

        if (animateRefresh) {
            val removedItemCount = adapter.itemCount
            adapter.clear()
            adapter.notifyItemRangeRemoved(0, removedItemCount)

            Collections.sort(wallItems) { t1, t2 -> if (t1 == null || t2 == null) 0 else t2.timestamp!!.compareTo(t1.timestamp) }
            adapter.addAll(wallItems)
            adapter.notifyItemRangeChanged(0, wallItems.size)
        } else {
            adapter.clear()
            Collections.sort(wallItems) { t1, t2 -> if (t1 == null || t2 == null) 0 else t2.timestamp!!.compareTo(t1.timestamp) }
            adapter.addAll(wallItems)
            adapter.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun onPostDeleted(event: PostDeletedEvent) {
        val deletedItem = event.post
        var itemToDelete: WallBase? = null
        for (post in wallItems) {
            if (post.postId == deletedItem.postId) {
                itemToDelete = post
            }
        }
        if (itemToDelete != null) {
            wallItems.remove(itemToDelete)
            refreshAdapter()
        }
    }

    private fun reloadWallFromModel(withSpinner: Boolean = true) {
        offset = 0
        fetchingPageOfPosts = true
        val source = TaskCompletionSource<List<WallBase>>()
        source.task.addOnCompleteListener {
            fetchingPageOfPosts = false
            if (withSpinner) {
                scrollUp()
            }
        }
        loadWallItemsPage(withSpinner, source)
    }

    private fun loadWallItemsPage(withSpinner: Boolean, completion: TaskCompletionSource<List<WallBase>>) {
        if (withSpinner || adapter.itemCount == 0) {
            swipeRefreshLayout!!.isRefreshing = true
        }
        val getWallPostCompletion = TaskCompletionSource<List<WallBase>>()
        getWallPostCompletion.task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val items = task.result
                if (items.size > 0) {
                    wallItems.clear()
                    wallItems.addAll(items)
                    refreshAdapter(withSpinner)
                }
                completion.setResult(items)
                swipeRefreshLayout!!.isRefreshing = false
                offset += pageSize
            }
            if (withSpinner) {
                // Cache news for pinning
                NewsModel.getInstance().loadPage(NewsType.OFFICIAL)
                NewsModel.getInstance().loadPage(NewsType.UNOFFICIAL)
                NewsModel.getInstance().loadPage(NewsType.SOCIAL)
            }
        }
        WallModel.getInstance().loadWallPosts(offset, pageSize, getWallPostCompletion)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(loginStateReceiver)
    }

    private fun reset() {
        WallModel.getInstance().clear()
    }

    override fun onLogout() {
        reset()
        reloadWallFromModel()
    }

    override fun onLogin(user: UserInfo) {
        reset()
        reloadWallFromModel(false)
        swipeRefreshLayout!!.isRefreshing = true
    }

    override fun onLoginAnonymously() {
        reset()
        reloadWallFromModel()
    }

    override fun onLoginError(error: Error) {
        reset()
    }

    @Subscribe
    fun handleFriendListChanged(event: FriendsListChangedEvent) {
        reset()
        reloadWallFromModel()
    }

    @Subscribe
    fun onAutoTranslateToggle(event: AutoTranslateEvent) {
        if (event.isEnabled) {
            // Translate
            adapter.notifyDataSetChanged()
        } else {
            // Undo translation
            refreshAdapter(false)
        }
    }
}
