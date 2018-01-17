package base.app.ui.fragment.content.wall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

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
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.UserInfo;
import base.app.data.wall.Post;
import base.app.data.wall.WallItem;
import base.app.data.wall.WallModel;
import base.app.ui.adapter.content.WallAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.IgnoreBackHandling;
import base.app.ui.fragment.popup.post.PostCreateFragment;
import base.app.ui.fragment.popup.LoginFragment;
import base.app.ui.fragment.popup.SignUpFragment;
import base.app.ui.fragment.popup.SignUpLoginFragment;
import base.app.util.commons.NextMatchCountdown;
import base.app.util.events.comment.CommentUpdateEvent;
import base.app.util.events.post.ItemUpdateEvent;
import base.app.util.events.post.PostDeletedEvent;
import base.app.util.events.post.WallLikeUpdateEvent;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
@IgnoreBackHandling
public class WallFragment extends BaseFragment implements LoginStateReceiver.LoginListener {

    WallAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.wall_top_image_container)
    RelativeLayout nextMatchContainer;
    @BindView(R.id.headerImage)
    ImageView headerImage;
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
    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;
    @Nullable
    @BindView(R.id.login_holder)
    LinearLayout loginHolder;
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;

    List<WallItem> wallItems;
    private LoginStateReceiver loginStateReceiver;

    int offset = 0;
    int pageSize = 30;
    boolean fetchingPageOfPosts = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);

        loginStateReceiver = new LoginStateReceiver(this);

        wallItems = new ArrayList<>();
        wallItems.addAll(WallItem.getCache().values());

        adapter = new WallAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        refreshAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (!fetchingPageOfPosts) {
                    fetchingPageOfPosts = true;
                    TaskCompletionSource<List<WallItem>> competition = new TaskCompletionSource<>();
                    competition.getTask().addOnCompleteListener(new OnCompleteListener<List<WallItem>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<WallItem>> task) {
                            fetchingPageOfPosts = false;
                        }
                    });
                    loadWallItemsPage(false, competition);
                }
            }
        });
        if (wallItems.size() > 0) {
            progressBar.setVisibility(View.GONE);
        }
        Glide.with(view).load(R.drawable.header_background).into(headerImage);

        showNextMatchInfo();

        scrollUp();
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

    private void showNextMatchInfo() {
        if (NextMatchModel.getInstance().isNextMatchUpcoming()) {
            NewsTickerInfo newsTickerInfo = NextMatchModel.getInstance().getTickerInfo();
            ImageLoader.displayImage(newsTickerInfo.getFirstClubUrl(), wallLeftTeamImage, null);
            ImageLoader.displayImage(newsTickerInfo.getSecondClubUrl(), wallRightTeamImage, null);
            wallLeftTeamName.setText(newsTickerInfo.getFirstClubName());
            wallRightTeamName.setText(newsTickerInfo.getSecondClubName());
            long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());
            wallTeamTime.setText(NextMatchCountdown.getTextValue(getContext(), timestamp, false));
        } else {
            wallTopInfoContainer.setVisibility(View.GONE);
            topCaption.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.postButton)
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
        final WallItem post = event.getPost();
        for (int i = 0; i < wallItems.size(); i++) {
            WallItem item = wallItems.get(i);
            if (item.getWallId().equals(post.getWallId()) &&
                    item.getPostId().equals(post.getPostId())) {
                wallItems.remove(item);
                wallItems.add(i, post);
                refreshAdapter();
                return;
            }
        }
        if (post.getPoster() == null && post instanceof Post) {
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
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onUpdateLikeCount(WallLikeUpdateEvent event) {
        if (event.getWallId() != null) {
            for (WallItem item : wallItems) {
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
    public void onUpdateComment(CommentUpdateEvent event) {
        if (event.getWallItem() != null) {
            for (WallItem item : wallItems) {
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
        adapter.clear();
        Collections.sort(wallItems, new Comparator<WallItem>() {
            @Override
            public int compare(WallItem t1, WallItem t2) {
                return Double.compare(t2.getTimestamp(), t1.getTimestamp());
            }
        });
        adapter.addAll(wallItems);
        adapter.notifyDataSetChanged();

        scrollUp(); // TODO: Do this only on startup, logout or login
    }

    @Subscribe
    public void onPostDeleted(PostDeletedEvent event) {
        WallItem deletedItem = event.getPost();
        WallItem itemToDelete = null;
        for (WallItem post : wallItems) {
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
        wallItems.clear();
        adapter.notifyDataSetChanged();

        offset = 0;
        fetchingPageOfPosts = true;
        final TaskCompletionSource<List<WallItem>> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<List<WallItem>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallItem>> task) {
                fetchingPageOfPosts = false;
            }
        });
        loadWallItemsPage(true, source);
    }

    private void loadWallItemsPage(final boolean withSpinner, final TaskCompletionSource<List<WallItem>> completion) {
        if (withSpinner) {
            progressBar.setVisibility(View.VISIBLE);
        }
        TaskCompletionSource<List<WallItem>> getWallPostCompletion = new TaskCompletionSource<>();
        getWallPostCompletion.getTask().addOnCompleteListener(new OnCompleteListener<List<WallItem>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallItem>> task) {
                if (task.isSuccessful()) {
                    List<WallItem> items = task.getResult();
                    wallItems.addAll(items);
                    completion.setResult(items);
                    refreshAdapter();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    offset += pageSize;
                }
                if (withSpinner) {
                    // Cache news for pinning
                    NewsModel.getInstance().loadPage(NewsType.OFFICIAL);
                    NewsModel.getInstance().loadPage(NewsType.UNOFFICIAL);
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
    public void onResume() {
        super.onResume();
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
        reloadWallFromModel();
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
}
