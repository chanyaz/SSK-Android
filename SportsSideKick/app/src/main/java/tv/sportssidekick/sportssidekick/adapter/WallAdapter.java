package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.instance.WallItemFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallNews;
import tv.sportssidekick.sportssidekick.model.wall.WallNewsShare;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;
import tv.sportssidekick.sportssidekick.model.wall.WallStoreItem;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 06/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {
    private static final String TAG = "WallAdapter";

    public SortedList<WallBase> getItems() {
        return values;
    }

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;


        @Nullable
        @BindView(R.id.caption)
        TextView captionTextView;
        @Nullable
        @BindView(R.id.description)
        TextView descriptionTextView;
        @Nullable
        @BindView(R.id.play_button)
        ImageView playButton;
        @Nullable
        @BindView(R.id.image)
        ImageView imageView;
        @Nullable
        @BindView(R.id.user_image)
        CircleImageView userImage;
        @Nullable
        @BindView(R.id.author)
        TextView author;
        @Nullable
        @BindView(R.id.likes_count)
        TextView likesCount;
        @Nullable
        @BindView(R.id.comments_count)
        TextView commentsCount;


        @Nullable
        @BindView(R.id.row_wall_post_likes)
        TextView rowPostLikes;
        @Nullable
        @BindView(R.id.row_wall_post_comments)
        TextView rowPostComments;


        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);


        }
    }

    private WallBase.PostType[] postTypeValues;

    public WallAdapter(Context context) {
        this.context = context;
        postTypeValues = WallBase.PostType.values();
    }

    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        int viewResourceId;
        switch (postTypeValues[viewType]) {
            case post:
                viewResourceId = R.layout.wall_item_user_post_new;
                break;
            case newsShare:
                viewResourceId = R.layout.wall_item_news_new;
                break;
            case rumor:
                viewResourceId = R.layout.wall_item_rumour_new;
                break;
            case wallStoreItem:
                viewResourceId = R.layout.wall_item_shop_new;
                break;
            default: // Somehow, we got unknown type as viewType argument, return default cell
                viewResourceId = R.layout.wall_item_rumour_new;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(viewResourceId, parent, false);
        return new WallAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(final WallAdapter.ViewHolder holder, final int position) {
        int screenHeight = Utility.getDisplayHeight(context);
        switch (values.get(position).getType()) {
            case post:
                WallPost post = (WallPost) values.get(position);
                if (holder.imageView != null) {
                    if (!TextUtils.isEmpty(post.getCoverImageUrl())) {
                        ImageLoader.getInstance().displayImage(post.getCoverImageUrl(), holder.imageView, Utility.getRoundedImageOptions());
                        holder.imageView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageView.setVisibility(View.GONE);
                    }
                }
                if (holder.playButton != null) {
                    holder.playButton.setVisibility(TextUtils.isEmpty(post.getVidUrl()) ? View.GONE : View.VISIBLE);
                }
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(post.getBodyText());
                }
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(post.getSubTitle());
                }
                Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(post.getWallId());
                getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if (task.isSuccessful()) {
                            UserInfo user = task.getResult();
                            if (user != null) {
                                if (user.getCircularAvatarUrl() != null && holder.userImage != null) {
                                    ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), holder.userImage, Utility.imageOptionsImageLoader());
                                }
                                if (user.getNicName() != null) {
                                    holder.author.setText(user.getFirstName() + " " + user.getLastName());
                                }
                            }
                        }
                    }
                });
                if (holder.playButton != null) {
                    holder.playButton.setVisibility(TextUtils.isEmpty(post.getVidUrl()) ? View.GONE : View.VISIBLE);
                }

                holder.commentsCount.setText(String.valueOf(post.getCommentsCount()));
                holder.likesCount.setText(String.valueOf(post.getLikeCount()));
                break;
            case newsShare:
                WallNewsShare news = (WallNewsShare) values.get(position);
                if (holder.imageView != null) {
                    if (!TextUtils.isEmpty(news.getCoverImageUrl()) || news.getCoverImageUrl() !=null) {
//                        holder.imageView.getLayoutParams().height = (int) (screenHeight * 0.25);
                        ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), holder.imageView, Utility.getImageOptionsForWallItem());
                        holder.imageView.setVisibility(View.VISIBLE);
                    }
                }
                if (holder.playButton != null) {
                    holder.playButton.setVisibility(TextUtils.isEmpty(news.getVidUrl()) ? View.GONE : View.VISIBLE);
                }
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(news.getTitle());
                }
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(news.getSubTitle());
                }
                if (holder.rowPostLikes != null) {
                    holder.rowPostLikes.setText(String.valueOf(news.getLikeCount()));
                }
                if (holder.rowPostComments != null) {
                    holder.rowPostComments.setText(String.valueOf(news.getCommentsCount()));
                }

                Task<UserInfo> getUserTaskNews = Model.getInstance().getUserInfoById(news.getWallId());
                getUserTaskNews.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if (task.isSuccessful()) {
                            UserInfo user = task.getResult();
                            if (user != null) {
                                if (user.getCircularAvatarUrl() != null && holder.userImage != null) {
                                    ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), holder.userImage, Utility.imageOptionsImageLoader());
                                }
                                if (user.getNicName() != null) {
                                    holder.author.setText(user.getFirstName() + " " + user.getLastName());
                                }
                            }
                        }
                    }
                });

                holder.commentsCount.setText(String.valueOf(news.getCommentsCount()));
                holder.likesCount.setText(String.valueOf(news.getLikeCount()));

                break;
            case betting:
                // No items of this type yet!
                break;
            case stats:
                // No items of this type yet!
                break;
            case rumor:
                WallNews rumour = (WallNews) values.get(position);
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(rumour.getTitle());
                }
                break;
            case wallStoreItem:
                WallStoreItem storeItem = (WallStoreItem) values.get(position);
                if (holder.imageView != null) {
                    String coverImageUrl = storeItem.getCoverImageUrl();
                    if (!TextUtils.isEmpty(coverImageUrl) && coverImageUrl!=null) {
                        ImageLoader.getInstance().displayImage(coverImageUrl, holder.imageView, Utility.getImageOptionsForWallItem());
                        holder.imageView.setVisibility(View.VISIBLE);
                    }
                }
                if (holder.captionTextView != null) {
                    holder.captionTextView.setText(storeItem.getTitle());
                }
                if (holder.descriptionTextView != null) {
                    holder.descriptionTextView.setText(storeItem.getSubTitle());
                }
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentEvent fe = new FragmentEvent(WallItemFragment.class);
                fe.setId(values.get(position).getPostId());
                EventBus.getDefault().post(fe);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return values.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void clear(){
        if(values!=null){
            clear();
        }
    }

    private static final Comparator<WallBase> ALPHABETICAL_COMPARATOR = new Comparator<WallBase>() {
        @Override
        public int compare(WallBase a, WallBase b) {
            return (a.getTimestamp()).compareTo
                    (b.getTimestamp());
        }
    };

    private final SortedList.Callback<WallBase> mCallback = new SortedList.Callback<WallBase>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(WallBase a, WallBase b) {
            return ALPHABETICAL_COMPARATOR.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(WallBase oldItem, WallBase newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(WallBase item1, WallBase item2) {
            return item1.getPostId().equals(item2.getPostId());
        }
    };

    final SortedList<WallBase> values = new SortedList<>(WallBase.class, mCallback);

    public void add(WallBase model) {
        values.add(model);
    }

    public void remove(WallBase model) {
        values.remove(model);
    }

    public void add(List<WallBase> models) {
        values.addAll(models);
    }

    public void remove(List<WallBase> models) {
        values.beginBatchedUpdates();
        for (WallBase model : models) {
            values.remove(model);
        }
        values.endBatchedUpdates();
    }

    public void replaceAll(List<WallBase> models) {
        values.beginBatchedUpdates();
        for (int i = values.size() - 1; i >= 0; i--) {
            final WallBase model = values.get(i);
            if (!models.contains(model)) {
                values.remove(model);
            }
        }
        values.addAll(models);
        values.endBatchedUpdates();
    }
}