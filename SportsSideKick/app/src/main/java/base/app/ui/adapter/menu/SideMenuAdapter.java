package base.app.ui.adapter.menu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import base.app.util.Constant;
import base.app.R;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.Utility;
import base.app.util.ui.NavigationDrawerItems;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandar Marinkovic on 18/04/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class SideMenuAdapter extends RecyclerView.Adapter<SideMenuAdapter.ViewHolder> {

    private static final String TAG = "Side Menu Adapter";
    private int oldPosition;

    private int screenWidth;
    private IDrawerCloseSideMenu iDrawerCloseSideMenu;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.menu_image)
        ImageView image;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public SideMenuAdapter(Context context, IDrawerCloseSideMenu iDrawerCloseSideMenu) {
        this.iDrawerCloseSideMenu = iDrawerCloseSideMenu;
        screenWidth = Utility.getDisplayWidth(context);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_bottom_navigation, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams((screenWidth / 5), RecyclerView.LayoutParams.MATCH_PARENT));
        viewHolder = new ViewHolder(view);
        //setup click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.getAdapterPosition() != (getItemCount() - 1)){
                    notifyItemChanged(oldPosition);
                    iDrawerCloseSideMenu.closeDrawerSideMenu(viewHolder.getAdapterPosition(), false);
                    oldPosition = viewHolder.getAdapterPosition();
                    viewHolder.itemView.setSelected(true);
                    EventBus.getDefault().post(new FragmentEvent(Constant.PHONE_MENU_OPTIONS.get(viewHolder.getAdapterPosition())));
                } else {
                    iDrawerCloseSideMenu.closeDrawerSideMenu(viewHolder.getAdapterPosition(), true);
                }
            }
        });
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setSelected(NavigationDrawerItems.getInstance().getItemById(position));
        if (holder.itemView.isSelected()) {
            oldPosition = position;
        }
        if(holder.image != null) {
            holder.image.setImageResource(icons[position]);
        }
    }

    @Override
    public int getItemCount() {
        if (icons == null) {
            return 0;
        }
        return icons.length;
    }

    private static final int[] icons = {
            R.drawable.menu_wall_selector,
            R.drawable.menu_chat_selector,
            R.drawable.menu_news_selector,
            R.drawable.menu_stats_selector,
            R.drawable.drawer,


    };

    public interface IDrawerCloseSideMenu {
        void closeDrawerSideMenu(int position, boolean openDrawer);
    }

}