package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.ui.adapter.stream.FindOfficialAdapter;
import base.app.ui.adapter.friends.FriendsAdapter;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.ui.BaseFragment;
import base.app.util.events.FragmentEvent;
import base.app.data.user.friends.PeopleSearchManager;
import base.app.data.user.User;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.ui.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;
/**
 * Created by Djordje Krutil on 29.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class AddFriendFragment extends BaseFragment {

    @BindView(R.id.top_buttons_container)
    View topBarContainer;

    RelativeLayout view;

    @BindView(R.id.add_friend_name)
    EditText friendName;

    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.people_recycler_view)
    RecyclerView peopleRecyclerView;

    @Nullable
    @BindView(R.id.official_accounts_recycler_view)
    RecyclerView officialAccountsRecyclerView;

    @BindView(R.id.no_results)
    TextView noResultCaption;

    @Nullable
    FriendsAdapter adapter;

    @Nullable
    FindOfficialAdapter officialAccountsAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_add_friend, container, false);
        ButterKnife.bind(this, view);
        adapter = new FriendsAdapter(this.getClass());
        if (Utility.isTablet(getActivity())) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            peopleRecyclerView.setLayoutManager(layoutManager);
            int screenWidth = Utility.getDisplayWidth(getActivity());
            adapter.screenWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        } else {
            adapter.setLayout(R.layout.row_add_friend_item);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            peopleRecyclerView.setLayoutManager(layoutManager);

        }
        peopleRecyclerView.setAdapter(adapter);
        setupOfficialAccounts();
        setupEditTextListeners();
        this.view = (RelativeLayout) view;
        return view;
    }

    int defaultTopMargin;







    private void setupOfficialAccounts() {
        if(Utility.isPhone(getContext())){
            peopleRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            if(officialAccountsRecyclerView !=null){
                officialAccountsRecyclerView.setLayoutManager(gridLayoutManager);
                officialAccountsAdapter = new FindOfficialAdapter(getActivity(), this.getClass());
                officialAccountsRecyclerView.setAdapter(officialAccountsAdapter);
            }
            Task<List<User>> fetchOfficialAccountsTask = LoginApi.getInstance().getOfficialAccounts(0);
            fetchOfficialAccountsTask.addOnCompleteListener(new OnCompleteListener<List<User>>() {
                @Override
                public void onComplete(@NonNull Task<List<User>> task) {
                    progressBar.setVisibility(View.GONE);
                    List<User> officialAccounts = task.getResult();
                    if (task.isSuccessful() && officialAccounts.size() != 0) {
                        officialAccountsAdapter.setValues(officialAccounts);
                        officialAccountsAdapter.notifyDataSetChanged();
                        toggleOfficialUsersVisibility(true);
                    } else {
                        toggleOfficialUsersVisibility(false);
                    }
                }
            });
        }
    }


    private void setupEditTextListeners() {
            if(Utility.isTablet(getContext())){
                View.OnFocusChangeListener focusChangeListener = Utility.getAdjustResizeFocusListener(getActivity());
                friendName.setOnFocusChangeListener(focusChangeListener);
            }
            friendName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count == 0){
                        isSearchPeopleTaskCanceled = true;
                        toggleOfficialUsersVisibility(true);
                        if(Utility.isTablet(getContext())){
                            onPeopleListChanged(false);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    if(!TextUtils.isEmpty(text)){
                        if(adapter!=null){
                            adapter.getValues().clear();
                            adapter.notifyDataSetChanged();
                        }
                        searchForPeople(text);
                    }
                }
        });
    }

    boolean isSearchPeopleTaskCanceled;


    private void searchForPeople(String text){
        toggleOfficialUsersVisibility(false);
        noResultCaption.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        isSearchPeopleTaskCanceled = false;
        Task<List<User>> searchPeopleTask = PeopleSearchManager.getInstance().searchPeople(text, 0);
        searchPeopleTask.addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
                progressBar.setVisibility(View.GONE);
                if(!isSearchPeopleTaskCanceled){
                    if (task.isSuccessful() && task.getResult().size() != 0) {
                        onPeopleListChanged(true);
                        if(adapter!=null){
                            adapter.setValues(task.getResult());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        onPeopleListChanged(false);
                    }

                }
            }
        });
    }

    private void onPeopleListChanged(boolean hasMembers) {
        if (Utility.isTablet(getActivity())) {
            topBarContainer.setVisibility(hasMembers ? View.GONE : View.VISIBLE);
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
            int desiredMargin;
            if(hasMembers){
                defaultTopMargin = relativeParams.topMargin;
                desiredMargin = 0;
            } else {
                desiredMargin = defaultTopMargin;
            }
            relativeParams.setMargins(relativeParams.leftMargin, desiredMargin, relativeParams.rightMargin, relativeParams.bottomMargin);
            view.setLayoutParams(relativeParams);
        }
        toggleOfficialUsersVisibility(false);
        noResultCaption.setVisibility(hasMembers ? View.GONE : View.VISIBLE);
        peopleRecyclerView.setVisibility(hasMembers ? View.VISIBLE : View.GONE);
    }

    private void toggleOfficialUsersVisibility(boolean visible) {
        if(visible){
            peopleRecyclerView.setVisibility(View.GONE);
            noResultCaption.setVisibility(View.GONE);
            peopleRecyclerView.setVisibility(View.GONE);
        }
        if (officialAccountsRecyclerView != null){
            officialAccountsRecyclerView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Optional
    @OnClick(R.id.your_friends_open_button)
    public void friendsOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FriendsFragment.class));
    }


    @Optional
    @OnClick(R.id.friend_requests)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(FriendRequestsFragment.class));
    }


}
