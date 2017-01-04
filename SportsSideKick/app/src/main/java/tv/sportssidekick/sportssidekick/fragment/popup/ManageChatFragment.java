package tv.sportssidekick.sportssidekick.fragment.popup;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChatMembersAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
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

    ChatMembersAdapter chatMembersAdapter;
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

        FriendsManager.getInstance().getFriends().addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        chatMembersAdapter = new ChatMembersAdapter(getContext());
                        chatMembersAdapter.add(userInfos);
                        chatMembersAdapter.setChatInfo(chatInfo);
                        recyclerView.setAdapter(chatMembersAdapter);
                    }
                });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        recyclerView.setLayoutManager(layoutManager);

        privateChatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setIsPublicText(!isChecked);
            }
        });

        return view;
    }

    private void setIsPublicText(boolean isPublic) {
        String switchText;
        if(isPublic){
            switchText = getResources().getString(R.string.this_chat_is_public);
        } else {
            switchText = getResources().getString(R.string.this_chat_is_private);
        }
        privateChatTextView.setText(switchText);
    }

    @Override
    public void onResume() {
        super.onResume();
        privateChatSwitch.setChecked(!chatInfo.getIsPublic());
        setIsPublicText(chatInfo.getIsPublic());
        String captionText = String.format(getResources().getString(R.string.manage_public_chat_caption), chatInfo.getChatTitle());
        topCaptionTextView.setText(captionText);
    }

    private void submitChanges(){
        String newChatName = chatNameEditText.getText().toString();
        boolean isPublic = !privateChatSwitch.isChecked();
        boolean shouldUpdate = false;
        if(!TextUtils.isEmpty(newChatName)){ // chat name is changed
            shouldUpdate = true;
            chatInfo.setName(newChatName);
        }
        if(isPublic !=chatInfo.getIsPublic()){ // Chat changed privacy state
            shouldUpdate = true;
            chatInfo.setIsPublic(isPublic);
        }
        if(shouldUpdate){
            chatInfo.updateChatInfo();
        }
        getActivity().onBackPressed();
    }
}
