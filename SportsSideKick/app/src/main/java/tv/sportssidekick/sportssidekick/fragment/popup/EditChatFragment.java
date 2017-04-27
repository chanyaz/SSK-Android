package tv.sportssidekick.sportssidekick.fragment.popup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.activity.LoungeActivity;
import tv.sportssidekick.sportssidekick.adapter.AddFriendsAdapter;
import tv.sportssidekick.sportssidekick.adapter.SelectableFriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.model.user.AddFriendsEvent;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.AutofitDecoration;
import tv.sportssidekick.sportssidekick.util.ui.AutofitRecyclerView;
import tv.sportssidekick.sportssidekick.util.ui.LinearItemSpacing;

import static tv.sportssidekick.sportssidekick.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class EditChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.chat_name_edit_text)
    EditText chatNameEditText;
    @BindView(R.id.join_a_chat)
    TextView joinChatTextView;
    @BindView(R.id.edit_caption_label)
    TextView addFriendsInChatLabel;
    @BindView(R.id.search_edit_text)
    EditText searchEditText;

    SelectableFriendsAdapter chatFriendsAdapter;

    @BindView(R.id.members_recycler_view)
    RecyclerView membersRecyclerView;

    @BindView(R.id.friends_in_chat_headline)
    TextView headlineFriendsInChat;

    List<UserInfo> userInfoList;
    AddFriendsAdapter addFriendsAdapter;

    ChatInfo chatInfo;

    public EditChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_edit_chat, container, false);
        ButterKnife.bind(this, view);

        String chatId = getPrimaryArgument();
        chatInfo = ImsManager.getInstance().getChatInfoById(chatId);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChanges();
            }
        });

        joinChatTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new FragmentEvent(JoinChatFragment.class));
                    }
                });

        int screenWidth = Utility.getDisplayWidth(getActivity());

        friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        friendsRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        friendsRecyclerView.setHasFixedSize(true);

        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends(0);
        task.addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        chatFriendsAdapter = new SelectableFriendsAdapter(getContext());
                        chatFriendsAdapter.add(userInfos);
                        userInfoList = userInfos;
                        friendsRecyclerView.setAdapter(chatFriendsAdapter);
                    }
                });

        List<UserInfo> chatMembers = Model.getInstance().getCachedUserInfoById(chatInfo.getUsersIds());
        chatFriendsAdapter.setSelectedUsers(chatMembers);

        searchEditText.addTextChangedListener(textWatcher);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        membersRecyclerView.setLayoutManager(linearLayoutManager);
        addFriendsAdapter = new AddFriendsAdapter(getActivity());
        int space = getResources().getDimensionPixelOffset(R.dimen.margin_15);
        membersRecyclerView.addItemDecoration(new LinearItemSpacing(space, true, true));
        membersRecyclerView.setAdapter(addFriendsAdapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        chatNameEditText.setText(chatInfo.getChatTitle());
        String captionText = String.format(getResources().getString(R.string.manage_public_chat_caption), "'" + chatInfo.getChatTitle() +"'");
        addFriendsInChatLabel.setText(captionText);
    }

    @Subscribe
    public void updateAddFriendsAdapter(AddFriendsEvent event) {
        if (event.isRemove()) {
            addFriendsAdapter.remove(event.getUserInfo());
        } else {
            addFriendsAdapter.add(event.getUserInfo());
        }
        int friendCount = addFriendsAdapter.getItemCount();
        String friendsInChat = " Friends in Chat";
        if (friendCount == 0) {
            headlineFriendsInChat.setText(friendsInChat);
        } else if (friendCount == 1) {
            headlineFriendsInChat.setText("1 Friend in Chat");
        } else {
            String friendsTotal = friendCount + friendsInChat;
            headlineFriendsInChat.setText(friendsTotal);
        }
    }


    @OnClick(R.id.popup_image_button)
    public void pickImage() {
        //TODO IMAGE
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity());
        chooseDialog.setTitle("Choose Option");
        chooseDialog.setNegativeButton("Choose from Library", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        chooseDialog.setPositiveButton("Use Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        chooseDialog.show();
    }

    @OnClick(R.id.close)
    public void closeFragment() {
        ((LoungeActivity) getActivity()).hideSlidePopupFragmentContainer();
    }

    public void performSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatFriendsAdapter != null) {
                    final List<UserInfo> filteredModelList = filter(userInfoList, searchEditText.getText().toString());
                    chatFriendsAdapter.replaceAll(filteredModelList);
                    friendsRecyclerView.scrollToPosition(0);
                }
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
        if (models != null) {
            for (UserInfo model : models) {
                final String text = (model.getFirstName() + model.getLastName() + model.getNicName()).toLowerCase();
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    private void submitChanges(){
        List<UserInfo> chatMembers = Model.getInstance().getCachedUserInfoById(chatInfo.getUsersIds());
        List<UserInfo> selectedValues = chatFriendsAdapter.getSelectedValues();

        boolean shouldUpdate = false;

        if(chatMembers.size() == selectedValues.size()){
            for(UserInfo user : chatMembers){
                if(!selectedValues.contains(user)){
                    shouldUpdate = true;
                    break;
                }
            }
        } else {
            shouldUpdate = true;
        }

        if(shouldUpdate){
            ArrayList<String> newMemebersIds = new ArrayList<>();
            for(UserInfo userInfo : selectedValues){
                newMemebersIds.add(userInfo.getUserId());
            }
            chatInfo.setUsersIds(newMemebersIds);
        }

        String newChatName = chatNameEditText.getText().toString();
        if(!TextUtils.isEmpty(newChatName)){ // chat name is changed
            if(!newChatName.equals(chatInfo.getName())){
                chatInfo.setName(newChatName);
                shouldUpdate = true;
            }
        }

        if(shouldUpdate){
            chatInfo.updateChatInfo();
        }
        getActivity().onBackPressed();
    }
}
