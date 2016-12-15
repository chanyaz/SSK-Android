package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChatHeadsAdapter;
import tv.sportssidekick.sportssidekick.adapter.MessageAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImModel;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;
import tv.sportssidekick.sportssidekick.service.UIEvent;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChatFragment extends BaseFragment {

    private static final String TAG = "CHAT Fragment";
    HashMap<String, ChatInfo> allUserChats;

    public ChatFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.message_container) ListView messageListView;
    @BindView(R.id.chat_heads_view) RecyclerView chatHeadsView;
    @BindView(R.id.progress_bar) AVLoadingIndicatorView progressBar;

    @BindView(R.id.input_container) View inputContainer;
    @BindView(R.id.chats_container) View chatHeadsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        inputContainer.setVisibility(View.GONE);
        chatHeadsContainer.setVisibility(View.GONE);
        messageListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        EventBus.getDefault().register(this);

        initializeUI();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onChatEventDetected(FirebaseEvent event){
        if(
                event.getEventType().equals(FirebaseEvent.Type.USER_CHAT_DETECTED) ||
                event.getEventType().equals(FirebaseEvent.Type.PUBLIC_CHAT_DETECTED)
        ){
            Log.d(TAG, "CHAT_DETECTED event received.");
            initializeUI();
        }
    }

    public void initializeUI(){
        Log.d(TAG, "Initialize Chat UI");
        allUserChats = ImModel.getInstance().getUserChats();

        if(allUserChats.size()==0){
            Log.d(TAG, "There are no chats, leaving...");
            // There are no chats - waiting for them to download.
            return;
        } else {
            Log.d(TAG, "There are " + allUserChats.size() + " in Model!");
        }

        inputContainer.setVisibility(View.VISIBLE);
        chatHeadsContainer.setVisibility(View.VISIBLE);
        messageListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        List<ChatInfo> chats = new ArrayList<>();
        chats.addAll(allUserChats.values());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        chatHeadsView.setLayoutManager(layoutManager);

        ChatHeadsAdapter chatHeadsAdapter = new ChatHeadsAdapter(chats, getContext());
        chatHeadsView.setAdapter(chatHeadsAdapter);

        ChatInfo initialChatInfo = chats.get(0);
        displayChat(initialChatInfo);
    }

    @Subscribe
    public void onUIChatEventDetected(UIEvent event){
        String chatId = event.getId();
        Log.d(TAG, "Displaying Chat with id:" + chatId);
        displayChat(allUserChats.get(chatId));
    }


    private void displayChat(ChatInfo info){
        if(info.getMessages()!=null){
            // Message container initialization
            Log.d(TAG, "Displaying Chat - message count: " + info.getMessages().size());
            MessageAdapter messageAdapter = new MessageAdapter(getContext(),info);
            messageListView.setAdapter(messageAdapter);
            messageListView.invalidate();
        } else {
            // TODO Show "No messages yet!" message
        }

    }

}
