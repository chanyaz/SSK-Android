package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.SelectableFriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.AlertDialogManager;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.service.AddUsersToCallEvent;
import tv.sportssidekick.sportssidekick.service.StartCallEvent;
import tv.sportssidekick.sportssidekick.util.ui.AutofitDecoration;
import tv.sportssidekick.sportssidekick.util.ui.AutofitRecyclerView;
import tv.sportssidekick.sportssidekick.util.Utility;

import static tv.sportssidekick.sportssidekick.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StartingNewCallFragment extends BaseFragment {

    //friends_recycler_view

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    SelectableFriendsAdapter chatFriendsAdapter;

    boolean addUsersToCall;

    public StartingNewCallFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_new_call, container, false);
        ButterKnife.bind(this, view);

        int screenWidth = Utility.getDisplayWidth(getActivity());

        addUsersToCall = false;

        friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        friendsRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        friendsRecyclerView.setHasFixedSize(true);


        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends(0);
        task.addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        chatFriendsAdapter = new SelectableFriendsAdapter(getContext());
                        List<String> presentUsers = getStringArrayArguement();
                        if (presentUsers != null) {
                            addUsersToCall = true;
                            List<UserInfo> usersToRemove = new ArrayList<>();
                            for (UserInfo userInfo : userInfos) {
                                if (presentUsers.contains(userInfo.getUserId())) {
                                    usersToRemove.add(userInfo);
                                }
                            }
                            userInfos.removeAll(usersToRemove);
                        }
                        chatFriendsAdapter.add(userInfos);
                        friendsRecyclerView.setAdapter(chatFriendsAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                });
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        if (chatFriendsAdapter.getSelectedValues().size() > 0) {
            getActivity().onBackPressed();
            if (addUsersToCall) {
                EventBus.getDefault().post(new AddUsersToCallEvent(chatFriendsAdapter.getSelectedValues()));
            } else {
                EventBus.getDefault().post(new StartCallEvent(chatFriendsAdapter.getSelectedValues()));
            }

        } else {
            AlertDialogManager.getInstance().showAlertDialog("No usesrs selected!", "You havent selected anyone to call!",
                    null,
                    new View.OnClickListener() { // Confirm
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new FragmentEvent(StartingNewCallFragment.class));
                        }
                    });
        }
    }

}
