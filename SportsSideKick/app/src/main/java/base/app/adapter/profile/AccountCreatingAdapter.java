package base.app.adapter.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import base.app.R;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandar Marinkovic on 13-Jul-17.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AccountCreatingAdapter extends RecyclerView.Adapter<AccountCreatingAdapter.ViewHolder> {

    private int oldPosition = -1;
    private String[] values;
    private int screenHeight;
    private OnClick onClickItem;
    private double itemHeight = 0.175;

    public String[] getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.text)
        TextView text;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public AccountCreatingAdapter(Context context, OnClick onClickItem) {
        values = context.getResources().getStringArray(R.array.menu_navigation);
        String swap = values[6];
        values[6] = values[7];
        values[7] = swap;
        oldPosition = -1;
        this.onClickItem = onClickItem;
        screenHeight = Utility.getDisplayHeight(context);
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_account_creating, parent, false);
        view.getLayoutParams().height = (int) (screenHeight * itemHeight);
        viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPosition = viewHolder.getAdapterPosition();
                onClickItem.itemClicked(oldPosition);
                notifyDataSetChanged();
            }
        });
        return viewHolder;
    }


    public interface OnClick {

        void itemClicked(int position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if ((position % 2) == 0) {
            if (oldPosition == position) {
                holder.itemView.setSelected(true);
            } else {
                holder.itemView.setSelected(false);
            }
            holder.image.setImageResource(myImages[position / 2]);
            holder.text.setText(values[position / 2]);
            holder.image.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.itemView.setClickable(true);
        } else {
            holder.image.setImageResource(myImages[getImageNumber(position)]);
            holder.image.setVisibility(View.INVISIBLE);
            holder.text.setVisibility(View.INVISIBLE);
            holder.text.setText(values[getImageNumber(position)]);
            holder.itemView.setClickable(false);
        }


    }

    @Override
    public int getItemCount() {
        return 18;
    }

    private static final int[] myImages = {
            R.drawable.menu_wall_selector,
            R.drawable.menu_chat_selector,
            R.drawable.menu_news_selector,
            R.drawable.menu_stats_selector,
            R.drawable.menu_rummours_selector,
            R.drawable.menu_club_radio_selector,
            R.drawable.menu_club_tv_selector,
            R.drawable.menu_shop_selector,
            R.drawable.menu_video_chat_selector,
            R.drawable.menu_menu_selector,


    };

    private int getImageNumber(int position) {
        switch (position) {
            case 1: {
                return 5;
            }
            case 3: {
                return 6;
            }
            case 5: {
                return 7;
            }
            case 7: {
                return 8;
            }
            case 9: {
                return 0;
            }
            case 11: {
                return 1;
            }
            case 13: {
                return 2;
            }
            case 15: {
                return 3;
            }
            case 17: {
                return 4;
            }
        }
        return 2;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public void setItemPosition(int oldPosition) {
        this.oldPosition = oldPosition;
        notifyDataSetChanged();
    }


}