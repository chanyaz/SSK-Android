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
import base.app.ui.fragment.popup.RegisterFragment;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.events.FriendsListChangedEvent;
import base.app.data.TypeConverter;
import base.app.data.content.news.NewsModel;
import base.app.data.content.news.NewsModel.NewsType;
import base.app.data.content.wall.nextmatch.NewsTickerInfo;
import base.app.data.content.wall.nextmatch.NextMatchModel;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.User;
import base.app.data.content.wall.FeedItem;
import base.app.data.content.wall.Post;
import base.app.data.content.wall.WallModel;
import base.app.ui.adapter.content.WallAdapter;
import base.app.util.ui.BaseFragment;
import base.app.util.events.FragmentEvent;
import base.app.ui.fragment.base.IgnoreBackHandling;
import base.app.ui.fragment.user.auth.LoginFragment;
import base.app.ui.fragment.popup.SignUpLoginFragment;
import base.app.ui.fragment.popup.post.PostCreateFragment;
import base.app.util.commons.NextMatchCountdown;
import base.app.util.events.CommentUpdateEvent;
import base.app.util.events.ItemUpdateEvent;
import base.app.util.events.PostDeletedEvent;
import base.app.util.events.WallLikeUpdateEvent;
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
public class WallFragmentOld extends BaseFragment implements LoginStateReceiver.LoginListener {

    WallAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.wall_top_image_container)
    RelativeLayout nextMatchContainer;
    @BindView(R.id.headerImage)
    ImageView headerImage;
    @BindView(R.id.wallLeftTeamName)
    TextView wallLeftTeamName;
    @BindView(R.id.wallLeftTeamImage)
    ImageView wallLeftTeamImage;
    @BindView(R.id.wallRightTeamImage)
    ImageView wallRightTeamImage;
    @BindView(R.id.wallRightTeamName)
    TextView wallRightTeamName;
    @BindView(R.id.wallTeamTime)
    TextView wallTeamTime;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.topCaption)
    View topCaption;
    @BindView(R.id.wallTopInfoContainer)
    View wallTopInfoContainer;
    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;
    @BindView(R.id.loginContainer)
    LinearLayout loginContainer;
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;

    List<FeedItem> wallItems;
    private LoginStateReceiver loginStateReceiver;

    boolean fetchingPageOfPosts = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        loginStateReceiver = new LoginStateReceiver(this);

        wallItems = new ArrayList<>();
        wallItems.addAll(TypeConverter.getCache().values());

        adapter = new WallAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        refreshAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (!fetchingPageOfPosts) {
                    fetchingPageOfPosts = true;
                    TaskCompletionSource<List<FeedItem>> competition = new TaskCompletionSource<>();
                    competition.getTask().addOnCompleteListener(new OnCompleteListener<List<FeedItem>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<FeedItem>> task) {
                            fetchingPageOfPosts = false;
                        }
                    });
                    loadFeed(false, competition, LoginApi.getInstance().getUser());
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
        if (LoginApi.getInstance().isLoggedIn()) {
            EventBus.getDefault().post(new FragmentEvent(PostCreateFragment.class));
        } else {
            EventBus.getDefault().post(new FragmentEvent(SignUpLoginFragment.class));
        }
    }

    @Optional
    @OnClick(R.id.registerButton)
    public void joinOnClick() {
        EventBus.getDefault().post(new FragmentEvent(RegisterFragment.class));
    }

    @Optional
    @OnClick(R.id.loginButton)
    public void loginOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @Subscribe
    public void onItemUpdate(ItemUpdateEvent event) {
        final FeedItem item = event.getItem();
        for (int i = 0; i < wallItems.size(); i++) {
            FeedItem wallItem = wallItems.get(i);
            if (item.getWallId().equals(wallItem.getWallId()) &&
                    item.getId().equals(wallItem.getId())) {
                wallItems.remove(wallItem);
                wallItems.add(i, wallItem);
                refreshAdapter();
                return;
            }
        }
        if (item instanceof Post && ((Post) item).getPoster() == null) {
            LoginApi.getInstance().getUserInfoById(item.getWallId())
                    .addOnCompleteListener(new OnCompleteListener<User>() {
                        @Override
                        public void onComplete(@NonNull Task<User> task) {
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
            for (FeedItem item : wallItems) {
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
            for (FeedItem item : wallItems) {
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
        Collections.sort(wallItems, new Comparator<FeedItem>() {
            @Override
            public int compare(FeedItem t1, FeedItem t2) {
                if (t1 == null || t2 == null) {
                    return 0;
                }
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
        for (FeedItem post : wallItems) {
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
        final TaskCompletionSource<List<FeedItem>> callback = new TaskCompletionSource<>();
        callback.getTask().addOnCompleteListener(new OnCompleteListener<List<FeedItem>>() {
            @Override
            public void onComplete(@NonNull Task<List<FeedItem>> task) {
                fetchingPageOfPosts = false;
            }
        });
        loadFeed(true, callback, LoginApi.getInstance().getUser());
    }

    private void loadFeed(final boolean withSpinner, final TaskCompletionSource<List<FeedItem>> completion, User user) {
        if (withSpinner) {
            progressBar.setVisibility(View.VISIBLE);
        }
        TaskCompletionSource<List<FeedItem>> getWallPostCompletion = new TaskCompletionSource<>();
        getWallPostCompletion.getTask().addOnCompleteListener(new OnCompleteListener<List<FeedItem>>() {
            @Override
            public void onComplete(@NonNull Task<List<FeedItem>> task) {
                if (task.isSuccessful()) {
                    List<FeedItem> items = task.getResult();
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
        WallModel.getInstance().loadFeed(getWallPostCompletion, user);
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
        if (loginContainer != null) {
            loginContainer.setVisibility(LoginApi.getInstance().isLoggedIn() ? View.GONE : View.VISIBLE);
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
    public void onLogin(User user) {
        loginContainer.setVisibility(View.GONE);
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLoginAnonymously() {
        loginContainer.setVisibility(View.VISIBLE);
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
