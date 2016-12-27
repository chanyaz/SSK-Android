package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChatHeadsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
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
            //getActivity().onBackPressed();
            FriendsManager.getInstance().getFriends();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 6);
        friendsRecyclerView.setLayoutManager(layoutManager);

        ChatHeadsAdapter chatHeadsAdapter = new ChatHeadsAdapter();
        friendsRecyclerView.setAdapter(chatHeadsAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().post(new BlurEvent(false));
    }

}
