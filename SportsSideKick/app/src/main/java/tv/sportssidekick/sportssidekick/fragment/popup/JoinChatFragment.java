package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
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
import tv.sportssidekick.sportssidekick.model.im.ImModel;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class JoinChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    RecyclerView recyclerView;
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

        confirmButton.setOnClickListener(v -> {
            joinChat();
        });

        createChatTextView.setOnClickListener(v -> {
            EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class));
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        recyclerView.setLayoutManager(layoutManager);

        chatsAdapter = new PublicChatsAdapter(getContext());
        chatsAdapter.add(ImModel.getInstance().getNonMemberPublicChatsInfo());
        searchEditText.addTextChangedListener(textWatcher);
        recyclerView.setAdapter(chatsAdapter);

        return view;
    }

    public void performSearch() {
        getActivity().runOnUiThread(() -> {
            final List<ChatInfo> filteredModelList = filter(chatInfos, searchEditText.getText().toString());
            chatsAdapter.replaceAll(filteredModelList);
            recyclerView.scrollToPosition(0);
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
        for (ChatInfo model : models) {
            final String text =model.getChatTitle();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void joinChat(){
        ChatInfo selectedChat = chatsAdapter.getSelectedValue();
        selectedChat.joinChat();
        getActivity().onBackPressed();
    }
}
