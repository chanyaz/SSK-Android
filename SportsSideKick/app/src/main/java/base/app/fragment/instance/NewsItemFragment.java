package base.app.fragment.instance;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import base.app.activity.PhoneLoungeActivity;
import base.app.adapter.CommentsAdapter;
import base.app.events.CommentDeleteEvent;
import base.app.events.GetCommentsCompleteEvent;
import base.app.events.PostCommentCompleteEvent;
import base.app.events.PostUpdateEvent;
import base.app.fragment.BaseFragment;
import base.app.model.Model;
import base.app.model.news.NewsModel;
import base.app.model.sharing.SharingManager;
import base.app.model.user.UserInfo;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallNews;
import base.app.util.SoundEffects;
import base.app.util.Utility;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.util.Utility.getCurrentTime;
import static base.app.util.Utility.hideKeyboard;
import static base.app.util.Utility.showKeyboard;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsItemFragment extends BaseFragment {

    private static final String TAG = "NewsItemFragment";
    @BindView(R.id.content_image)
    ImageView imageHeader;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.author_user_image)
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
    @BindView(R.id.share_buttons_container)
    View shareButtons;
    @Nullable
    @BindView(R.id.share_facebook)
    ImageView shareFacebookButton;
    @Nullable
    @BindView(R.id.share_twitter)
    ImageView shareTwitterButton;

    @Nullable
    @BindView(R.id.swipe_refresh_layout)
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

    CommentsAdapter commentsAdapter;
    WallNews item;
    List<PostComment> comments;

    public NewsItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_news_item,
                container,
                false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        item = loadFromCacheBy(id);
        if (item == null) {
            // TODO This item is not in cache, fetch it individually!
            return view;
        }
        showHeaderImage();
        showTextContent(item);

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
        return view;
    }

    private void setClickListeners() {
        closeButtonSharedComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSharedCommentOverlay();
            }
        });
        postButtonSharedComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinToWall();
            }
        });
    }

    private void showCommentsLikesCount() {
        postContainer.setVisibility(View.VISIBLE);
        WallModel.getInstance().getCommentsForPost(item);
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
        if (item.isLikedByUser()) {
            if (likesIcon != null) {
                likesIcon.setVisibility(View.GONE);
            }
            if (likesIconLiked != null) {
                likesIconLiked.setVisibility(View.VISIBLE);
            }
        }
    }

    private WallNews loadFromCacheBy(String id) {
        NewsModel.NewsType type = NewsModel.NewsType.OFFICIAL;
        if (id.contains("UNOFFICIAL$$$")) {
            id = id.replace("UNOFFICIAL$$$", "");
            type = NewsModel.NewsType.UNOFFICIAL;
        }
        return NewsModel.getInstance().getCachedItemById(id, type);
    }

    private void showHeaderImage() {
        Glide.with(getContext())
                .load(item.getCoverImageUrl())
                .into(imageHeader);
    }

    private void showSharingPreviewImage() {
        Glide.with(getContext())
                .load(item.getCoverImageUrl())
                .into(image);
    }

    private void showTextContent(WallNews item) {
        title.setText(item.getTitle());
        content.setText(item.getBodyText());
    }

    private void showSharingAvatar() {
        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(item.getWallId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    UserInfo user = task.getResult();
                    if (user != null) {
                        Glide.with(getContext())
                                .load(user.getCircularAvatarUrl())
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
        commentsAdapter = new CommentsAdapter(comments, imgUri);
        commentsListView.setLayoutManager(commentLayoutManager);
        commentsListView.setAdapter(commentsAdapter);
        commentsAdapter.setTranslationView(translationView);
        translationView.setParentView(view);
    }

    private void autoHideShowShareButton() {
        if (Model.getInstance().isRealUser()) {
            if (shareButton != null) {
                shareButton.setOnTouchListener(new View.OnTouchListener() {

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
            for (PostComment comment : event.getCommentList()) {
                if (!comments.contains(comment)) {
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

    @OnClick(R.id.post_comment_button)
    public void postComment() {
        if (Model.getInstance().isRealUser()) {
            PostComment comment = new PostComment();
            comment.setComment(inputFieldComment.getText().toString());
            comment.setPosterId(Model.getInstance().getUserInfo().getUserId());
            comment.setWallId(item.getWallId());
            comment.setPostId(item.getPostId());
            comment.setTimestamp((double) (getCurrentTime() / 1000));
            WallModel.getInstance().postComment(item, comment);
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
        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
        }
    }

    @Subscribe
    public void onDeleteComment(CommentDeleteEvent event) {
        WallBase wallItem = event.getWallItem();
        if (wallItem != null) {
            if (wallItem.getWallId().equals(item.getWallId()) && wallItem.getPostId().equals(item.getPostId())) {
                PostComment commentToDelete = null;
                PostComment deletedComment = event.getComment();
                for (PostComment comment : comments) {
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

    @OnClick({R.id.likes_icon_liked, R.id.likes_icon})
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

    @OnClick(R.id.share_facebook)
    public void sharePostFacebook(View view) {
        if (Model.getInstance().isRealUser()) {
            SharingManager.getInstance().share(getContext(), item, false, SharingManager.ShareTarget.facebook, view);
        } else {
            //TODO Notify user that need to login in order to SHARE
        }

    }

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

    public void pinToWall() {
        String sharingMessage = inputFieldComment.getText().toString();

        WallNews itemToPost = new WallNews();
        itemToPost.setTimestamp((double) Utility.getCurrentTime());
        if (sharingMessage.isEmpty()) {
            itemToPost.setTitle(item.getTitle());
            itemToPost.setBodyText(item.getBodyText());
            itemToPost.setCoverAspectRatio(0.666666f);
            if (item.getSource() != null) {
                itemToPost.setSubTitle(item.getSource());
            } else {
                itemToPost.setSubTitle("");
            }
            if (item.getCoverImageUrl() != null) {
                itemToPost.setCoverImageUrl(item.getCoverImageUrl());
            }
        } else {
            itemToPost.setType(WallBase.PostType.newsShare);
            itemToPost.setReferencedItemClub(Utility.getClubConfig().get("ID"));
            itemToPost.setReferencedItemId(item.getPostId());
            itemToPost.setSharedComment(sharingMessage);
        }
        WallModel.getInstance().mbPost(itemToPost);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.pin_container)
    public void showSharedCommentOverlay() {
        if (Model.getInstance().isRealUser()) {
            commentInputOverlay.setVisibility(View.VISIBLE);

            inputFieldComment.requestFocus();
            showKeyboard(getContext());

            // Blur background
            PhoneLoungeActivity activity = (PhoneLoungeActivity) getActivity();
            activity.toggleBlur(true, blurredContainer);
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
        PhoneLoungeActivity activity = (PhoneLoungeActivity) getActivity();
        activity.toggleBlur(false, null);
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
            ((TextView) view).setText(R.string.read_more_open);
        } else {
            content.setMaxLines(3);
            ((TextView) view).setText(R.string.read_more_closed);
        }
    }

    @OnClick(R.id.close_button)
    public void closeOnClick() {
        getActivity().onBackPressed();
    }

    @BindView(R.id.translation_view)
    TranslationView translationView;

    @OnClick(R.id.translate)
    public void onTranslateClick(View view) {
        String postId = item.getPostId();
        TaskCompletionSource<WallNews> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<WallNews>() {
            @Override
            public void onComplete(@NonNull Task<WallNews> task) {
                if (task.isSuccessful()) {
                    WallNews translatedNews = task.getResult();
                    updateWithTranslatedPost(translatedNews);
                }
            }
        });
        translationView.showTranslationPopup(view, postId, source, TranslationView.TranslationType.TRANSLATE_NEWS);
    }

    private void updateWithTranslatedPost(WallNews translatedNews) {
        showTextContent(translatedNews);
    }

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }
}
