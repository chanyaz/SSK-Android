package tv.sportssidekick.sportssidekick.fragment.instance;


import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import tv.sportssidekick.sportssidekick.util.OnSwipeTouchListener;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChatFragment extends BaseFragment {

    private static final String TAG = "CHAT Fragment";

    private GestureDetectorCompat mDetector;

    public ChatFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.message_container) RecyclerView messageListView;
    @BindView(R.id.chat_heads_view) RecyclerView chatHeadsView;
    @BindView(R.id.progress_bar) AVLoadingIndicatorView progressBar;

    @BindView(R.id.input_container) View inputContainer;
    @BindView(R.id.chats_container) RelativeLayout chatHeadsContainer;
    @BindView(R.id.outer_chats_container) RelativeLayout chatHeadsContainerOuter;
    @BindView(R.id.bottom_create_chat_container) RelativeLayout bottomCreateChatContainer;
    @BindView(R.id.info_message)
    TextView infoMessage;

    @BindView(R.id.down_arrow)
    ImageView downArrow;

    @BindView(R.id.mic_button)
    Button micButton;

    boolean isExpanded;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        inputContainer.setVisibility(View.GONE);
        chatHeadsContainer.setVisibility(View.GONE);
        messageListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        infoMessage.setVisibility(View.GONE);

        EventBus.getDefault().register(this);
        initializeUI();
        isExpanded = false;

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        chatHeadsContainer.setLayoutTransition(layoutTransition);

        downArrow.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeBottom() {
                Log.d(TAG, "onSwipeBottom - showGridChats");
                showGridChats();
            }
        });

        bottomCreateChatContainer.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Log.d(TAG, "onSwipeTop - hideGridChats");
                hideGridChats();
            }
        });
        return view;
    }

    private void hideGridChats(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (60 * scale + 0.5f); // 60dp
        chatHeadsContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pixels));
        chatHeadsView.setLayoutManager(layoutManager);
        bottomCreateChatContainer.setVisibility(View.GONE);
        messageListView.setVisibility(View.VISIBLE);
        downArrow.setVisibility(View.VISIBLE);
        isExpanded = false;
    }

    private void showGridChats(){
        chatHeadsContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        chatHeadsView.setLayoutManager(layoutManager);
        bottomCreateChatContainer.setVisibility(View.VISIBLE);
        messageListView.setVisibility(View.GONE);
        downArrow.setVisibility(View.GONE);
        isExpanded = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onChatEventDetected(FirebaseEvent event){
        if( event.getEventType().equals(FirebaseEvent.Type.USER_CHAT_DETECTED)){
            Log.d(TAG, "USER_CHAT_DETECTED event received.");
            initializeUI();
        } else if (event.getEventType().equals(FirebaseEvent.Type.PUBLIC_CHAT_DETECTED)){
            Log.d(TAG, "PUBLIC_CHAT_DETECTED event received.");
            initializeUI();
        }
    }

    public void initializeUI(){
        Log.d(TAG, "Initialize Chat UI");
        HashMap<String, ChatInfo> allUserChats = ImModel.getInstance().getUserChats();

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
        HashMap<String, ChatInfo> allUserChats = ImModel.getInstance().getUserChats();
        String chatId = event.getId();
        Log.d(TAG, "Displaying Chat with id:" + chatId);
        displayChat(allUserChats.get(chatId));
    }

    private void displayChat(ChatInfo info){
        if(info.getMessages()!=null){
            // Message container initialization
            if(info.getMessages().size()>0){
                infoMessage.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Displaying Chat - message count: " + info.getMessages().size());

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                messageListView.setLayoutManager(layoutManager);

                MessageAdapter messageAdapter = new MessageAdapter(getContext(),info);
                messageListView.setAdapter(messageAdapter);
                messageListView.invalidate();
                return;
            } else {
                Log.d(TAG, "Message array size is 0!");
            }
        } else {
            Log.d(TAG, "Message array is null!");
        }
        infoMessage.setVisibility(View.VISIBLE);

    }
}
