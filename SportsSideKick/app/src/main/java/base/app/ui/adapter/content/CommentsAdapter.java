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
import base.app.util.events.comment.CommentSelectedEvent;
import base.app.data.Model;
import base.app.data.user.UserInfo;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallModel;
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
    private static final String TAG = "Comments Adapter";

    private List<PostComment> comments;

    private TranslationView translationView;
    private List<PostComment> translatedComments;

    private String defaultImageForUserUrl;

    public List<PostComment> getComments() {
        return comments;
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


    public CommentsAdapter(List<PostComment> comments, String defaultImageForUserUrl) {
        this.comments = comments;
        this.defaultImageForUserUrl = defaultImageForUserUrl;
        translatedComments = new ArrayList<>();
    }

    public void setTranslationView(TranslationView translationView) {
        this.translationView = translationView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PostComment comment = comments.get(position);
        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(comment.getPosterId());
        holder.view.setTag(comment.getPosterId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    UserInfo user = task.getResult();
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
        for (PostComment translated : translatedComments) {
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
             TaskCompletionSource<PostComment> source = new TaskCompletionSource<>();
             source.getTask().addOnCompleteListener(new OnCompleteListener<PostComment>() {
                 @Override
                 public void onComplete(@NonNull Task<PostComment> task) {
                     if(task.isSuccessful()){
                         PostComment translatedComment = task.getResult();
                         updateWithTranslatedComment(translatedComment,position);
                     }
                 }
             });
             translationView.showTranslationPopup(holder.translate,commentId, source, TranslationView.TranslationType.TRANSLATE_COMMENT);
         }
     });

        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);

        // check if this comment belongs to this user
        if(Model.getInstance().getUserInfo()!=null){
            if(Model.getInstance().getUserInfo().getUserId().equals(comment.getPosterId())){
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

    private void setupWithUserInfo(PostComment comment, ViewHolder holder, UserInfo user){
        final
        String userImage = user.getCircularAvatarUrl();
        if (userImage != null) {
            ImageLoader.displayImage(userImage, holder.profileImage);
        } else if(defaultImageForUserUrl!=null){
            ImageLoader.displayImage(defaultImageForUserUrl, holder.profileImage);
        }
        String time = "" + DateUtils.getRelativeTimeSpanString(
                comment.getTimestamp().longValue()*1000,
                Utility.getCurrentTime(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.messageInfo.setText(
            user.getFirstName() + " " +
            user.getLastName() + " | "
            + time
        );
    }

    public void updateWithTranslatedComment(PostComment translated, int position){
        translatedComments.add(translated);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }
}