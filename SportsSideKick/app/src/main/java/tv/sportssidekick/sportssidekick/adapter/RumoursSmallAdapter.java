package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Djordje on 05/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursSmallAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context context;

    public RumoursSmallAdapter(Context context) {
        this.context = context;
        if (context != null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView rowDescription;
        TextView rowInfo;
        TextView rowTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_rumours, parent, false);
            holder = new ViewHolder();

            holder.rowDescription = (TextView) convertView.findViewById(R.id.row_rumors_description);
            holder.rowInfo = (TextView) convertView.findViewById(R.id.row_rumors_description_info);
            holder.rowTime = (TextView) convertView.findViewById(R.id.row_rumors_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.rowDescription.setText("Club and player know that Leicester will try in January");
        holder.rowInfo.setText("Whoscored.com(latest news)");
        holder.rowTime.setText("22 hours");

        return convertView;
    }
}
