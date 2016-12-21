package tv.sportssidekick.sportssidekick.fragment.instance;


import android.animation.LayoutTransition;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
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
import tv.sportssidekick.sportssidekick.model.im.ImsMessage;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;
import tv.sportssidekick.sportssidekick.service.FullScreenImageEvent;
import tv.sportssidekick.sportssidekick.service.PlayVideoEvent;
import tv.sportssidekick.sportssidekick.service.UIEvent;
import tv.sportssidekick.sportssidekick.util.OnSwipeTouchListener;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChatFragment extends BaseFragment {

    private static final String TAG = "CHAT Fragment";
    @BindView(R.id.message_container) RecyclerView messageListView;
    @BindView(R.id.chat_heads_view) RecyclerView chatHeadsView;
    @BindView(R.id.progress_bar) AVLoadingIndicatorView progressBar;
    @BindView(R.id.input_container) View inputContainer;
    @BindView(R.id.chats_container) RelativeLayout chatHeadsContainer;
    @BindView(R.id.outer_chats_container) RelativeLayout chatHeadsContainerOuter;
    @BindView(R.id.bottom_create_chat_container) RelativeLayout bottomCreateChatContainer;
    @BindView(R.id.info_message) TextView infoMessage;
    @BindView(R.id.down_arrow) ImageView downArrow;
    @BindView(R.id.messenger_send_button) Button sendButton;
    @BindView(R.id.mic_button) Button micButton;
    @BindView(R.id.video_view) VideoView videoView;
    @BindView(R.id.image_fullscreen) ImageView imageViewFullScreen;
    @BindView(R.id.full_screen_container) RelativeLayout fullScreenContainer;
    @BindView(R.id.close_image_button) ImageView imageCloseButton;
    @BindView(R.id.input_edit_text) EditText inputEditText;
    @BindView(R.id.download_image_button) ImageView imageDownloadButton;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    ChatInfo activeChatInfo;

    public ChatFragment() {
        // Required empty public constructor
    }

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
        sendButton.setVisibility(View.GONE);

        imageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreenContainer.setVisibility(View.GONE);
            }
        });

        imageDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Image is downloaded.", Toast.LENGTH_SHORT).show();
            }
        });

        EventBus.getDefault().register(this);
        initializeUI();
      //  isExpanded = false;

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

         //Add textWatcher to notify the user
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Before user enters the text
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //On user changes the text
                if(s.toString().trim().length()==0) {
                    sendButton.setVisibility(View.GONE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                //After user is done entering the text

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImsMessage message = ImsMessage.getDefaultMessage();
                message.setText(inputEditText.getText().toString().trim());
                activeChatInfo.sendMessage(message);
                inputEditText.setText("");
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(activeChatInfo!=null){
                    activeChatInfo.loadPreviouseMessagesPage();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        return view;
    }

    private void hideGridChats(){
        swipeRefreshLayout.setEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (60 * scale + 0.5f); // 60dp
        chatHeadsContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pixels));
        chatHeadsView.setLayoutManager(layoutManager);
        bottomCreateChatContainer.setVisibility(View.GONE);
        messageListView.setVisibility(View.VISIBLE);
        downArrow.setVisibility(View.VISIBLE);
    }

    private void showGridChats(){
        swipeRefreshLayout.setEnabled(false);
        chatHeadsContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        chatHeadsView.setLayoutManager(layoutManager);
        bottomCreateChatContainer.setVisibility(View.VISIBLE);
        messageListView.setVisibility(View.GONE);
        downArrow.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onChatEventDetected(FirebaseEvent event){
        Log.d(TAG, "event received: " + event.getEventType());
        switch (event.getEventType()){
            case USER_CHAT_DETECTED:
                initializeUI();
                break;
            case PUBLIC_CHAT_DETECTED:
                initializeUI();
                break;
            case NEW_MESSAGE_ADDED:
                messageListView.getAdapter().notifyDataSetChanged();
                int lastMessagePosition = messageListView.getAdapter().getItemCount() == 0 ? 0 : messageListView.getAdapter().getItemCount();
                messageListView.smoothScrollToPosition(lastMessagePosition);
                break;
            case NEXT_PAGE_LOADED:
                swipeRefreshLayout.setRefreshing(false);
                messageListView.getAdapter().notifyDataSetChanged();
                messageListView.smoothScrollToPosition(0);
                break;
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

    @Subscribe
    public void playVideo(PlayVideoEvent event){
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(event.getId()));
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void showImageFullScreen(FullScreenImageEvent event){
        fullScreenContainer.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(event.getId(),imageViewFullScreen);
    }

    private void displayChat(ChatInfo info){
        activeChatInfo = info;
        if(info.getMessages()!=null){
            // Message container initialization
            if(info.getMessages().size()>0){
                swipeRefreshLayout.setEnabled(true);
                messageListView.setVisibility(View.VISIBLE);
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
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        infoMessage.setVisibility(View.VISIBLE);
        messageListView.setVisibility(View.INVISIBLE);
    }
}
