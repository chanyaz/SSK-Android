package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.github.rongi.rotate_layout.layout.RotateLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.TemporaryWallModel;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 06/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {
    private static final String TAG = "WallAdapter";

    private List<TemporaryWallModel> fakeModelList;

    public static final int VIEW_TYPE_POST_IMAGE = 0;
    public static final int VIEW_TYPE_SMALL_CELL = 1;
    public static final int VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS = 2;
    public static final int VIEW_TYPE_SHOP = 3;
    public static final int VIEW_TYPE_COMMENT = 4;

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;


        @Nullable
        @BindView(R.id.row_wall_post_image)
        ImageView rowPostImage;
        @Nullable
        @BindView(R.id.row_wall_post_description)
        TextView rowPostHeadline;
        @Nullable
        @BindView(R.id.row_wall_post_likes)
        TextView rowPostLikes;
        @Nullable
        @BindView(R.id.row_wall_post_comments)
        TextView rowPostComments;
        @Nullable
        @BindView(R.id.row_wall_post_play)
        ImageView rowPostPlay;
        @Nullable
        @BindView(R.id.row_wall_post_name)
        TextView rowPostName;

        @Nullable
        @BindView(R.id.row_wall_small_cell_headline_container)
        RotateLayout headlineBackground;
        @Nullable
        @BindView(R.id.row_wall_small_cell_headline)
        TextView rowSmallCellHeadline;
        @Nullable
        @BindView(R.id.row_wall_small_cell_info)
        TextView rowSmallCellDescription;

        @Nullable
        @BindView(R.id.row_wall_small_cell_with_progress_headline_container)
        RotateLayout categoryBackground;
        @Nullable
        @BindView(R.id.row_wall_small_cell_with_progress_description_headline)
        TextView rowSmallCellWithProgressHeadline;
        @Nullable
        @BindView(R.id.row_wall_small_cell_with_progress_description)
        TextView rowSmallCellWithProgressDescription;
        @Nullable
        @BindView(R.id.row_wall_small_cell_with_progress_name)
        TextView rowSmallCellWithProgressName;
        @Nullable
        @BindView(R.id.row_wall_small_cell_with_progress_headline)
        TextView rowSmallCellWithProgressCategory;
        @Nullable
        @BindView(R.id.row_wall_small_cell_with_progress_progress)
        DonutProgress rowSmallCellWithProgressProgres;

        @Nullable
        @BindView(R.id.row_wall_shop_image)
        ImageView rowShopImage;
        @Nullable
        @BindView(R.id.row_wall_shop_item_name)
        TextView rowShopItemName;
        @Nullable
        @BindView(R.id.row_wall_shop_price)
        TextView rowShopItemPrice;
        @Nullable
        @BindView(R.id.row_wall_shop_name)
        TextView rowShopUserName;
        @Nullable
        @BindView(R.id.row_wall_shop_open)
        Button rowShopOpen;

        @Nullable
        @BindView(R.id.row_wall_small_cell_comment_name)
        TextView rowCommentName;
        @Nullable
        @BindView(R.id.row_wall_small_cell_comment_content)
        TextView rowCommentContent;
        @Nullable
        @BindView(R.id.row_wall_small_cell_comment_tag_color)
        RelativeLayout rowCommentColorEdge;


        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public WallAdapter(Context context, List<TemporaryWallModel> fakeModelList) {
        this.context = context;
        this.fakeModelList = fakeModelList;
    }

    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        WallAdapter.ViewHolder viewHolder;
        // view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wall_post_image_video, parent, false);
        if (viewType == VIEW_TYPE_POST_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wall_post_image_video, parent, false);
            viewHolder = new WallAdapter.ViewHolder(view);
            return viewHolder;
       }  else if (viewType == VIEW_TYPE_SMALL_CELL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wall_small_cell, parent, false);
            viewHolder = new WallAdapter.ViewHolder(view);
            return viewHolder;
        } else if (viewType == VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wall_small_cell_with_progress_bar, parent, false);
            viewHolder = new WallAdapter.ViewHolder(view);
            return viewHolder;
        } else if (viewType == VIEW_TYPE_SHOP) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wall_shop, parent, false);
            viewHolder = new WallAdapter.ViewHolder(view);
            return viewHolder;
        } else if (viewType == VIEW_TYPE_COMMENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wall_small_cell_comment, parent, false);
            viewHolder = new WallAdapter.ViewHolder(view);
            return viewHolder;
        } else {
            return null;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(WallAdapter.ViewHolder holder, final int position) {
        if (fakeModelList.get(position).getType() == VIEW_TYPE_POST_IMAGE) {
            int screenHeight = Utility.getDisplayHeight(context);
            holder.rowPostImage.getLayoutParams().height = (int) (screenHeight * 0.25);
        }
        if (fakeModelList.get(position).getType() == VIEW_TYPE_SMALL_CELL) {
            holder.rowSmallCellHeadline.setText("RUMOUR");
            holder.rowSmallCellDescription.setText("Small cell description");
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.rumour_headerz);
            drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            if (holder.headlineBackground != null) {
                holder.headlineBackground.setBackground(drawable);
            }
        }
        if (fakeModelList.get(position).getType() == VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS) {
            holder.rowSmallCellWithProgressHeadline.setText("Small cell headline");
            if (holder.rowSmallCellWithProgressDescription != null) {
                holder.rowSmallCellWithProgressDescription.setText("Small cell with circle progress description");
            }
            if (holder.rowSmallCellWithProgressName != null) {
                holder.rowSmallCellWithProgressName.setText("Small cell user name");
            }
            if (holder.rowSmallCellWithProgressCategory != null) {
                holder.rowSmallCellWithProgressCategory.setText("BETTING");
            }
            if (holder.rowSmallCellWithProgressProgres != null) {
                holder.rowSmallCellWithProgressProgres.setProgress(89);
            }
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.rumour_headerz);
            drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            if (holder.categoryBackground != null) {
                holder.categoryBackground.setBackground(drawable);
            }
        }
        if (fakeModelList.get(position).getType() == VIEW_TYPE_SHOP) {
            int screenHeight = Utility.getDisplayHeight(context);
            if (holder.rowShopImage != null) {
                holder.rowShopImage.getLayoutParams().height = (int) (screenHeight * 0.25);
            }
            //TODO SET IMAGE
            holder.rowShopItemName.setText("Shop item name");
            holder.rowShopItemPrice.setText("€82");
            holder.rowShopUserName.setText("Shop user name");
            if (holder.rowShopOpen != null) {
                holder.rowShopOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"OPEN SHOP",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (fakeModelList.get(position).getType() == VIEW_TYPE_COMMENT) {
            if (holder.rowCommentName != null) {
                holder.rowCommentName.setText("Comment name");
            }

            if (holder.rowCommentContent != null) {
                holder.rowCommentContent.setText("Comment content");
            }
            //  holder.rowCommentColorEdge.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.small_cell_comment_left_line);
            drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            if (holder.rowCommentColorEdge != null) {
                holder.rowCommentColorEdge.setBackground(drawable);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        switch (fakeModelList.get(position).getType()) {
            case VIEW_TYPE_POST_IMAGE:
                return VIEW_TYPE_POST_IMAGE;
            case VIEW_TYPE_SMALL_CELL:
                return VIEW_TYPE_SMALL_CELL;
            case VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS:
                return VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS;
            case VIEW_TYPE_SHOP:
                return VIEW_TYPE_SHOP;
            case VIEW_TYPE_COMMENT:
                return VIEW_TYPE_COMMENT;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        return fakeModelList.size();
    }
}