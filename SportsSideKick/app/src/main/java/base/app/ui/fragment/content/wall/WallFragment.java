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

import base.app.R;
import base.app.data.Model;
import base.app.data.TypeMapper;
import base.app.data.friendship.FriendsListChangedEvent;
import base.app.data.news.NewsModel;
import base.app.data.news.NewsModel.NewsType;
import base.app.data.ticker.NewsTickerInfo;
import base.app.data.ticker.NextMatchModel;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.UserInfo;
import base.app.data.wall.BaseItem;
import base.app.data.wall.Post;
import base.app.data.wall.WallModel;
import base.app.ui.adapter.content.WallAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.IgnoreBackHandling;
import base.app.ui.fragment.popup.LoginFragment;
import base.app.ui.fragment.popup.SignUpFragment;
import base.app.ui.fragment.popup.SignUpLoginFragment;
import base.app.ui.fragment.popup.post.PostCreateFragment;
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
    @BindView(R.id.login_holder)
    LinearLayout loginHolder;
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;

    List<BaseItem> wallItems;
    private LoginStateReceiver loginStateReceiver;

    int pageSize = 30;
    boolean fetchingPageOfPosts = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        loginStateReceiver = new LoginStateReceiver(this);

        wallItems = new ArrayList<>();
        wallItems.addAll(TypeMapper.getCache().values());

        adapter = new WallAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        refreshAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (!fetchingPageOfPosts) {
                    fetchingPageOfPosts = true;
                    TaskCompletionSource<List<BaseItem>> competition = new TaskCompletionSource<>();
                    competition.getTask().addOnCompleteListener(new OnCompleteListener<List<BaseItem>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<BaseItem>> task) {
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
        final BaseItem item = event.getItem();
        for (int i = 0; i < wallItems.size(); i++) {
            BaseItem wallItem = wallItems.get(i);
            if (item.getWallId().equals(wallItem.getWallId()) &&
                    item.getId().equals(wallItem.getId())) {
                wallItems.remove(wallItem);
                wallItems.add(i, wallItem);
                refreshAdapter();
                return;
            }
        }
        if (item instanceof Post && ((Post) item).getPoster() == null) {
            Model.getInstance().getUserInfoById(item.getWallId())
                    .addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if (task.isSuccessful()) {
                                ((Post) item).setPoster(task.getResult());
                                wallItems.add(item);
                            }
                            refreshAdapter();
                        }
                    });
        } else {
            wallItems.add(item);
            refreshAdapter();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onUpdateLikeCount(WallLikeUpdateEvent event) {
        if (event.getWallId() != null) {
            for (BaseItem item : wallItems) {
                if (event.getWallId().equals(item.getWallId())
                        && event.getPostId().equals(item.getId())) {
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
            for (BaseItem item : wallItems) {
                if (event.getWallItem().getWallId().equals(item.getWallId())
                        && event.getWallItem().getId().equals(item.getId())) {
                    item.setCommentsCount(event.getWallItem().getCommentsCount());
                    refreshAdapter();
                    return;
                }
            }
        }
    }

    public void refreshAdapter() {
        adapter.clear();
        Collections.sort(wallItems, new Comparator<BaseItem>() {
            @Override
            public int compare(BaseItem t1, BaseItem t2) {
                return Double.compare(t2.getTimestamp(), t1.getTimestamp());
            }
        });
        adapter.addAll(wallItems);
        adapter.notifyDataSetChanged();

        scrollUp(); // TODO: Do this only on startup, logout or login
    }

    @Subscribe
    public void onPostDeleted(PostDeletedEvent event) {
        Post deletedItem = event.getPost();
        for (BaseItem post : wallItems) {
            if (post.getId().equals(deletedItem.getId())) {
                wallItems.remove(post);
                refreshAdapter();
                return;
            }
        }
    }

    private void reloadWallFromModel() {
        wallItems.clear();
        adapter.notifyDataSetChanged();

        fetchingPageOfPosts = true;
        final TaskCompletionSource<List<BaseItem>> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<List<BaseItem>>() {
            @Override
            public void onComplete(@NonNull Task<List<BaseItem>> task) {
                fetchingPageOfPosts = false;
            }
        });
        loadWallItemsPage(true, source);
    }

    private void loadWallItemsPage(final boolean withSpinner, final TaskCompletionSource<List<BaseItem>> completion) {
        if (withSpinner) {
            progressBar.setVisibility(View.VISIBLE);
        }
        TaskCompletionSource<List<BaseItem>> getWallPostCompletion = new TaskCompletionSource<>();
        getWallPostCompletion.getTask().addOnCompleteListener(new OnCompleteListener<List<BaseItem>>() {
            @Override
            public void onComplete(@NonNull Task<List<BaseItem>> task) {
                if (task.isSuccessful()) {
                    List<BaseItem> items = task.getResult();
                    wallItems.addAll(items);
                    completion.setResult(items);
                    refreshAdapter();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                }
                if (withSpinner) {
                    // Cache news for pinning
                    NewsModel.getInstance().loadPage(NewsType.OFFICIAL);
                    NewsModel.getInstance().loadPage(NewsType.UNOFFICIAL);
                }
            }
        });
        WallModel.getInstance().loadWallPosts(getWallPostCompletion);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        loginHolder.setVisibility(View.GONE);
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLoginAnonymously() {
        loginHolder.setVisibility(View.VISIBLE);
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
