package base.app.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.adapter.FriendsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.friendship.PeopleSearchManager;
import base.app.model.user.UserInfo;
import base.app.util.Utility;

/**
 * Created by Djordje Krutil on 29.3.2017..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class AddFriendFragment extends BaseFragment {

    @BindView(R.id.people_recycler_view)
    RecyclerView people;

    @BindView(R.id.add_friend_name)
    EditText friendName;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.no_result)
    TextView noResultCaption;

    @BindView(R.id.friends_list)
    RelativeLayout listContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.popup_add_friend, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        people.setLayoutManager(layoutManager);

        friendName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s))
                {
                    listContainer.setVisibility(View.GONE);
                }
                else {
                    final FriendsAdapter adapter = new FriendsAdapter(this.getClass());
                    adapter.setInitiatorFragment(this.getClass());
                    people.setAdapter(adapter);
                    String text = s.toString();
                    Task<List<UserInfo>> peopleTask = PeopleSearchManager.getInstance().searchPeople(text,0);
                    peopleTask.addOnCompleteListener(new OnCompleteListener<List<UserInfo>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<UserInfo>> task) {
                            if(task.isSuccessful()){
                                if (task.getResult().size() >0)
                                {
                                    noResultCaption.setVisibility(View.GONE);
                                    people.setVisibility(View.VISIBLE);
                                    listContainer.setVisibility(View.VISIBLE);
                                    adapter.getValues().addAll(task.getResult());
                                    adapter.notifyDataSetChanged();
                                }
                                else {
                                    noResultCaption.setVisibility(View.VISIBLE);
                                    listContainer.setVisibility(View.VISIBLE);
                                    people.setVisibility(View.GONE);
                                }
                            } else {
                                noResultCaption.setVisibility(View.VISIBLE);
                                listContainer.setVisibility(View.VISIBLE);
                                people.setVisibility(View.GONE);
                            }
                            progressBar.setVisibility(View.GONE);
//                            if (view != null)
//                            {
//                                Utility.hideKeyboardFrom(getContext(), view);
//                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    @OnClick(R.id.invite_friend_button)
    public void onClickInviteFriend() {
        EventBus.getDefault().post(new FragmentEvent(InviteFriendFragment.class));
    }
}
