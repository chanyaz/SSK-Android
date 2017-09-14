package base.app.adapter;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import base.app.R;
import base.app.model.Model;
import base.app.model.user.UserInfo;
import base.app.model.wall.PostComment;
import base.app.util.Utility;
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

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    String defaultImageForUserUrl;

    public CommentsAdapter(List<PostComment> comments, String defaultImageForUserUrl) {
        this.comments = comments;
        this.defaultImageForUserUrl = defaultImageForUserUrl;
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
                    Object tag =  holder.view.getTag();
                    if(tag!=null){
                        String holdersCurrentUser = (String)tag;
                        if(user.getUserId().equals(holdersCurrentUser)){
                            setupWithUserInfo(comment,holder,user);
                        }
                    }

                }
            }
        });
        holder.comment.setText(comment.getComment());
    }

    private void setupWithUserInfo(PostComment comment, ViewHolder holder, UserInfo user){
        final DisplayImageOptions imageOptions = Utility.getDefaultImageOptions();
        String userImage = user.getCircularAvatarUrl();
        if (userImage != null) {
            ImageLoader.getInstance().displayImage(userImage, holder.profileImage, imageOptions);
        } else if(defaultImageForUserUrl!=null){
            ImageLoader.getInstance().displayImage(defaultImageForUserUrl, holder.profileImage, imageOptions);
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
    @Override
    public int getItemCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }
}