package base.app.ui.adapter.content;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.Translator;
import base.app.data.wall.news.NewsModel;
import base.app.data.wall.WallNews;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.content.NewsItemFragment;
import base.app.util.ui.ImageLoader;

import static base.app.ui.fragment.popup.ProfileFragment.isAutoTranslateEnabled;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;

/**
 * Created by Djordje on 12/29/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NewsAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ROW = 2;
    private final NewsModel.NewsType itemType;
    private String screenTitle = null;

    protected List<WallNews> values;

    public List<WallNews> getValues() {
        return values;
    }

    public NewsAdapter(NewsModel.NewsType itemType) {
        this.itemType = itemType;
        values = new ArrayList<>();
    }

    public NewsAdapter(NewsModel.NewsType itemType, String screenTitle) {
        this.screenTitle = screenTitle;
        this.itemType = itemType;
        values = new ArrayList<>();
    }

    @NonNull
    public static WallNews showItemDetails(WallAdapter.ViewHolder holder, WallNews news) {
        WallAdapter.displayUserInfo(news, holder);
        WallAdapter.displayTitle(news.getTitle(), holder);
        WallAdapter.displaySubhead(news, holder);
        WallAdapter.displayPostImage(news, holder);
        WallAdapter.displayCommentsAndLikes(news, holder);
        holder.view.setOnClickListener(getClickListener(news));
        return news;
    }

    @NonNull
    private static View.OnClickListener getClickListener(final WallNews item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fe = new FragmentEvent(NewsItemFragment.class);
                fe.setId(item.getPostId());
                EventBus.getDefault().post(fe);
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ROW;
        }
    }

    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        WallAdapter.ViewHolder viewHolder;
        int layoutId;
        if (viewType == VIEW_TYPE_HEADER) {
            layoutId = R.layout.header_news;
        } else {
            layoutId = getItemLayoutId();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        viewHolder = new WallAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final WallAdapter.ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ImageView topImage = holder.view.findViewById(R.id.topImage);
            ImageLoader.displayImage(R.drawable.image_wall_background, topImage);

            if (itemType == NewsModel.NewsType.SOCIAL) {
                TextView topCaption = holder.view.findViewById(R.id.topCaption);
                topCaption.setText(screenTitle);
            }
        } else {
            final WallNews news = showItemDetails(holder, values.get(position));

            if (isAutoTranslateEnabled() && news.isNotTranslated()) {
                TaskCompletionSource<WallNews> task = new TaskCompletionSource<>();
                task.getTask().addOnCompleteListener(new OnCompleteListener<WallNews>() {
                    @Override
                    public void onComplete(@NonNull Task<WallNews> task) {
                        int position = holder.getAdapterPosition();
                        if (task.isSuccessful() && position != -1) {
                            WallNews translatedItem = task.getResult();
                            remove(position);
                            add(position, translatedItem);
                            notifyItemChanged(position);
                        }
                    }
                });
                if (itemType == NewsModel.NewsType.SOCIAL) {
                    Translator.getInstance().translateSocial(
                            news.getPostId(),
                            Prefs.getString(CHOSEN_LANGUAGE, "en"),
                            task
                    );
                } else {
                    Translator.getInstance().translateNews(
                            news.getPostId(),
                            Prefs.getString(CHOSEN_LANGUAGE, "en"),
                            task
                    );
                }
            }
        }
    }

    protected int getItemLayoutId() {
        return R.layout.wall_item_news;
    }

    @Override
    public int getItemCount() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    public void add(int position, WallNews model) {
        values.add(position, model);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (values.get(position) != null) {
            values.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void remove(WallNews item) {
        int deletedFromPosition = values.indexOf(item);
        values.remove(item);
        notifyItemRemoved(deletedFromPosition);
    }

    public void addAll(@NotNull List<? extends WallNews> items) {
        values.addAll(items);
        notifyItemRangeChanged(0, items.size());
    }

    public void clear() {
        values.clear();
    }
}