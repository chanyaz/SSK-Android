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
import tv.sportssidekick.sportssidekick.model.UserInfo;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.util.BlurEvent;

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
    @BindView(R.id.search_edit_text)
    EditText searchEditText;
    ChatFriendsAdapter chatHeadsAdapter;

    List<UserInfo> userInfoList;

    public CreateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EventBus.getDefault().post(new BlurEvent(true));
        View view = inflater.inflate(R.layout.popup_create_chat, container, false);
        ButterKnife.bind(this, view);


        confirmButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        friendsRecyclerView.setLayoutManager(layoutManager);

        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends();
        task.addOnSuccessListener(userInfos -> {
            chatHeadsAdapter = new ChatFriendsAdapter();
            chatHeadsAdapter.add(userInfos);
            userInfoList = userInfos;
            friendsRecyclerView.setAdapter(chatHeadsAdapter);
        });

        searchEditText.addTextChangedListener(textWatcher);

        return view;
    }

    public void performSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final List<UserInfo> filteredModelList = filter(userInfoList, searchEditText.getText().toString());
                chatHeadsAdapter.replaceAll(filteredModelList);
                friendsRecyclerView.scrollToPosition(0);
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

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().post(new BlurEvent(false));
    }

    private static List<UserInfo> filter(List<UserInfo> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<UserInfo> filteredModelList = new ArrayList<>();
        for (UserInfo model : models) {
            final String text =(model.getFirstName() + model.getLastName() + model.getNicName()).toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}
