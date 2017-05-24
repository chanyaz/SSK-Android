package base.app.fragment.popup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.adapter.SelectableFriendsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.AlertDialogManager;
import base.app.model.friendship.FriendsManager;
import base.app.model.user.UserInfo;
import base.app.events.AddUsersToCallEvent;
import base.app.events.StartCallEvent;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import base.app.util.Utility;

import static base.app.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Djordje on 1/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class StartingNewCallFragment extends BaseFragment {
    private static final String TAG = "Start New Call Fragment";

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.confirm_button)
    ImageButton confirmButton;

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
        confirmButton.setEnabled(false);

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
                        confirmButton.setEnabled(true);
                    }
                });
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        if(chatFriendsAdapter!=null){
            if (chatFriendsAdapter.getSelectedValues().size() > 0) {
                getActivity().onBackPressed();
                if (addUsersToCall) {
                    EventBus.getDefault().post(new AddUsersToCallEvent(chatFriendsAdapter.getSelectedValues()));
                } else {
                    EventBus.getDefault().post(new StartCallEvent(chatFriendsAdapter.getSelectedValues()));
                }

            } else {
                AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.no_users_selected), getContext().getResources().getString(R.string.no_users_selected_message),
                        null,
                        new View.OnClickListener() { // Confirm
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new FragmentEvent(StartingNewCallFragment.class));
                            }
                        });
            }
        } else {
            Log.e(TAG, "Adapter is null - wait for friends to be loaded!");
        }

    }

}
