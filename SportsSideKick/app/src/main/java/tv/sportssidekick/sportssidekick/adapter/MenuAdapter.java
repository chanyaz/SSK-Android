package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.os.Handler;
import android.provider.VoicemailContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.ChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubRadioFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTVFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.ClubTvPlaylistFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StatisticsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.NavigationDrawerItems;

/**
 * Created by Filip on 1/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private static final String TAG = "Club Adapter";
    private int oldPosition;
    private String[] values;
    private Context context;
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
        NavigationDrawerItems.getInstance().generateList(myImages.length);
        this.drawerClose = drawerClose;
        screenWidth = Utility.getDisplayWidth(context);
        this.context = context;
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

                if (viewHolder.getAdapterPosition() != getItemCount() - 1) {

                    notifyItemChanged(oldPosition);

                    drawerClose.closeDrawerMenu(viewHolder.getAdapterPosition());
                    if (!viewHolder.itemView.isSelected()) {
                        Handler handler = new Handler();
                        final Runnable r = new Runnable() {
                            public void run() {
                                oldPosition = viewHolder.getAdapterPosition();
                                viewHolder.itemView.setSelected(true);
                                EventBus.getDefault().post(new FragmentEvent(ClassList.get(viewHolder.getAdapterPosition())));
                            }
                        };

                        handler.postDelayed(r, 500);

                    } else {

                    }
                }

            }
        });

        return viewHolder;
    }


    public interface IDrawerClose {

        void closeDrawerMenu(int position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setSelected(NavigationDrawerItems.getInstance().getItemById(position));
        holder.image.setImageResource(myImages[position]);
        holder.menu_text.setText(values[position]);

    }

    @Override
    public int getItemCount() {
        if (values == null)
            return 0;
        return myImages.length;
    }

    public static final int[] myImages = {
            R.drawable.menu_wall_selector,
            R.drawable.menu_chat_selector,
            R.drawable.menu_news_selector,
            R.drawable.menu_stats_selector,
            R.drawable.menu_rummours_selector,
            R.drawable.menu_club_radio_selector,
            R.drawable.menu_shop_selector,
            R.drawable.menu_club_tv_selector,
            R.drawable.menu_video_chat_selector,
            R.drawable.menu_menu_selector,


    };

    public static final List<Class> ClassList = Collections.unmodifiableList(
            new ArrayList<Class>() {{
                add(WallFragment.class);
                add(ChatFragment.class);
                add(NewsFragment.class);
                add(StatisticsFragment.class);
                add(RumoursFragment.class);
                add(ClubRadioFragment.class);
                add(StoreFragment.class);
                add(ClubTVFragment.class);
                add(VideoChatFragment.class);

                // etc
            }});

}