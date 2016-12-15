package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Message Adapter for use in chat fragment
 */
public class MessageAdapter extends BaseAdapter {

    private ChatInfo chatInfo;
    private Context context;
    private static LayoutInflater inflater = null;

    public MessageAdapter(Context context, ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
        this.context = context;
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public int getCount() {
        return chatInfo.getMessages().size();
    }

    @Override
    public Object getItem(int i) {
        return chatInfo.getMessages().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.message_item, parent, false);
        TextView textView = (TextView)view.findViewById(R.id.text);
        textView.setText(chatInfo.getMessages().get(position).getText());
        return view;
    }
}
