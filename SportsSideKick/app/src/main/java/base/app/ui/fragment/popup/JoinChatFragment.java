package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import base.app.R;
import base.app.data.chat.ChatInfo;
import base.app.data.chat.ImsManager;
import base.app.data.user.UserInfo;
import base.app.data.user.friends.FriendsManager;
import base.app.ui.adapter.chat.ChatSearchExpandableAdapter;
import base.app.ui.adapter.chat.OfficialChatsAdapter;
import base.app.ui.adapter.friends.FriendsInChatAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Utility;
import base.app.util.ui.AnimatedExpandableListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class JoinChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.bottom_public_chats_friends_in_recycler)
    RecyclerView recyclerViewFriendsIn;

    @BindView(R.id.bottom_public_chats_recycler_container)
    RelativeLayout recyclerViewFriendsInContainer;

    @BindView(R.id.join_chat_search_result_list_view)
    AnimatedExpandableListView recyclerViewSearchResult;

    @BindView(R.id.bottom_public_chats_arrow)
    ImageView bottomContainerArrow;


    @BindView(R.id.chat_name_edit_text)
    EditText searchEditText;
    OfficialChatsAdapter chatsAdapter;

    List<ChatInfo> chatInfos;
    LinearLayoutManager friendsInChatLayoutManager;

    public JoinChatFragment() {
        // Required empty public constructor
    }

    int lastExpandedGroupPosition = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_join, container, false);
        ButterKnife.bind(this, view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        if (Utility.isTablet(getActivity())) {
            recyclerView.getLayoutParams().height = (int) (Utility.getDisplayHeight(getActivity()) * 0.55);
        }
        final int cellHeight = (int) (Utility.getDisplayHeight(getActivity()) * 0.55) / 2;
        friendsInChatLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFriendsIn.setLayoutManager(friendsInChatLayoutManager);
        //endregion

        Task<List<ChatInfo>> taskAllChats = ImsManager.getInstance().getAllPublicChats();
        taskAllChats.addOnCompleteListener(new OnCompleteListener<List<ChatInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatInfo>> task) {
                if (task.isSuccessful()) {
                    setupSearchResultAdapters(task.getResult());
                    searchEditText.addTextChangedListener(textWatcher);
                    setupFriendsChats(task.getResult(), cellHeight);
                }
            }
        });
        chatsAdapter = new OfficialChatsAdapter(cellHeight, this);
        Task<List<ChatInfo>> officialChats = ImsManager.getInstance().getAllOfficialChats();
        officialChats.addOnCompleteListener(new OnCompleteListener<List<ChatInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatInfo>> task) {
                if (task.isSuccessful()) {
                    chatsAdapter.add(task.getResult());
                }
            }
        });

        searchEditText.addTextChangedListener(textWatcher);
        recyclerView.setAdapter(chatsAdapter);
        return view;
    }

    private void setupFriendsChats(final List<ChatInfo> allPublicChats, final int cellHeight) {
        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends(0);
        task.addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        // Other chats that friends are in:
                        List<ChatInfo> otherChats = new ArrayList<>();

                        for (ChatInfo publicChat : allPublicChats) {
                            if (!otherChats.contains(publicChat)) {
                                for (UserInfo friend : userInfos) {
                                    if (publicChat.getUsersIds().contains(friend.getUserId())) {
                                        otherChats.add(publicChat);
                                    }
                                }
                            }
                        }
                        FriendsInChatAdapter friendsInChatAdapter = new FriendsInChatAdapter(getActivity(), recyclerViewFriendsInContainer.getWidth());
                        friendsInChatAdapter.setValues(otherChats);
                        recyclerViewFriendsIn.setAdapter(friendsInChatAdapter);
                    }
                });
    }

    private void setupSearchResultAdapters(List<ChatInfo> chatInfos) {
        ChatSearchExpandableAdapter expandableAdapter = new ChatSearchExpandableAdapter(getActivity(), this, chatInfos);
        recyclerViewSearchResult.setAdapter(expandableAdapter);
        //region Ensure collapse && expand with animation
        recyclerViewSearchResult.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (recyclerViewSearchResult.isGroupExpanded(groupPosition)) {
                    recyclerViewSearchResult.collapseGroupWithAnimation(groupPosition);
                } else {
                    recyclerViewSearchResult.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
        //endregion

        //region Ensure only 1 cell is expanded at time
        recyclerViewSearchResult.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != lastExpandedGroupPosition) {
                    recyclerViewSearchResult.collapseGroup(lastExpandedGroupPosition);
                }
                lastExpandedGroupPosition = groupPosition;
            }
        });
    }

    @OnClick(R.id.create_a_chat)
    public void createChatTextViewOnClick() {
        EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
    }

    @OnClick(R.id.chat_join_search_button)
    public void search() {
        recyclerViewSearchResult.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.backButton)
    public void closeFragment() {
    //    if(Utility.isTablet(getActivity())){
    //        ((LoungeActivity) getActivity()).hideSlidePopupFragmentContainer();
    //    }else {
            getActivity().onBackPressed();
    //    }
    }

    public void performSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final List<ChatInfo> filteredModelList = filter(chatInfos, searchEditText.getText().toString());
                chatsAdapter.replaceAll(filteredModelList);
                recyclerView.scrollToPosition(0);
            }
        });
    }


    TextWatcher textWatcher = new TextWatcher() {
        private final long DELAY = 500; // milliseconds
        private Timer timer = new Timer();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

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

    private static List<ChatInfo> filter(List<ChatInfo> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<ChatInfo> filteredModelList = new ArrayList<>();
        if (models != null) {
            for (ChatInfo model : models) {
                final String text = model.getChatTitle();
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }
}
