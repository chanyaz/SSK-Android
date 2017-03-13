package tv.sportssidekick.sportssidekick.fragment.popup;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChatFriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class CreateChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    RecyclerView friendsRecyclerView;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.chat_name_edit_text)
    EditText chatNameEditText;
    @BindView(R.id.join_a_chat)
    TextView joinChatTextView;
    @BindView(R.id.search_edit_text)
    EditText searchEditText;
    @BindView(R.id.private_chat_switch)
    Switch privateChatSwitch;
    ChatFriendsAdapter chatFriendsAdapter;
    @BindView(R.id.private_chat_label)
    TextView privateChatTextView;
    List<UserInfo> userInfoList;

    public CreateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_create_chat, container, false);
        ButterKnife.bind(this, view);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChat();
            }
        });

        joinChatTextView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new FragmentEvent(JoinChatFragment.class));
                }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        friendsRecyclerView.setLayoutManager(layoutManager);

        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends();
        task.addOnSuccessListener(
            new OnSuccessListener<List<UserInfo>>() {
                @Override
                public void onSuccess(List<UserInfo> userInfos) {
                    chatFriendsAdapter = new ChatFriendsAdapter(getContext());
                    for (int i = 0; i < 20; i++) {
                        UserInfo info = new UserInfo();
                        info.setEqualsTo(userInfos.get(0));
                        info.setUserId("TEST" + i);
                        userInfos.add(info); // for demo!
                    }
                    chatFriendsAdapter.add(userInfos);
                    userInfoList = userInfos;
                    friendsRecyclerView.setAdapter(chatFriendsAdapter);
                }
            });

        searchEditText.addTextChangedListener(textWatcher);

        final Resources res = getResources();
        privateChatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String switchText;
                if(isChecked){
                    switchText = res.getString(R.string.this_chat_is_private);
                } else {
                    switchText = res.getString(R.string.this_chat_is_public);
                }
                privateChatTextView.setText(switchText);
            }
        });

        return view;
    }

    public void performSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run(){
                final List<UserInfo> filteredModelList = filter(userInfoList, searchEditText.getText().toString());
                chatFriendsAdapter.replaceAll(filteredModelList);
                friendsRecyclerView.scrollToPosition(0);
            }
        });
    }


    TextWatcher textWatcher = new TextWatcher() {
        private static final long DELAY = 500; // milliseconds
        private Timer timer = new Timer();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            performSearch();
                        }
                    },
                    DELAY
            );
        }
    };

    private static List<UserInfo> filter(List<UserInfo> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<UserInfo> filteredModelList = new ArrayList<>();
        if(models!=null){
            for (UserInfo model : models) {
                final String text =(model.getFirstName() + model.getLastName() + model.getNicName()).toLowerCase();
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    public void createNewChat(){
        List<UserInfo> selectedUsers = chatFriendsAdapter.getSelectedValues();
        String chatName = chatNameEditText.getText().toString();
        boolean isPrivate = privateChatSwitch.isChecked();

        ChatInfo newChatInfo = new ChatInfo();
        newChatInfo.setOwner(Model.getInstance().getUserInfo().getUserId());
        newChatInfo.setIsPublic(!isPrivate);
        newChatInfo.setName(chatName);
        ArrayList<String> userIds = new ArrayList<>();
        for(UserInfo info : selectedUsers){
            userIds.add(info.getUserId());
        }
        userIds.add(newChatInfo.getOwner());
        newChatInfo.setUsersIds(userIds);

        ImsManager.getInstance().createNewChat(newChatInfo);
        getActivity().onBackPressed();
    }
}
