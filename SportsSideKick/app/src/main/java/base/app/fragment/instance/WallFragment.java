package base.app.fragment.instance;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import base.app.R;
import base.app.adapter.WallAdapter;
import base.app.events.CommentUpdateEvent;
import base.app.events.PostDeletedEvent;
import base.app.events.PostUpdateEvent;
import base.app.events.WallLikeUpdateEvent;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.IgnoreBackHandling;
import base.app.fragment.popup.CreatePostFragment;
import base.app.fragment.popup.LoginFragment;
import base.app.fragment.popup.SignUpFragment;
import base.app.fragment.popup.SignUpLoginFragment;
import base.app.model.Model;
import base.app.model.friendship.FriendsListChangedEvent;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.ticker.NextMatchModel;
import base.app.model.ticker.NextMatchUpdateEvent;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserInfo;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallPost;
import base.app.util.NextMatchCountdown;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Filip on 12/5/2016.
 * <p>
 * A simple {@link BaseFragment} subclass.
 */

@IgnoreBackHandling
public class WallFragment extends BaseFragment implements LoginStateReceiver.LoginStateListener {

    private static final String TAG = "WALL FRAGMENT";
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

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.login_holder)
    LinearLayout loginHolder;
    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;

    List<WallBase> wallItems;
    private LoginStateReceiver loginStateReceiver;

    int offset = 0;
    int pageSize = 20;
    boolean fetchingPageOfPosts = false;

    public WallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);
        this.loginStateReceiver = new LoginStateReceiver(this);
        wallItems = new ArrayList<>();

        wallItems.addAll(WallBase.getCache().values());
        WallModel.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), StaggeredGridLayoutManager.VERTICAL,false);
        adapter = new WallAdapter(getActivity());
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            filterPosts();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(!fetchingPageOfPosts) {
                    fetchingPageOfPosts = true;
                    TaskCompletionSource<List<WallBase>> competition = new TaskCompletionSource<>();
                    competition.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<WallBase>> task) {
                            fetchingPageOfPosts = false;
                        }
                    });
                    loadWallItemsPage(false,competition);
                }
            }
        });
        if (wallItems.size() > 0) {
            progressBar.setVisibility(View.GONE);
        }
        Glide.with(view).load(R.drawable.image_wall_background).into(wallTopImage);
        return view;
    }

    /**
     *     Update Match info - this method is for Phone only
     */
    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void updatePhoneNextMatchDisplay(NextMatchUpdateEvent event){
        if (Utility.isPhone(getActivity())) {
            if(NextMatchModel.getInstance().isNextMatchUpcoming()){
                nextMatchContainer.setVisibility(View.VISIBLE);
                NewsTickerInfo newsTickerInfo = NextMatchModel.getInstance().getTickerInfo();
                ImageLoader.getInstance().displayImage(newsTickerInfo.getFirstClubUrl(), wallLeftTeamImage, Utility.getImageOptionsForTicker());
                ImageLoader.getInstance().displayImage(newsTickerInfo.getSecondClubUrl(), wallRightTeamImage, Utility.getImageOptionsForTicker());
                wallLeftTeamName.setText(newsTickerInfo.getFirstClubName());
                wallRightTeamName.setText(newsTickerInfo.getSecondClubName());
                long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());
                wallTeamTime.setText(NextMatchCountdown.getTextValue(getContext(),timestamp,false));
            } else {
                nextMatchContainer.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.fab)
    public void fabOnClick() {
        if (Model.getInstance().isRealUser()){
            EventBus.getDefault().post(new FragmentEvent(CreatePostFragment.class));
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
    public void onPostUpdate(PostUpdateEvent event) {
        final WallBase post = event.getPost();
        if (post != null) {
            Log.d(TAG, "GOT POST with id: " + post.getPostId());
            for (WallBase item : wallItems) {
                if (item.getWallId().equals(post.getWallId()) && item.getPostId().equals(post.getPostId())) {
                    item.setEqualTo(post);
                    filterPosts();
                    return;
                }
            }
            if (post.getPoster() == null && post instanceof WallPost) {
                Model.getInstance().getUserInfoById(post.getWallId()).addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if (task.isSuccessful()) {
                            post.setPoster(task.getResult());
                            wallItems.add(post);
                        }
                        filterPosts();
                    }
                });
            } else {
                wallItems.add(post);
                filterPosts();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onUpdateLikeCount(WallLikeUpdateEvent event) {
        if (event.getWallId() != null) {
            for (WallBase item : wallItems) {
                if (event.getWallId().equals(item.getWallId())
                        && event.getPostId().equals(item.getPostId())) {
                    item.setLikeCount(event.getCount());
                    filterPosts();
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onUpdateComment(CommentUpdateEvent event) {
        if (event.getWallItem() != null) {
            for (WallBase item : wallItems) {
                if (event.getWallItem().getWallId().equals(item.getWallId())
                        && event.getWallItem().getPostId().equals(item.getPostId())) {
                    item.setCommentsCount(event.getWallItem().getCommentsCount());
                    filterPosts();
                    return;
                }
            }
        }
    }

    public void filterPosts() {
        adapter.replaceAll(wallItems);
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPostDeleted(PostDeletedEvent event) {
        WallBase deletedItem = event.getPost();
        WallBase itemToDelete = null;
        for(WallBase post : wallItems){
            if(post.getPostId().equals(deletedItem.getPostId())){
                itemToDelete = post;
            }
        }
        if(itemToDelete!=null){
            wallItems.remove(itemToDelete);
            filterPosts();
        }
    }

    private void reloadWallFromModel() {
        wallItems.clear();
        adapter.notifyDataSetChanged();

        offset = 0;
        fetchingPageOfPosts = true;
        final TaskCompletionSource<List<WallBase>> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallBase>> task) {
                fetchingPageOfPosts = false;
            }
        });
        loadWallItemsPage(true,source);
    }

    private void loadWallItemsPage(boolean withSpinner,  final TaskCompletionSource<List<WallBase>> completion){
        if(withSpinner){
            progressBar.setVisibility(View.VISIBLE);
        }
        TaskCompletionSource<List<WallBase>> getWallPostCompletion = new TaskCompletionSource<>();
        getWallPostCompletion.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallBase>> task) {
                if(task.isSuccessful()){
                    List<WallBase> items = task.getResult();
                    wallItems.addAll(items);
                    completion.setResult(items);
                    filterPosts();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    offset +=pageSize;
                }
            }
        });
        WallModel.getInstance().loadWallPosts(offset,pageSize,null,getWallPostCompletion);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(loginStateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loginHolder!=null) {
            loginHolder.setVisibility(Model.getInstance().isRealUser() ? View.GONE : View.VISIBLE);
        }
    }

    private void reset() {
        WallModel.getInstance().clear();
    }

    @Override
    public void onLogout() {
        reset();
    }

    @Override
    public void onLoginAnonymously() {
        if (!Model.getInstance().isRealUser() && loginHolder!=null) {
            loginHolder.setVisibility(View.VISIBLE);
        }
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().isRealUser() && loginHolder!=null) {
            loginHolder.setVisibility(View.GONE);
        }
        reset();
        reloadWallFromModel();
    }

    @Subscribe
    public void handleFriendListChanged(FriendsListChangedEvent event){
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLoginError(Error error) {
        reset();
    }
}
