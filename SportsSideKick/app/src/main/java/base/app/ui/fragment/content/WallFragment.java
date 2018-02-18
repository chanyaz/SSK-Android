package base.app.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import base.app.R;
import base.app.data.Model;
import base.app.data.friendship.FriendsListChangedEvent;
import base.app.data.news.NewsModel;
import base.app.data.news.NewsModel.NewsType;
import base.app.data.ticker.NewsTickerInfo;
import base.app.data.ticker.NextMatchModel;
import base.app.data.ticker.NextMatchUpdateEvent;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.UserInfo;
import base.app.data.wall.WallBase;
import base.app.data.wall.WallModel;
import base.app.data.wall.WallPost;
import base.app.ui.adapter.content.WallAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.IgnoreBackHandling;
import base.app.ui.fragment.popup.LoginFragment;
import base.app.ui.fragment.popup.SignUpFragment;
import base.app.ui.fragment.popup.SignUpLoginFragment;
import base.app.ui.fragment.popup.post.PostCreateFragment;
import base.app.util.commons.NextMatchCountdown;
import base.app.util.commons.Utility;
import base.app.util.events.comment.CommentReceiveEvent;
import base.app.util.events.post.AutoTranslateEvent;
import base.app.util.events.post.ItemUpdateEvent;
import base.app.util.events.post.PostDeletedEvent;
import base.app.util.events.post.WallLikeUpdateEvent;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
@IgnoreBackHandling
public class WallFragment extends BaseFragment
        implements LoginStateReceiver.LoginStateListener {

    WallAdapter adapter;

    @BindView(R.id.fragment_wall_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.wall_top_image_container)
    RelativeLayout nextMatchContainer;
    @BindView(R.id.wall_top_image)
    ImageView wallTopImage;
    @BindView(R.id.wall_team_left_name)
    TextView wallLeftTeamName;
    @BindView(R.id.wall_team_left_image)
    ImageView wallLeftTeamImage;
    @BindView(R.id.wall_team_right_image)
    ImageView wallRightTeamImage;
    @BindView(R.id.wall_team_right_name)
    TextView wallRightTeamName;
    @BindView(R.id.wall_team_time)
    TextView wallTeamTime;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.topCaption)
    View topCaption;
    @BindView(R.id.wall_top_info_container)
    View wallTopInfoContainer;
    @Nullable
    @BindView(R.id.login_holder)
    LinearLayout loginHolder;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    List<WallBase> wallItems;
    private LoginStateReceiver loginStateReceiver;

    int offset = 0;
    int pageSize = 100;
    boolean fetchingPageOfPosts = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);

        loginStateReceiver = new LoginStateReceiver(this);

        wallItems = new ArrayList<>();
        wallItems.addAll(WallBase.getCache().values());

        adapter = new WallAdapter(getActivity());
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
            refreshAdapter(false);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!fetchingPageOfPosts) {
                    fetchingPageOfPosts = true;
                    TaskCompletionSource<List<WallBase>> competition = new TaskCompletionSource<>();
                    competition.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<WallBase>> task) {
                            fetchingPageOfPosts = false;
                        }
                    });
                    loadWallItemsPage(false, competition);
                }
            }
        });
        swipeRefreshLayout.setProgressViewOffset(false, 0, Utility.dpToPixels(248));
        if (wallItems.size() > 0) {
            swipeRefreshLayout.setRefreshing(false);
        }
        Glide.with(view).load(R.drawable.image_wall_background).into(wallTopImage);

        NextMatchModel.getInstance().getNextMatchInfo();

        return view;
    }

    protected void scrollUp() {
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(0, 0);
                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        recyclerView.smoothScrollToPosition(0);
    }

    @Subscribe
    public void updateNextMatchInfo(NextMatchUpdateEvent event) {
        if (Utility.isPhone(getActivity())) {
            if (NextMatchModel.getInstance().isNextMatchUpcoming()) {
                nextMatchContainer.setVisibility(View.VISIBLE);
                NewsTickerInfo newsTickerInfo = NextMatchModel.getInstance().getTickerInfo();
                ImageLoader.displayImage(newsTickerInfo.getFirstClubUrl(), wallLeftTeamImage);
                ImageLoader.displayImage(newsTickerInfo.getSecondClubUrl(), wallRightTeamImage);
                wallLeftTeamName.setText(newsTickerInfo.getFirstClubName());
                wallRightTeamName.setText(newsTickerInfo.getSecondClubName());
                long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());
                wallTeamTime.setText(NextMatchCountdown.getTextValue(getContext(), timestamp, false));
            } else {
                wallTopInfoContainer.setVisibility(View.GONE);
                topCaption.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.fab)
    public void fabOnClick() {
        if (Model.getInstance().isRealUser()) {
            EventBus.getDefault().post(new FragmentEvent(PostCreateFragment.class));
        } else {
            EventBus.getDefault().post(new FragmentEvent(SignUpLoginFragment.class));
        }
    }

    @Optional
    @OnClick(R.id.join_now_button)
    public void joinOnClick() {
        EventBus.getDefault().post(new FragmentEvent(SignUpFragment.class));
    }

    @Optional
    @OnClick(R.id.login_button)
    public void loginOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @Subscribe
    public void onItemUpdate(ItemUpdateEvent event) {
        final WallBase post = event.getPost();
        for (WallBase item : wallItems) {
            if (item.getWallId().equals(post.getWallId()) &&
                    item.getPostId().equals(post.getPostId())) {
                item.setEqualTo(post);
                refreshAdapter();
                return;
            }
        }
        if (post.getPoster() == null && post instanceof WallPost) {
            Model.getInstance().getUserInfoById(post.getWallId())
                    .addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if (task.isSuccessful()) {
                                post.setPoster(task.getResult());
                                wallItems.add(post);
                            }
                            refreshAdapter();
                        }
                    });
        } else {
            wallItems.add(post);
            refreshAdapter();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onUpdateLikeCount(WallLikeUpdateEvent event) {
        if (event.getWallId() != null) {
            for (WallBase item : wallItems) {
                if (event.getWallId().equals(item.getWallId())
                        && event.getPostId().equals(item.getPostId())) {
                    item.setLikeCount(event.getCount());
                    refreshAdapter();
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onUpdateComment(CommentReceiveEvent event) {
        if (event.getWallItem() != null) {
            for (WallBase item : wallItems) {
                if (event.getWallItem().getWallId().equals(item.getWallId())
                        && event.getWallItem().getPostId().equals(item.getPostId())) {
                    item.setCommentsCount(event.getWallItem().getCommentsCount());
                    refreshAdapter();
                    return;
                }
            }
        }
    }

    public void refreshAdapter() {
        refreshAdapter(true);
    }

    public void refreshAdapter(boolean animateRefresh) {
        if (wallItems.isEmpty()) return;

        if (animateRefresh) {
            int removedItemCount = adapter.getItemCount();
            adapter.clear();
            adapter.notifyItemRangeRemoved(0, removedItemCount);

            Collections.sort(wallItems, new Comparator<WallBase>() {
                @Override
                public int compare(WallBase t1, WallBase t2) {
                    if (t1 == null || t2 == null) return 0;
                    return t2.getTimestamp().compareTo(t1.getTimestamp());
                }
            });
            adapter.addAll(wallItems);
            adapter.notifyItemRangeChanged(0, wallItems.size());
        } else {
            adapter.clear();
            Collections.sort(wallItems, new Comparator<WallBase>() {
                @Override
                public int compare(WallBase t1, WallBase t2) {
                    if (t1 == null || t2 == null) return 0;
                    return t2.getTimestamp().compareTo(t1.getTimestamp());
                }
            });
            adapter.addAll(wallItems);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onPostDeleted(PostDeletedEvent event) {
        WallBase deletedItem = event.getPost();
        WallBase itemToDelete = null;
        for (WallBase post : wallItems) {
            if (post.getPostId().equals(deletedItem.getPostId())) {
                itemToDelete = post;
            }
        }
        if (itemToDelete != null) {
            wallItems.remove(itemToDelete);
            refreshAdapter();
        }
    }

    private void reloadWallFromModel() {
        reloadWallFromModel(true);
    }

    private void reloadWallFromModel(final boolean withSpinner) {
        offset = 0;
        fetchingPageOfPosts = true;
        final TaskCompletionSource<List<WallBase>> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallBase>> task) {
                fetchingPageOfPosts = false;
                if (withSpinner) {
                    scrollUp();
                }
            }
        });
        loadWallItemsPage(withSpinner, source);
    }

    private void loadWallItemsPage(final boolean withSpinner, final TaskCompletionSource<List<WallBase>> completion) {
        if (withSpinner || adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(true);
        }
        TaskCompletionSource<List<WallBase>> getWallPostCompletion = new TaskCompletionSource<>();
        getWallPostCompletion.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallBase>> task) {
                if (task.isSuccessful()) {
                    List<WallBase> items = task.getResult();
                    if (items.size() > 0) {
                        wallItems.clear();
                        wallItems.addAll(items);
                        refreshAdapter(withSpinner);
                    }
                    completion.setResult(items);
                    swipeRefreshLayout.setRefreshing(false);
                    offset += pageSize;
                }
                if (withSpinner) {
                    // Cache news for pinning
                    NewsModel.getInstance().loadPage(NewsType.OFFICIAL);
                    NewsModel.getInstance().loadPage(NewsType.UNOFFICIAL);
                    NewsModel.getInstance().loadPage(NewsType.SOCIAL);
                }
            }
        });
        WallModel.getInstance().loadWallPosts(offset, pageSize, getWallPostCompletion);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(loginStateReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (loginHolder != null) {
            loginHolder.setVisibility(Model.getInstance().isRealUser() ? View.GONE : View.VISIBLE);
        }
    }

    private void reset() {
        WallModel.getInstance().clear();
    }

    @Override
    public void onLogout() {
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().isRealUser() && loginHolder != null) {
            loginHolder.setVisibility(View.GONE);
        }
        reset();
        reloadWallFromModel(false);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onLoginAnonymously() {
        if (!Model.getInstance().isRealUser() && loginHolder != null) {
            loginHolder.setVisibility(View.VISIBLE);
        }
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLoginError(Error error) {
        reset();
    }

    @Subscribe
    public void handleFriendListChanged(FriendsListChangedEvent event) {
        reset();
        reloadWallFromModel();
    }

    @Subscribe
    public void onAutoTranslateToggle(AutoTranslateEvent event) {
        if (event.isEnabled()) {
            // Translate
            adapter.notifyDataSetChanged();
        } else {
            // Undo translation
            refreshAdapter(false);
        }
    }
}
