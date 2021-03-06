package base.app.ui.adapter.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.util.events.CommentSelectedEvent;
import base.app.data.content.wall.Comment;
import base.app.data.content.wall.WallModel;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 04/03/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;

    private TranslationView translationView;
    private List<Comment> translatedComments;

    private String defaultImageForUserUrl;

    public List<Comment> getComments() {
        return comments;
    }

    public void remove(Comment comment) {
        comments.remove(comment);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.profile_image)
        ImageView profileImage;
        @Nullable
        @BindView(R.id.post_text)
        TextView comment;
        @Nullable
        @BindView(R.id.message_information)
        TextView messageInfo;
        @BindView(R.id.translate)
        TextView translate;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.delete)
        TextView delete;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }


    public CommentsAdapter(String defaultImageForUserUrl) {
        comments = new ArrayList<>();
        this.defaultImageForUserUrl = defaultImageForUserUrl;
        translatedComments = new ArrayList<>();
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        Task<User> getUserTask = LoginApi.getInstance().getUserInfoById(comment.getPosterId());
        holder.view.setTag(comment.getPosterId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult();
                    Object tag = holder.view.getTag();
                    if (tag != null) {
                        String holdersCurrentUser = (String) tag;
                        if (user.getUserId().equals(holdersCurrentUser)) {
                            setupWithUserInfo(comment, holder, user);
                        }
                    }
                }
            }
        });

        String translatedValue = null;
        for (Comment translated : translatedComments) {
            if (comment.getId().equals(translated.getId())) {
                translatedValue = translated.getComment();
            }
        }
        if (holder.comment != null) {
            if (translatedValue != null) {
                holder.comment.setText(translatedValue);
            } else {
                holder.comment.setText(comment.getComment());
            }
        }

        holder.translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentId = comment.getId();
                TaskCompletionSource<Comment> source = new TaskCompletionSource<>();
                source.getTask().addOnCompleteListener(new OnCompleteListener<Comment>() {
                    @Override
                    public void onComplete(@NonNull Task<Comment> task) {
                        if (task.isSuccessful()) {
                            Comment translatedComment = task.getResult();
                            updateWithTranslatedComment(translatedComment, holder.getAdapterPosition());
                        }
                    }
                });
                translationView.showTranslationPopup(holder.translate, commentId, source, TranslationView.TranslationType.TRANSLATE_COMMENT);
            }
        });

        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);

        // check if this comment belongs to this user
        if (LoginApi.getInstance().getUser() != null) {
            if (LoginApi.getInstance().getUser().getUserId().equals(comment.getPosterId())) {
                holder.edit.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new CommentSelectedEvent(comment));
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WallModel.getInstance().deletePostComment(comment);
                    }
                });
            }
        }
    }

    public void addAll(List<Comment> items) {
        comments.addAll(items);
    }

    public void clear() {
        comments.clear();
    }

    private void setupWithUserInfo(Comment comment, ViewHolder holder, User user) {
        final String userImage = user.getAvatar();
        if (userImage != null) {
            ImageLoader.displayImage(userImage, holder.profileImage, null);
        } else if (defaultImageForUserUrl != null) {
            ImageLoader.displayImage(defaultImageForUserUrl, holder.profileImage, null);
        }
        String time = "" + DateUtils.getRelativeTimeSpanString(
                (long) (comment.getTimestamp() * 1000),
                Utility.getCurrentTime(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.messageInfo.setText(
                user.getFirstName() + " " +
                        user.getLastName() + " | "
                        + time
        );
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setTranslationView(TranslationView translationView) {
        this.translationView = translationView;
    }

    private void updateWithTranslatedComment(Comment translated, int position) {
        translatedComments.add(translated);
        notifyItemChanged(position);
    }
}