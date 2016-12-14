package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.MessageAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsMessage;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChatFragment extends BaseFragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    ListView messageListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        messageListView = (ListView) view.findViewById(R.id.message_container);
        ChatInfo info = new ChatInfo("",null,"",true);
        List<ImsMessage> messages = new ArrayList<>();

        for(int i = 0; i<20; i++){
            messages.add(new ImsMessage());
        }

        info.setMessages(messages);
        MessageAdapter messageAdapter = new MessageAdapter(getContext(),info);
        messageListView.setAdapter(messageAdapter);
        return view;
    }

}
