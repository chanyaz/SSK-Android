package base.app.ui.fragment.content;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import base.app.R;
import base.app.data.Model;
import base.app.data.sharing.SharingManager;
import base.app.data.tutorial.WallTip;
import base.app.data.user.UserInfo;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallBase;
import base.app.data.wall.WallModel;
import base.app.data.wall.WallNewsShare;
import base.app.data.wall.WallPost;
import base.app.data.wall.WallStoreItem;
import base.app.ui.adapter.content.CommentsAdapter;
import base.app.ui.adapter.menu.TutorialStepAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.events.comment.CommentDeleteEvent;
import base.app.util.events.comment.CommentSelectedEvent;
import base.app.util.events.comment.CommentUpdateEvent;
import base.app.util.events.comment.CommentUpdatedEvent;
import base.app.util.events.comment.GetCommentsCompleteEvent;
import base.app.util.events.post.GetPostByIdEvent;
import base.app.util.events.post.PostCommentCompleteEvent;
import base.app.util.events.post.PostUpdateEvent;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class WallItemFragment extends BaseFragment {

    @BindView(R.id.content_image)
    ImageView imageHeader;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content_text)
    TextView content;
    @BindView(R.id.share_news_to_wall_button)
    Button share;
    @BindView(R.id.content_video)
    VideoView videoView;
    @BindView(R.id.comments_wall)
    RecyclerView commentsList;

    @BindView(R.id.post_container)
    RelativeLayout postContainer;
    @BindView(R.id.post_text)
    EditText post;
    @BindView(R.id.post_comment_button)
    ImageView postButton;
    @BindView(R.id.post_comment_progress_bar)
    View postCommentProgressBar;

    @Nullable
    @BindView(R.id.comments_count)
    TextView commentsCount;
    @Nullable
    @BindView(R.id.likes_icon)
    ImageView likesIcon;
    @Nullable
    @BindView(R.id.likes_icon_liked)
    ImageView likesIconLiked;
    @Nullable
    @BindView(R.id.likes_count)
    TextView likesCount;
    @Nullable
    @BindView(R.id.share_count)
    TextView shareCount;

    @Nullable
    @BindView(R.id.pin_container)
    LinearLayout pinContainer;

    @Nullable
    @BindView(R.id.social_buttons_container)
    LinearLayout buttonsContainer;
    @Nullable
    @BindView(R.id.header_container)
    RelativeLayout headerContainer;
    @Nullable
    @BindView(R.id.bottom_horizontal_split_line)
    View bottomSplitLine;
    @Nullable
    @BindView(R.id.tutorial_container)
    LinearLayout tutorialContainer;
    @Nullable
    @BindView(R.id.tutorial_title)
    TextView tutorialTitle;
    @Nullable
    @BindView(R.id.tutorial_description)
    TextView tutorialDescription;

    @Nullable
    @BindView(R.id.share_icon)
    ImageView shareButton;

    @Nullable
    @BindView(R.id.delete)
    TextView delete;

    @Nullable
    @BindView(R.id.share_buttons_container)
    View shareButtonsContainer;

    @Nullable
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;

    CommentsAdapter commentsAdapter;
    WallBase item;
    List<PostComment> comments;

    public WallItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        comments = new ArrayList<>();

        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", getActivity().getPackageName());
        commentsAdapter = new CommentsAdapter(comments,imgUri);
        commentsAdapter.setTranslationView(translationView);
        translationView.setParentView(view);
        commentsList.setLayoutManager(commentLayoutManager);
        commentsList.setAdapter(commentsAdapter);

        if (pinContainer != null) {
            pinContainer.setVisibility(View.GONE);
        }
        if(Utility.isPhone(getContext())){
            ButterKnife.findById(view,R.id.close_button).setVisibility(View.GONE);
        }
        String id = getPrimaryArgument();
        item = WallBase.getCache().get(id);
        // Probably came here from Deeplink/Notification - if item is not in cache, fetch it
        if(item == null){
            String postId;
            String wallId = null;
            if(id.contains("$$$")){
                String[] parts = StringUtils.split(id,"$$$");
                if(parts.length==2){
                    postId = parts[0];
                    wallId = parts[1];
                } else {
                    postId = StringUtils.remove(id,"$$$");
                }
                WallModel.getInstance().getPostById(postId,wallId);
            }
            return view;
        } else {
            initializeWithData(true, item);
        }

        String userId = Model.getInstance().getUserInfo().getUserId();
        if(item.getWallId()!=null){
            if(item.getWallId().equals(userId)){
                delete.setVisibility(View.VISIBLE);
            }
        }

        post.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postButton.setVisibility(s.length() != 0 ? View.VISIBLE : View.GONE);
            }
        });

        post.setEnabled(Model.getInstance().isRealUser());

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    WallModel.getInstance().getCommentsForPost(item,comments.size());
                }
            });
        }

        return view;
    }

    private void initializeWithData(boolean fetchComments, WallBase item){

        if(fetchComments){
            WallModel.getInstance().getCommentsForPost(item);
        }
        switch (item.getType()) {
            case post:
                WallPost post = (WallPost) item;
                Glide.with(this).load(post.getCoverImageUrl()).into(imageHeader);
                title.setText(post.getTitle());
                content.setText(post.getBodyText());
                if (post.getVidUrl() != null) {
                    videoView.setVisibility(View.VISIBLE);
                    imageHeader.setVisibility(View.GONE);
                    videoView.setVideoURI(Uri.parse(post.getVidUrl()));
                    videoView.start();
                }
                postContainer.setVisibility(View.VISIBLE);
                commentsList.setNestedScrollingEnabled(false);

                if (commentsCount != null) {
                    commentsCount.setText(String.valueOf(post.getCommentsCount()));
                }
                if (likesCount != null) {
                    likesCount.setText(String.valueOf(post.getLikeCount()));
                }
                if (shareCount != null) {
                    shareCount.setText(String.valueOf(post.getShareCount()));
                }
                if (post.isLikedByUser()) {
                    if (likesIcon != null) {
                        likesIcon.setVisibility(View.GONE);
                    }
                    if (likesIconLiked != null) {
                        likesIconLiked.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case rumor:
            case newsShare:
                WallNewsShare news = (WallNewsShare) item;
                ImageLoader.displayImage(news.getCoverImageUrl(), imageHeader, R.drawable.blank_profile_rounded);
                title.setText(news.getTitle());
                content.setText(news.getBodyText());
                if (commentsCount != null) {
                    commentsCount.setText(String.valueOf(news.getCommentsCount()));
                }
                if (likesCount != null) {
                    likesCount.setText(String.valueOf(news.getLikeCount()));
                }
                if (shareCount != null) {
                    shareCount.setText(String.valueOf(news.getShareCount()));
                }
                if (news.isLikedByUser()) {
                    if (likesIcon != null) {
                        likesIcon.setVisibility(View.GONE);
                    }
                    if (likesIconLiked != null) {
                        likesIconLiked.setVisibility(View.VISIBLE);
                    }
                }

                break;
            case betting:
                break;
            case stats:
                break;
            case wallStoreItem:
                WallStoreItem storeItem = (WallStoreItem) item;
                ImageLoader.displayImage(storeItem.getCoverImageUrl(), imageHeader, R.drawable.blank_profile_rounded);
                title.setText(storeItem.getTitle());
                break;
            case tip:
                final WallTip tip = (WallTip) item;
                TutorialStepAdapter adapter = new TutorialStepAdapter();
                adapter.getWallSteps().addAll(tip.getTipSteps());

                commentsList.setAdapter(adapter);

                if (buttonsContainer != null) {
                    buttonsContainer.setVisibility(View.GONE);
                }
                if (headerContainer != null) {
                    headerContainer.setVisibility(View.GONE);
                }
                if (bottomSplitLine != null) {
                    bottomSplitLine.setVisibility(View.GONE);
                }
                content.setVisibility(View.GONE);
                if (tutorialContainer != null) {
                    tutorialContainer.setVisibility(View.VISIBLE);
                }
                if (tutorialTitle != null) {
                    tutorialTitle.setText(tip.getTipTittle());
                }
                if (tutorialDescription != null) {
                    tutorialDescription.setText(tip.getTipDescription());
                }
                break;
        }
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        if(event.getCommentList()!=null){
            for(PostComment comment : event.getCommentList()){
                if(!comments.contains(comment)){
                    comments.add(comment);
                }
            }

            // Sort by timestamp
            Collections.sort(comments, new Comparator<PostComment>() {
                @Override
                public int compare(PostComment lhs, PostComment rhs) {
                    return rhs.getTimestamp().compareTo(lhs.getTimestamp());
                }
            });
            commentsAdapter.notifyDataSetChanged();
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.close_button)
    public void closeOnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.share_container)
    public void onShareClick() {
        if (shareButtonsContainer != null) {
            shareButtonsContainer.setVisibility(View.VISIBLE);
        }

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(250);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1700);
        fadeOut.setDuration(250);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        shareButtonsContainer.setAnimation(animation);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shareButtonsContainer.setVisibility(View.GONE);
            }
        }, 1900);
    }

    @OnClick(R.id.post_comment_button)
    public void postComment() {
        if (Model.getInstance().isRealUser()) {
            if(commentForEdit==null){
                sendComment();
            } else {
                updateComment();
            }
        } else {
            //TODO notify user
        }
    }

    private void sendComment(){
        PostComment comment = new PostComment();
        comment.setComment(post.getText().toString());
        comment.setPosterId(Model.getInstance().getUserInfo().getUserId());
        comment.setWallId(item.getWallId());
        comment.setPostId(item.getPostId());
        comment.setTimestamp((double) (Utility.getCurrentTime() / 1000));
        WallModel.getInstance().postComment(comment);
        post.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
    }

    private void updateComment(){
        commentForEdit.setComment(post.getText().toString());
        WallModel.getInstance().postComment(commentForEdit);
        post.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
    }

    PostComment commentForEdit;

    @Subscribe
    public void setCommentForEdit(CommentSelectedEvent event){
        this.commentForEdit = event.getSelectedComment();
        post.setText(commentForEdit.getComment());
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        postCommentProgressBar.setVisibility(View.GONE);
        commentsAdapter.getComments().add(0, event.getComment());
        commentsAdapter.notifyDataSetChanged();
        item.setCommentsCount(commentsAdapter.getComments().size());
        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
        }
    }


    @Subscribe
    public void onPostById(GetPostByIdEvent event){
        item = event.getPost();
        if(item!=null){
            initializeWithData(true, item);
        }
    }

    @Subscribe
    public void onCommentUpdated(final CommentUpdatedEvent event) {
        WallBase wallItem = event.getWallItem();
        if(wallItem != null && wallItem.getWallId().equals(item.getWallId()) && wallItem.getPostId().equals(item.getPostId())) {
            PostComment receivedComment = event.getComment();
            PostComment commentToUpdate = null;
            List<PostComment> commentsInAdapter = commentsAdapter.getComments();
            for(PostComment comment : commentsInAdapter){
                if(comment.getId().equals(receivedComment.getId())){
                    commentToUpdate = comment;
                }
            }

            if(commentToUpdate!=null){
                int position = commentsInAdapter.indexOf(commentToUpdate);
                commentsInAdapter.remove(commentToUpdate);
                commentsInAdapter.add(position,receivedComment);
                commentsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onCommentReceived(final CommentUpdateEvent event) {
        WallBase wallItem = event.getWallItem();
        if (wallItem != null) {
            if (wallItem.getWallId().equals(item.getWallId())
                    && wallItem.getPostId().equals(item.getPostId())) {

                item.setCommentsCount(event.getWallItem().getCommentsCount());
                final PostComment comment = event.getComment();

                if(event.getComment()!=null) {
                    Model.getInstance().getUserInfoById(comment.getPosterId())
                            .addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                        @Override
                        public void onComplete(@NonNull Task<UserInfo> task) {
                            if(task.isSuccessful()){
                                commentsAdapter.getComments().add(0, comment);
                                commentsAdapter.notifyDataSetChanged();
                                if (commentsCount != null) {
                                    commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Subscribe
    public void onDeleteComment(CommentDeleteEvent event) {
        WallBase wallItem = event.getWallItem();
        if (wallItem != null) {
            if (wallItem.getWallId().equals(item.getWallId()) && wallItem.getPostId().equals(item.getPostId())) {
                PostComment commentToDelete = null;
                PostComment deletedComment = event.getComment();
                for(PostComment comment : comments){
                    if(comment.getId().equals(deletedComment.getId())){
                        commentToDelete = comment;
                    }
                }
                if(commentToDelete!=null){
                    comments.remove(commentToDelete);
                    commentsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick({R.id.likes_container})
    public void togglePostLike() {
        if (Model.getInstance().isRealUser()) {
            if (item != null) {
                if (likesIconLiked != null) {
                    likesIconLiked.setEnabled(false);
                }
                if (likesIcon != null) {
                    likesIcon.setEnabled(false);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (likesIconLiked != null) {
                            likesIconLiked.setEnabled(true);
                        }
                        if (likesIcon != null) {
                            likesIcon.setEnabled(true);
                        }
                    }
                }, 2000);

                item.toggleLike();
                if (likesIconLiked != null) {
                    likesIconLiked.setEnabled(false);
                }
                if (likesIcon != null) {
                    likesIcon.setVisibility(item.isLikedByUser() ? View.GONE : View.VISIBLE);
                }
                if (likesIconLiked != null) {
                    likesIconLiked.setVisibility(item.isLikedByUser() ? View.VISIBLE : View.GONE);
                }
                SoundEffects.getDefault().playSound(item.isLikedByUser() ? SoundEffects.ROLL_OVER : SoundEffects.SOFT);
            }
        }
    }

    @Subscribe
    public void onPostUpdate(PostUpdateEvent event) {
        WallBase post = event.getPost();
        if ((post != null)) {
            if (commentsCount != null) {
                commentsCount.setText(String.valueOf(post.getCommentsCount()));
            }
            if (likesCount != null) {
                likesCount.setText(String.valueOf(post.getLikeCount()));
            }
            if (shareCount != null) {
                shareCount.setText(String.valueOf(post.getShareCount()));
            }
        }
    }

    @Optional
    @OnClick(R.id.delete)
    public void deletePostOnClick(View view){
        WallModel.getInstance().deletePost(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().onBackPressed();
            }
        });
    }

    @Optional
    @OnClick(R.id.share_facebook)
    public void sharePostFacebook(View view) {
        if (Model.getInstance().isRealUser()) {
            SharingManager.getInstance().share(getContext(), item, false, SharingManager.ShareTarget.facebook, view);
        } else {
            //TODO Notify user that need to login in order to SHARE
        }

    }

    @Optional
    @OnClick(R.id.share_twitter)
    public void sharePostTwitter(View view) {
        if (Model.getInstance().isRealUser()) {
            PackageManager pkManager = getActivity().getPackageManager();
            try {
                PackageInfo pkgInfo = pkManager.getPackageInfo("com.twitter.android", 0);
                String getPkgInfo = pkgInfo.toString();

                if (getPkgInfo.contains("com.twitter.android")) {
                    SharingManager.getInstance().share(getContext(), item, false, SharingManager.ShareTarget.twitter, view);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.news_install_twitter), Toast.LENGTH_LONG).show();
            }
        } else {
            //TODO Notify user that need to login in order to SHARE
        }

    }


    // TODO - Same code in this and News Item fragment - decide how to solve

    @Optional
    @OnClick(R.id.read_more_text)
    public void readMoreClick(View view) {
        if (content.getMaxLines() == 3) {
            content.setMaxLines(Integer.MAX_VALUE);
            ((TextView)view).setText(R.string.read_more_open);
        } else {
            content.setMaxLines(3);
            ((TextView)view).setText(R.string.read_more_closed);
        }
    }

    @BindView(R.id.translation_view)
    TranslationView translationView;

    @OnClick(R.id.translate)
    public void onTranslateClick(View view){
        String postId = item.getPostId();
        TaskCompletionSource<WallBase> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<WallBase>() {
            @Override
            public void onComplete(@NonNull Task<WallBase> task) {
                if(task.isSuccessful()){
                    WallBase translatedPost = task.getResult();
                    updateWithTranslatedPost(translatedPost);
                }
            }
        });
        translationView.showTranslationPopup(view,postId, source,TranslationView.TranslationType.TRANSLATE_WALL, item.getType());
    }

    private void updateWithTranslatedPost(WallBase translatedPost){
        initializeWithData(false, translatedPost);
    }

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }
}