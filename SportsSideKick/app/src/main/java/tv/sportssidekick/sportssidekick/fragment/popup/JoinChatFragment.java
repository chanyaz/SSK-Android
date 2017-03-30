package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.PublicChatsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.util.AutofitDecoration;
import tv.sportssidekick.sportssidekick.util.AutofitRecyclerView;
import tv.sportssidekick.sportssidekick.util.Utility;

import static tv.sportssidekick.sportssidekick.fragment.popup.YourFriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class JoinChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView recyclerView;
    @BindView(R.id.confirm_button)
    Button confirmButton;

    @BindView(R.id.create_a_chat)
    TextView createChatTextView;

    @BindView(R.id.search_edit_text)
    EditText searchEditText;
    PublicChatsAdapter chatsAdapter;

    List<ChatInfo> chatInfos;

    public JoinChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_join_chat, container, false);
        ButterKnife.bind(this, view);

        confirmButton.setOnClickListener(
            new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     joinChat();
                 }
             }
        );

        createChatTextView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
                }
            });

        int screenWidth = Utility.getDisplayWidth(getActivity());

        recyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        recyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        recyclerView.setHasFixedSize(true);

        Task<List<ChatInfo>> task = ImsManager.getInstance().getAllPublicChats();
        task.addOnCompleteListener(new OnCompleteListener<List<ChatInfo>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatInfo>> task) {
                if(task.isSuccessful()){
                    chatsAdapter = new PublicChatsAdapter(getContext());
                    chatsAdapter.add(task.getResult());
                    searchEditText.addTextChangedListener(textWatcher);
                    recyclerView.setAdapter(chatsAdapter);
                }
            }
        });
        return view;
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

    private static List<ChatInfo> filter(List<ChatInfo> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<ChatInfo> filteredModelList = new ArrayList<>();
        if (models != null)
        {
            for (ChatInfo model : models) {
                final String text = model.getChatTitle();
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    public void joinChat(){
        ChatInfo selectedChat = chatsAdapter.getSelectedValue();
        if(selectedChat!=null){
            selectedChat.joinChat();
            getActivity().onBackPressed();
        }
    }
}
