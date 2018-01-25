package base.app.ui.adapter.menu;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import base.app.util.commons.Constant;
import base.app.R;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Utility;
import base.app.util.ui.NavigationDrawerItems;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandar Marinkovic on 18/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private static final String TAG = "Menu Adapter";
    private int oldPosition;
    private String[] values;
    private int screenWidth;
    private IDrawerClose drawerClose;

    public String[] getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.menu_image)
        ImageView image;
        @BindView(R.id.menu_text)
        TextView menu_text;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public MenuAdapter(Context context, IDrawerClose drawerClose) {
        values = context.getResources().getStringArray(R.array.menu_navigation);
        NavigationDrawerItems.getInstance().generateList(icons.length);
        this.drawerClose = drawerClose;
        screenWidth = Utility.getDisplayWidth(context);
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_navigation, parent, false);
        view.getLayoutParams().height = (int) (screenWidth * 0.245);
        view.getLayoutParams().width = (int) (screenWidth * 0.245);
        viewHolder = new ViewHolder(view);
        //setup click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                if (!viewHolder.itemView.isSelected()) {
                    notifyItemChanged(oldPosition);
                    oldPosition = position;
                    NavigationDrawerItems.getInstance().setByPosition(position);
                    viewHolder.itemView.setSelected(NavigationDrawerItems.getInstance().getItemById(position));
                    drawerClose.closeDrawerMenu(viewHolder.getAdapterPosition(),true);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Class fragmentToStart = Constant.PHONE_MENU_OPTIONS.get(viewHolder.getAdapterPosition());
                            EventBus.getDefault().post(new FragmentEvent(fragmentToStart));
                        }
                    }, 200);
                }
                else {
                    drawerClose.closeDrawerMenu(0,false);
                }

            }
        });

        return viewHolder;
    }


    public interface IDrawerClose {

        void closeDrawerMenu(int position,boolean good);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setSelected(NavigationDrawerItems.getInstance().getItemById(position));
        if (holder.itemView.isSelected())
            oldPosition = position;
        assert holder.image != null;
        holder.image.setImageResource(icons[position]);
        holder.menu_text.setText(values[position]);

    }

    @Override
    public int getItemCount() {
        if (values == null) {
            return 0;
        }
        return icons.length;
    }

    private static final int[] icons = {
            R.drawable.menu_wall_selector,
            R.drawable.menu_chat_selector,
            R.drawable.menu_news_selector,
            R.drawable.menu_social,
            R.drawable.menu_stats_selector,
//            R.drawable.menu_rummours_selector,
//            R.drawable.menu_club_radio_selector,
            R.drawable.menu_shop_selector,
            R.drawable.menu_club_tv_selector,
//            R.drawable.menu_video_chat_selector
    };
}