package tv.sportssidekick.sportssidekick.fragment.popup;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChatFriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImModel;

/**
 * Created by Filip on 1/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ManageChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.chat_name_edit_text)
    EditText chatNameEditText;
    @BindView(R.id.private_chat_switch)
    Switch privateChatSwitch;
    @BindView(R.id.top_caption)
    TextView topCaptionTextView;
    @BindView(R.id.private_chat_label)
    TextView privateChatTextView;

    ChatFriendsAdapter chatFriendsAdapter;

    List<UserInfo> membersList;
    List<UserInfo> friendsList;
    ChatInfo chatInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_manage_chat, container, false);
        ButterKnife.bind(this, view);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChanges();
            }
        });

        String chatId = getPrimaryArgument();
        chatInfo = ImModel.getInstance().getChatInfoById(chatId);
        membersList = Model.getInstance().getCachedUserInfoById(chatInfo.getUsersIds().keySet());

        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends();
        task.addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        chatFriendsAdapter = new ChatFriendsAdapter(getContext());
                        chatFriendsAdapter.add(userInfos);
                        friendsList = userInfos;
                        chatFriendsAdapter.add(membersList);
                        recyclerView.setAdapter(chatFriendsAdapter);
                    }
                });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        recyclerView.setLayoutManager(layoutManager);

        chatFriendsAdapter = new ChatFriendsAdapter(getContext());
        chatFriendsAdapter.add(membersList);
        recyclerView.setAdapter(chatFriendsAdapter);

        final Resources res = getResources();
        String captionText = String.format(res.getString(R.string.manage_public_chat_caption), chatInfo.getChatTitle());
        topCaptionTextView.setText(captionText);

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

        privateChatSwitch.setChecked(chatInfo.getIsPublic());
        return view;
    }

    private void submitChanges(){
        List<UserInfo> selectedUsers = chatFriendsAdapter.getSelectedValues();
        String chatName = chatNameEditText.getText().toString();
        boolean isPrivate = privateChatSwitch.isChecked();

        // TODO

        if(!TextUtils.isEmpty(chatName)){
            // new chat name is added // TODO Validation?
        }
    }
}
