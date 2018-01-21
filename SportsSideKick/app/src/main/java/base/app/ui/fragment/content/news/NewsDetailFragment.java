package base.app.ui.fragment.content.news;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import base.app.R;
import base.app.data.user.User;
import base.app.util.commons.UserRepository;
import base.app.data.content.news.NewsModel;
import base.app.data.content.share.ShareHelper;
import base.app.data.content.wall.FeedItem;
import base.app.data.content.wall.Comment;
import base.app.data.content.wall.News;
import base.app.data.content.wall.Pin;
import base.app.data.content.wall.WallModel;
import base.app.ui.MainActivity;
import base.app.ui.adapter.content.CommentsAdapter;
import base.app.util.ui.BaseFragment;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.events.CommentDeleteEvent;
import base.app.util.events.GetCommentsCompleteEvent;
import base.app.util.events.ItemUpdateEvent;
import base.app.util.events.PostCommentCompleteEvent;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.data.TypeConverter.*;
import static base.app.util.commons.Utility.getCurrentTime;
import static base.app.util.commons.Utility.hideKeyboard;
import static base.app.util.commons.Utility.showKeyboard;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsDetailFragment extends BaseFragment {

    @BindView(R.id.contentImage)
    ImageView imageHeader;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.authorImage)
    ImageView authorUserImage;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content_text)
    TextView content;
    @Nullable
    @BindView(R.id.share_news_to_wall_button)
    Button share;
    @BindView(R.id.comments_wall)
    RecyclerView commentsListView;
    @BindView(R.id.post_container)
    RelativeLayout postContainer;
    @BindView(R.id.post_text)
    EditText inputFieldComment;
    @BindView(R.id.post_comment_button)
    ImageView postButton;
    @BindView(R.id.post_comment_progress_bar)
    View postCommentProgressBar;
    @Nullable
    @BindView(R.id.comments_count_header)
    TextView commentsCountHeader;
    @Nullable
    @BindView(R.id.comments_count)
    TextView commentsCount;
    @Nullable
    @BindView(R.id.likes_icon)
    ImageView likesIcon;
    @BindView(R.id.pin_icon)
    ImageView pinIcon;
    @Nullable
    @BindView(R.id.likes_icon_liked)
    ImageView likesIconLiked;
    @Nullable
    @BindView(R.id.likes_count_header)
    TextView likesCountHeader;
    @Nullable
    @BindView(R.id.likes_count)
    TextView likesCount;
    @Nullable
    @BindView(R.id.share_count)
    TextView shareCount;
    @Nullable
    @BindView(R.id.share_icon)
    ImageView shareButton;
    @Nullable
    @BindView(R.id.share_container)
    View shareContainer;
    @Nullable
    @BindView(R.id.share_buttons_container)
    View shareButtons;
    @Nullable
    @BindView(R.id.share_facebook)
    ImageView shareFacebookButton;
    @Nullable
    @BindView(R.id.share_twitter)
    ImageView shareTwitterButton;
    @Nullable
    @BindView(R.id.swipeRefreshLayout)
    SwipyRefreshLayout swipeRefreshLayout;
    @BindView(R.id.commentInputOverlay)
    View commentInputOverlay;
    @Nullable
    @BindView(R.id.blurredContainer)
    View blurredContainer;
    @BindView(R.id.closeButtonSharedComment)
    View closeButtonSharedComment;
    @BindView(R.id.postButtonSharedComment)
    View postButtonSharedComment;
    @BindView(R.id.text_content)
    TextView textContent;
    @BindView(R.id.sharedMessageBar)
    View sharedMessageBar;
    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.sharedNewsCloseButton)
    ImageButton sharedNewsCloseButton;
    @BindView(R.id.sharedMessageField)
    EditText sharedMessageField;
    @BindView(R.id.pin_container)
    View pinContainer;
    @BindView(R.id.sharedMessageDivider)
    View sharedMessageDivider;
    @BindView(R.id.sharedMessageAvatar)
    ImageView sharedMessageAvatar;

    @BindView(R.id.sharedMessageMoreButton)
    ImageButton sharedMessageMoreButton;
    @BindView(R.id.sharedMessageDeleteEditContainer)
    View sharedMessageDeleteEditContainer;
    @BindView(R.id.sharedMessageEditButton)
    Button sharedMessageEditButton;
    @BindView(R.id.sharedMessageDeleteButton)
    Button sharedMessageDeleteButton;

    CommentsAdapter commentsAdapter;
    News item;
    private Pin pointerPin;
    List<Comment> comments;

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_detail,
                container,
                false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        item = loadFromCacheBy(id);
        if (item == null) return view;

        if (getSecondaryArgument() != null) {
            setSharedMessageBarVisible(true);
            pointerPin = (Pin) getCache().get(getSecondaryArgument());
            if (pointerPin != null) {
                showSharedMessageAvatar();
                sharedMessageField.setText(pointerPin.getSharedComment());
            }
        } else {
            setSharedMessageBarVisible(false);
        }

        showHeaderImage();
        showTextContent(item);

        setSharedMessageBarVisible(true);
        showSharingPreviewImage();
        showSharingAvatar();

        showCommentsLikesCount();
        showComments(view);

        autoHideShowPostButton();
        autoHideShowShareButton();

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    WallModel.getInstance().getCommentsForPost(item, comments.size());
                }
            });
        }
        setClickListeners();

        sharedMessageField.setHint(R.string.pin_message_hint);

        return view;
    }

    private void showSharedMessageAvatar() {
        Task<User> getUserTask = UserRepository.getInstance().getUserInfoById(pointerPin.getWallId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult();
                    if (user != null) {
                        if (getContext() != null) {
                            Glide.with(getContext())
                                    .load(user.getAvatar())
                                    .apply(new RequestOptions().placeholder(R.drawable.avatar_placeholder))
                                    .into(sharedMessageAvatar);
                        }
                    }
                }
            }
        });
    }

    @Optional
    @OnClick(R.id.delete)
    public void deletePostOnClick(View view) {
        /* TODO: Alex Sheiko
        WallModel.getInstance().deletePost(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().onBackPressed();
            }
        }); */
    }

    private void setSharedMessageBarVisible(boolean setVisible) {
        if (setVisible) {
            sharedMessageBar.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
        } else {
            sharedMessageBar.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {
        closeButtonSharedComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSharedCommentOverlay();
            }
        });
        sharedNewsCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        sharedMessageMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedMessageMoreButton.setVisibility(View.GONE);
                sharedMessageDeleteEditContainer.setVisibility(View.VISIBLE);
                sharedMessageField.setSingleLine(false);
                sharedMessageDivider.setVisibility(View.GONE);
            }
        });
        sharedMessageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSharedCommentEdit();
                sharedMessageEditButton.setText("Save");
            }
        });
        sharedMessageField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showSharedCommentEdit();
                    inputFieldComment.selectAll();
                    sharedMessageEditButton.setText("Save");
                }
            }
        });
        sharedMessageDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WallModel.getInstance().deletePost(pointerPin)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }
                            }
                        });
            }
        });
        postButtonSharedComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getPrimaryArgument().contains("UNOFFICIAL")) {
                    pin(ItemType.NewsShare);
                } else {
                    pin(ItemType.RumourShare);
                }
            }
        });
    }

    private void showCommentsLikesCount() {
        postContainer.setVisibility(View.VISIBLE);
        // TODO: Alex Sheiko WallModel.getInstance().getCommentsForPost(item);
        commentsListView.setNestedScrollingEnabled(false);

        if (commentsCountHeader != null) {
            commentsCountHeader.setText(String.valueOf(item.getCommentsCount()));
            commentsCount.setText(String.valueOf(item.getCommentsCount()));
        }
        if (likesCountHeader != null) {
            likesCountHeader.setText(String.valueOf(item.getLikeCount()));
            likesCount.setText(String.valueOf(item.getLikeCount()));
        }
        if (shareCount != null) {
            shareCount.setText(String.valueOf(item.getShareCount()));
        }
        if (item.getLikedByUser()) {
            if (likesIcon != null) {
                likesIcon.setVisibility(View.GONE);
            }
            if (likesIconLiked != null) {
                likesIconLiked.setVisibility(View.VISIBLE);
            }
        }
        if (pointerPin != null
                && pointerPin.getReferencedItemId() != null
                && !pointerPin.getReferencedItemId().isEmpty()) {
            pinIcon.setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.colorAccentSemiDark),
                    PorterDuff.Mode.MULTIPLY);
        }
    }

    private News loadFromCacheBy(String id) {
        NewsModel.NewsType type = NewsModel.NewsType.OFFICIAL;
        if (id.contains("UNOFFICIAL$$$")) {
            id = id.replace("UNOFFICIAL$$$", "");
            type = NewsModel.NewsType.UNOFFICIAL;
        }
        return NewsModel.getInstance().loadItemFromCache(id, type);
    }

    private void showHeaderImage() {
        Glide.with(getContext())
                .load(item.getImage())
                .into(imageHeader);
    }

    private void showSharingPreviewImage() {
        Glide.with(getContext())
                .load(item.getImage())
                .into(image);
    }

    private void showTextContent(News item) {
        title.setText(item.getTitle());
        content.setText(item.getContent());
        textContent.setText(item.getTitle());
    }

    private void showSharingAvatar() {
        Task<User> getUserTask = UserRepository.getInstance().getUserInfoById(item.getWallId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult();
                    if (user != null) {
                        Glide.with(getContext())
                                .load(user.getAvatar())
                                .into(authorUserImage);
                    }
                }
            }
        });
    }

    private void showComments(View view) {
        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        comments = new ArrayList<>();
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", getActivity().getPackageName());
        commentsAdapter = new CommentsAdapter(imgUri);
        commentsListView.setLayoutManager(commentLayoutManager);
        commentsListView.setAdapter(commentsAdapter);
        commentsAdapter.setTranslationView(translationView);
        translationView.setParentView(view);
    }

    private void autoHideShowShareButton() {
        if (UserRepository.getInstance().isRealUser()) {
            if (shareContainer != null) {
                shareContainer.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (shareButtons != null) {
                                shareButtons.setVisibility(View.VISIBLE);
                            }
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (shareButtons != null) {
                                shareButtons.setVisibility(View.GONE);
                            }
                        }
                        return false;
                    }
                });
            }
        } else {
            inputFieldComment.setEnabled(false);
        }
    }

    private void autoHideShowPostButton() {
        inputFieldComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    postButton.setVisibility(View.VISIBLE);
                } else {
                    postButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        if (event.getCommentList() != null) {
            for (Comment comment : event.getCommentList()) {
                if (!comments.contains(comment)) {
                    comments.add(comment);
                }
            }
            // Sort by timestamp
            Collections.sort(comments, new Comparator<Comment>() {
                @Override
                public int compare(Comment lhs, Comment rhs) {
                    return Double.compare(rhs.getTimestamp(), lhs.getTimestamp());
                }
            });
            commentsAdapter.notifyDataSetChanged();
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.post_comment_button)
    public void postComment() {
        if (UserRepository.getInstance().isRealUser()) {
            Comment comment = new Comment();
            comment.setComment(inputFieldComment.getText().toString());
            comment.setPosterId(UserRepository.getInstance().getUser().getUserId());
            comment.setWallId(item.getWallId());
            comment.setPostId(item.getId());
            comment.setTimestamp((double) (getCurrentTime() / 1000));
            WallModel.getInstance().postComment(comment);
            inputFieldComment.getText().clear();
            postCommentProgressBar.setVisibility(View.VISIBLE);
        } else {
            //TODO Notify user that need to login
        }
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        postCommentProgressBar.setVisibility(View.GONE);
        commentsAdapter.getComments().add(0, event.getComment());
        commentsAdapter.notifyDataSetChanged();
        commentsListView.scrollToPosition(commentsAdapter.getComments().size() - 1);
        item.setCommentsCount(commentsAdapter.getComments().size());
        commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
        commentsCountHeader.setText(String.valueOf(commentsAdapter.getComments().size()));
    }

    @Subscribe
    public void onDeleteComment(CommentDeleteEvent event) {
        FeedItem wallItem = event.getPost();
        if (wallItem != null) {
            if (wallItem.getWallId().equals(item.getWallId()) && wallItem.getId().equals(item.getId())) {
                Comment commentToDelete = null;
                Comment deletedComment = event.getComment();
                for (Comment comment : comments) {
                    if (comment.getId().equals(deletedComment.getId())) {
                        commentToDelete = comment;
                    }
                }
                if (commentToDelete != null) {
                    comments.remove(commentToDelete);
                    commentsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick({R.id.likes_container})
    public void togglePostLike() {
        if (UserRepository.getInstance().isRealUser()) {
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

                toggleLike(item);
                if (likesIconLiked != null) {
                    likesIconLiked.setEnabled(false);
                }
                if (likesIcon != null) {
                    likesIcon.setVisibility(item.getLikedByUser() ? View.GONE : View.VISIBLE);
                }
                if (likesIconLiked != null) {
                    likesIconLiked.setVisibility(item.getLikedByUser() ? View.VISIBLE : View.GONE);
                }
                SoundEffects.getDefault().playSound(item.getLikedByUser() ? SoundEffects.ROLL_OVER : SoundEffects.SOFT);
            }
        }
    }

    private void toggleLike(News item) {
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
        if ((post != null)) {
            if (commentsCount != null) {
                commentsCount.setText(String.valueOf(post.getCommentsCount()));
                commentsCountHeader.setText(String.valueOf(post.getCommentsCount()));
            }
            if (likesCount != null) {
                likesCount.setText(String.valueOf(post.getLikeCount()));
                likesCountHeader.setText(String.valueOf(post.getLikeCount()));
            }
            if (shareCount != null) {
                shareCount.setText(String.valueOf(post.getShareCount()));
            }
        }
    }

    @OnClick(R.id.share_facebook)
    public void sharePostFacebook() {
        if (UserRepository.getInstance().isRealUser()) {
            ShareHelper.share(item);
        } else {
            //TODO Notify user that need to login in order to SHARE
        }

    }

    @OnClick(R.id.share_twitter)
    public void sharePostTwitter() {
        if (UserRepository.getInstance().isRealUser()) {
            PackageManager pkManager = getActivity().getPackageManager();
            try {
                PackageInfo pkgInfo = pkManager.getPackageInfo("com.twitter.android", 0);
                String getPkgInfo = pkgInfo.toString();

                if (getPkgInfo.contains("com.twitter.android")) {
                    ShareHelper.share(item);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //TODO Notify user that need to login in order to SHARE
        }
    }

    protected void pin(ItemType type) {
        EditText sharedMessageField = commentInputOverlay.findViewById(R.id.post_text);
        String sharingMessage = sharedMessageField.getText().toString();

        Pin itemToPost = new Pin(
                sharingMessage,
                Utility.getClubConfig().get("ID"),
                item.getId());
        itemToPost.setTimestamp((double) Utility.getCurrentTime());
        itemToPost.setTitle(item.getTitle());
        itemToPost.setBodyText(item.getContent());
        itemToPost.setCoverAspectRatio(0.6f);
        if (item.getImage() != null) {
            itemToPost.setCoverImageUrl(item.getImage());
        }
        WallModel.getInstance().createPost(itemToPost);

        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.pin_container)
    public void showSharedCommentOverlay() {
        if (UserRepository.getInstance().isRealUser()) {
            commentInputOverlay.setVisibility(View.VISIBLE);

            showKeyboard(getContext());

            EditText sharedMessageField = commentInputOverlay.findViewById(R.id.post_text);
            sharedMessageField.requestFocus();

            // Blur background
            // MainActivity activity = (MainActivity) getActivity();
            // TODO: activity.toggleBlur(true, blurredContainer);
        } else {
            Toast.makeText(getContext(),
                    "Please login to pin news articles",
                    Toast.LENGTH_SHORT).show();
        }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
    }

    public void hideSharedCommentOverlay() {
        commentInputOverlay.setVisibility(View.GONE);

        hideKeyboard(getContext());

        // Disable background blur
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.toggleBlur(false, null);
        }
    }

    public void showSharedCommentEdit() {
        sharedMessageField.setSingleLine(false);
        sharedMessageField.requestFocus();

        sharedMessageMoreButton.setVisibility(View.GONE);
        sharedMessageDeleteEditContainer.setVisibility(View.VISIBLE);
        sharedMessageDivider.setVisibility(View.GONE);

        showKeyboard(getContext());

        sharedMessageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newSharedComment = sharedMessageField.getText().toString();
                pointerPin.setSharedComment(newSharedComment);
                pointerPin.setTimestamp((double) (getCurrentTime() / 1000));
                WallModel.getInstance().updatePost(pointerPin)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }
                            }
                        });
            }
        });
    }

    @Optional
    @OnClick(R.id.share_buttons_container)
    public void closeShareDialog() {
        if (shareButtons != null) {
            shareButtons.setVisibility(View.GONE);
        }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
    }

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

    @OnClick(R.id.translate)
    public void onTranslateClick(View view) {
        String postId = item.getId();
        TaskCompletionSource<News> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<News>() {
            @Override
            public void onComplete(@NonNull Task<News> task) {
                if (task.isSuccessful()) {
                    News translatedNews = task.getResult();
                    updateWithTranslatedPost(translatedNews);
                }
            }
        });
        translationView.showTranslationPopup(view, postId, source, TranslationView.TranslationType.TRANSLATE_NEWS);
    }

    private void updateWithTranslatedPost(News translatedNews) {
        showTextContent(translatedNews);
    }

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }
}
