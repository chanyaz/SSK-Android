package base.app.ui.fragment.content.wall;

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
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pixplicity.easyprefs.library.Prefs;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.ui.fragment.user.auth.LoginApi;
import base.app.data.content.Translator;
import base.app.data.content.share.ShareHelper;
import base.app.data.content.wall.FeedItem;
import base.app.data.content.wall.Comment;
import base.app.data.content.wall.News;
import base.app.data.content.wall.Post;
import base.app.data.content.wall.StoreOffer;
import base.app.data.content.wall.WallModel;
import base.app.ui.adapter.content.CommentsAdapter;
import base.app.util.ui.BaseFragment;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.events.CommentDeleteEvent;
import base.app.util.events.CommentSelectedEvent;
import base.app.util.events.CommentUpdateEvent;
import base.app.util.events.CommentUpdatedEvent;
import base.app.util.events.GetCommentsCompleteEvent;
import base.app.util.events.GetPostByIdEvent;
import base.app.util.events.ItemUpdateEvent;
import base.app.util.events.PostCommentCompleteEvent;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.data.TypeConverter.ItemType;
import static base.app.data.TypeConverter.getCache;
import static base.app.ui.fragment.popup.ProfileFragment.isAutoTranslateEnabled;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;
import static base.app.util.ui.TranslationView.TranslationType;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class DetailFragment extends BaseFragment {

    @BindView(R.id.contentImage)
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
    @BindView(R.id.comments_count_header)
    TextView commentsCount;
    @Nullable
    @BindView(R.id.likes_icon)
    ImageView likesIcon;
    @Nullable
    @BindView(R.id.likes_icon_liked)
    ImageView likesIconLiked;
    @Nullable
    @BindView(R.id.likes_count_header)
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
    @BindView(R.id.deleteButton)
    Button delete;
    @Nullable
    @BindView(R.id.share_buttons_container)
    View shareButtonsContainer;
    @Nullable
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;

    CommentsAdapter commentsAdapter;
    Post mPost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        String imgUri = "drawable://" + getResources().getIdentifier(
                "blank_profile_rounded", "drawable", getActivity().getPackageName());
        commentsAdapter = new CommentsAdapter(imgUri);
        commentsAdapter.setTranslationView(translationView);
        translationView.setParentView(view);
        commentsList.setLayoutManager(commentLayoutManager);
        commentsList.setAdapter(commentsAdapter);

        if (pinContainer != null) {
            pinContainer.setVisibility(View.GONE);
        }
        if (Utility.isPhone(getContext())) {
            ButterKnife.findById(view, R.id.close_button).setVisibility(View.GONE);
        }
        String id = getPrimaryArgument();
        mPost = (Post) getCache().get(id);
        // Probably came here from Deeplink/Notification - if item is not in cache, fetch it
        if (mPost == null) {
            String postId = id;
            if (id.contains("$$$")) {
                String[] parts = StringUtils.split(id, "$$$");
                if (parts.length == 2) {
                    postId = parts[0];
                } else {
                    postId = StringUtils.remove(id, "$$$");
                }
            }
            WallModel.getInstance().loadPost(postId);
            return view;
        } else {
            initializeWithData(mPost, true);
        }

        String userId = LoginApi.getInstance().getUser().getUserId();
        if (mPost.getWallId() != null) {
            if (mPost.getWallId().equals(userId)) {
                delete.setVisibility(View.VISIBLE);
            }
        }
        post.setEnabled(LoginApi.getInstance().isLoggedIn());
        post.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postButton.setVisibility(s.length() != 0 ? View.VISIBLE : View.GONE);
            }
        });
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    WallModel.getInstance().getCommentsForPost(mPost, commentsAdapter.getItemCount());
                }
            });
        }
        if (isAutoTranslateEnabled()) {
            TaskCompletionSource<Post> task = new TaskCompletionSource<>();
            task.getTask().addOnCompleteListener(new OnCompleteListener<Post>() {
                @Override
                public void onComplete(@NonNull Task<Post> task) {
                    if (task.isSuccessful()) {
                        Post translatedItem = task.getResult();
                        updateWithTranslatedItem(translatedItem);
                    }
                }
            });
            Translator.getInstance().translatePost(
                    mPost.getId(),
                    Prefs.getString(CHOSEN_LANGUAGE, "en"),
                    task
            );
        }
        return view;
    }

    private void initializeWithData(FeedItem item, boolean fetchComments) {
        if (item instanceof News) {
            News news = (News) item;
            ImageLoader.displayImage(news.getImage(), imageHeader, null);
            title.setText(news.getTitle());
            content.setText(news.getContent());
            if (commentsCount != null) {
                commentsCount.setText(String.valueOf(news.getCommentsCount()));
            }
            if (likesCount != null) {
                likesCount.setText(String.valueOf(news.getLikeCount()));
            }
            if (shareCount != null) {
                shareCount.setText(String.valueOf(news.getShareCount()));
            }
            if (news.getLikedByUser()) {
                if (likesIcon != null) {
                    likesIcon.setVisibility(View.GONE);
                }
                if (likesIconLiked != null) {
                    likesIconLiked.setVisibility(View.VISIBLE);
                }
            }
        } else if (item instanceof StoreOffer) {
            StoreOffer storeItem = (StoreOffer) item;
            ImageLoader.displayImage(storeItem.getCoverImageUrl(), imageHeader, null);
            title.setText(storeItem.getTitle());
        } else if (item instanceof Post) {
            if (fetchComments) {
                WallModel.getInstance().getCommentsForPost((Post) item);
            }
            Post post = (Post) item;
            ImageLoader.displayImage(post.getCoverImageUrl(), imageHeader,
                    R.drawable.wall_detail_header_placeholder);
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
            if (post.getLikedByUser()) {
                if (likesIcon != null) {
                    likesIcon.setVisibility(View.GONE);
                }
                if (likesIconLiked != null) {
                    likesIconLiked.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        List<Comment> comments = event.getCommentList();

        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                return Double.compare(rhs.getTimestamp(), lhs.getTimestamp());
            }
        });
        commentsAdapter.clear();
        commentsAdapter.addAll(comments);
        commentsAdapter.notifyDataSetChanged();

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
        if (commentForEdit == null) {
            sendComment();
        } else {
            updateComment();
        }
    }

    private void sendComment() {
        Comment comment = new Comment();
        comment.setComment(post.getText().toString());
        comment.setPosterId(LoginApi.getInstance().getUser().getUserId());
        comment.setWallId(mPost.getWallId());
        comment.setPostId(mPost.getId());
        comment.setTimestamp((double) (Utility.getCurrentTime() / 1000));
        WallModel.getInstance().postComment(comment);
        post.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
    }

    private void updateComment() {
        commentForEdit.setComment(post.getText().toString());
        WallModel.getInstance().postComment(commentForEdit);
        post.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
    }

    Comment commentForEdit;

    @Subscribe
    public void setCommentForEdit(CommentSelectedEvent event) {
        this.commentForEdit = event.getSelectedComment();
        post.setText(commentForEdit.getComment());
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        mPost = event.getPost();

        postCommentProgressBar.setVisibility(View.GONE);
        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(mPost.getCommentsCount()));
        }
    }

    @Subscribe
    public void onPostById(GetPostByIdEvent event) {
        mPost = event.getPost();
        initializeWithData(mPost, true);
    }

    @Subscribe
    public void onCommentUpdated(final CommentUpdatedEvent event) {
        FeedItem wallItem = event.getWallItem();
        if (wallItem != null && wallItem.getWallId().equals(mPost.getWallId()) && wallItem.getId().equals(mPost.getId())) {
            Comment receivedComment = event.getComment();
            Comment commentToUpdate = null;
            List<Comment> commentsInAdapter = commentsAdapter.getComments();
            for (Comment comment : commentsInAdapter) {
                if (comment.getId().equals(receivedComment.getId())) {
                    commentToUpdate = comment;
                }
            }
            if (commentToUpdate != null) {
                int position = commentsInAdapter.indexOf(commentToUpdate);
                commentsInAdapter.remove(commentToUpdate);
                commentsInAdapter.add(position, receivedComment);
                commentsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onCommentReceived(final CommentUpdateEvent event) {
        FeedItem wallItem = event.getWallItem();
        if (wallItem != null) {
            if (wallItem.getWallId().equals(mPost.getWallId())
                    && wallItem.getId().equals(mPost.getId())) {

                mPost.setCommentsCount(event.getWallItem().getCommentsCount());
                final Comment comment = event.getComment();

                if (event.getComment() != null) {
                    LoginApi.getInstance().getUserInfoById(comment.getPosterId())
                            .addOnCompleteListener(new OnCompleteListener<User>() {
                                @Override
                                public void onComplete(@NonNull Task<User> task) {
                                    if (task.isSuccessful()) {
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

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onDeleteComment(CommentDeleteEvent event) {
        if (event.getPost().getWallId().equals(mPost.getWallId())) {
            Comment commentToDelete = event.getComment();

            for (Comment comment : commentsAdapter.getComments()) {
                if (comment.getId().equals(commentToDelete.getId())) {
                    commentsAdapter.remove(comment);
                    commentsAdapter.notifyDataSetChanged();

                    commentsCount.setText(String.valueOf(mPost.getCommentsCount()));
                }
            }
        }
    }

    @OnClick({R.id.likes_container})
    public void togglePostLike() {
        if (LoginApi.getInstance().isLoggedIn()) {
            if (mPost != null) {
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

                toggleLike(mPost);
                if (likesIconLiked != null) {
                    likesIconLiked.setEnabled(false);
                }
                if (likesIcon != null) {
                    likesIcon.setVisibility(mPost.getLikedByUser() ? View.GONE : View.VISIBLE);
                }
                if (likesIconLiked != null) {
                    likesIconLiked.setVisibility(mPost.getLikedByUser() ? View.VISIBLE : View.GONE);
                }
                SoundEffects.getDefault().playSound(mPost.getLikedByUser() ? SoundEffects.ROLL_OVER : SoundEffects.SOFT);
            }
        }
    }

    private void toggleLike(FeedItem item) {
        boolean isLikedByUser = !item.getLikedByUser();
        item.setLikedByUser(isLikedByUser);
        if (isLikedByUser) {
            item.setLikeCount(item.getLikeCount() + 1);
        } else {
            item.setLikeCount(item.getLikeCount() - 1);
        }
    }

    @Subscribe
    public void onPostUpdate(ItemUpdateEvent event) {
        FeedItem post = event.getItem();
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

    @Optional
    @OnClick(R.id.deleteButton)
    public void deletePostOnClick(View view) {
        WallModel.getInstance().deletePost(mPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().onBackPressed();
            }
        });
    }

    @Optional
    @OnClick(R.id.share_facebook)
    public void sharePostFacebook() {
        ShareHelper.share(mPost);
    }

    @Optional
    @OnClick(R.id.share_twitter)
    public void sharePostTwitter() {
        PackageManager pkManager = getActivity().getPackageManager();
        try {
            PackageInfo pkgInfo = pkManager.getPackageInfo("com.twitter.android", 0);
            String getPkgInfo = pkgInfo.toString();

            if (getPkgInfo.contains("com.twitter.android")) {
                ShareHelper.share(mPost);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    // TODO - Same code in this and News Item fragment - decide how to solve

    @Optional
    @OnClick(R.id.read_more_text)
    public void readMoreClick(View view) {
        if (content.getMaxLines() == 3) {
            content.setMaxLines(Integer.MAX_VALUE);
            ((TextView) view).setText(R.string.read_less);
        } else {
            content.setMaxLines(3);
            ((TextView) view).setText(R.string.read_more);
        }
    }

    @BindView(R.id.translation_view)
    TranslationView translationView;

    @OnClick(R.id.translateButton)
    public void onTranslateClick(View view) {
        TaskCompletionSource<Post> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<Post>() {
            @Override
            public void onComplete(@NonNull Task<Post> task) {
                if (task.isSuccessful()) {
                    Post translatedPost = task.getResult();
                    updateWithTranslatedItem(translatedPost);
                }
            }
        });
        translationView.showTranslationPopup(view, mPost.getId(), source,
                TranslationType.TRANSLATE_POST, ItemType.Post);
    }

    private void updateWithTranslatedItem(Post translatedPost) {
        initializeWithData(translatedPost, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }
}