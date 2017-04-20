package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.FriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.friendship.FriendRequest;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.ui.AutofitDecoration;
import tv.sportssidekick.sportssidekick.util.ui.AutofitRecyclerView;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FriendsFragment extends BaseFragment {

    public static final double GRID_PERCENT_CELL_WIDTH = 0.092;

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id. friend_requests_count)
    TextView friendRequestCount;

    @BindView(R.id.no_result_text)
    TextView noResultText;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_friends, container, false);
        ButterKnife.bind(this, view);

      /*  int screenWidth = Utility.getDisplayWidth(getActivity());
        int cellSize = (int) (screenWidth * 0.092);
        int columns = (screenWidth / (cellSize + (getResources().getDimensionPixelSize(R.dimen.margin_15) * 2)));*/
       // GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
       // friendsRecyclerView.setLayoutManager(layoutManager);
       // friendsRecyclerView.addItemDecoration(new GridItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_20),columns));

        int screenWidth = Utility.getDisplayWidth(getActivity());
        friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));

        friendsRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        friendsRecyclerView.setHasFixedSize(true);


        final FriendsAdapter adapter = new FriendsAdapter(this.getClass());

        adapter.setInitiatorFragment(FriendsFragment.class);
        friendsRecyclerView.setAdapter(adapter);
        Task<List<UserInfo>> friendsTask =  FriendsManager.getInstance().getFriends(0);
        friendsTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserInfo>> task) {
                if(task.isSuccessful()){
                    noResultText.setVisibility(View.GONE);
                    friendsRecyclerView.setVisibility(View.VISIBLE);
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                } else {
                    noResultText.setVisibility(View.VISIBLE);
                    friendsRecyclerView.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        Task<List<FriendRequest>> task = FriendsManager.getInstance().getOpenFriendRequests(0);
        task.addOnCompleteListener(new OnCompleteListener<List<FriendRequest>>() {
            @Override
            public void onComplete(@NonNull Task<List<FriendRequest>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        friendRequestCount.setText(String.valueOf(task.getResult().size()));
                        return;
                    }
                }
                friendRequestCount.setText("0");
            }
        });

        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.close_dialog_button)
    public void closeDialog(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.friend_requests)
    public void friendRequestsDialog(){
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class));
    }

    @OnClick(R.id.add_friend)
    public void  addFriend()
    {
        EventBus.getDefault().post(new FragmentEvent(AddFriendFragment.class));
    }

}
