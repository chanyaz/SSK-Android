package base.app.ui.fragment.content;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import base.app.BuildConfig;
import base.app.R;
import base.app.data.Id;
import base.app.data.Model;
import base.app.data.Translator;
import base.app.data.sharing.SharingManager;
import base.app.data.user.UserInfo;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallBase;
import base.app.data.wall.WallModel;
import base.app.data.wall.WallNewsShare;
import base.app.data.wall.WallPost;
import base.app.data.wall.WallStoreItem;
import base.app.ui.adapter.content.CommentsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.events.comment.CommentDeleteEvent;
import base.app.util.events.comment.CommentReceiveEvent;
import base.app.util.events.comment.CommentSelectedEvent;
import base.app.util.events.comment.CommentUpdatedEvent;
import base.app.util.events.comment.GetCommentsCompleteEvent;
import base.app.util.events.post.GetPostByIdEvent;
import base.app.util.events.post.ItemUpdateEvent;
import base.app.util.events.post.PostCommentCompleteEvent;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.ui.fragment.popup.ProfileFragment.isAutoTranslateEnabled;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;
import static base.app.util.ui.TranslationView.TranslationType;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class WallItemFragment extends BaseFragment {

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
    View pinContainer;
    @BindView(R.id.comments_container)
    View commentsContainer;
    @Nullable
    @BindView(R.id.social_buttons_container)
    View buttonsContainer;
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
    SwipeRefreshLayout swipeRefreshLayout;

    CommentsAdapter commentsAdapter;
    WallBase mPost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        setClickListeners();

        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        String imgUri = "drawable://" + getResources().getIdentifier(
                "blank_profile_rounded", "drawable", getActivity().getPackageName());

        if (pinContainer != null) {
            pinContainer.setVisibility(View.GONE);
        }
        String id = getPrimaryArgument();
        mPost = WallBase.getCache().get(id);
        // Probably came here from Deeplink/Notification - if item is not in cache, fetch it
        if (mPost == null) {
            String postId;
            String wallId = null;
            if (id.contains("$$$")) {
                StringTokenizer parts = new StringTokenizer(id, "$$$");
                if (parts.countTokens() == 2) {
                    postId = parts.nextToken();
                    wallId = parts.nextToken();
                } else {
                    postId = id.replace("$$$", "");
                }
                WallModel.getInstance().getPostById(postId, wallId);
            }
            return view;
        } else {
            initializeWithData(true, mPost);
            hideReadMoreIfShort();
        }
        commentsAdapter = new CommentsAdapter(imgUri, mPost.getTypeAsInt());
        commentsAdapter.setTranslationView(translationView);
        translationView.setParentView(view);
        commentsList.setLayoutManager(commentLayoutManager);
        commentsList.setAdapter(commentsAdapter);

        String userId = Model.getInstance().getUserInfo().getUserId();
        if (mPost.getWallId() != null) {
            if (mPost.getWallId().equals(userId)) {
                delete.setVisibility(View.VISIBLE);
            }
        }
        post.setEnabled(Model.getInstance().isRealUser());
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
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    WallModel.getInstance().getCommentsForPost(mPost, commentsAdapter.getItemCount());
                }
            });
        }
        if (isAutoTranslateEnabled()) {
            TaskCompletionSource<WallBase> task = new TaskCompletionSource<>();
            task.getTask().addOnCompleteListener(new OnCompleteListener<WallBase>() {
                @Override
                public void onComplete(@NonNull Task<WallBase> task) {
                    if (task.isSuccessful()) {
                        WallBase translatedItem = task.getResult();
                        updateWithTranslatedItem(translatedItem);
                    }
                }
            });
            Translator.getInstance().translatePost(
                    mPost.getPostId(),
                    Prefs.getString(CHOSEN_LANGUAGE, "en"),
                    task,
                    mPost.getType()
            );
        }
        if (mPost instanceof WallStoreItem) {
            imageHeader.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageHeader.setBackgroundResource(R.color.white);
        } else {
            imageHeader.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (BuildConfig.DEBUG) {
            showDummyInfo();
        }

        return view;
    }

    protected void showDummyInfo() {
        post.setText("Test comment");
    }

    private void setClickListeners() {
        commentsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.requestFocus();
                Utility.showKeyboard(getContext());
            }
        });
    }

    private void initializeWithData(boolean fetchComments, WallBase item) {
        if (fetchComments) {
            WallModel.getInstance().getCommentsForPost(item);
        }
        switch (item.getType()) {
            case post:
            case wallStoreItem:
                WallBase post = item;
                ImageLoader.displayImage(post.getCoverImageUrl(), imageHeader,
                        R.drawable.wall_detail_header_placeholder);
                title.setText(post.getTitle());
                content.setText(post.getBodyText());
                if (post instanceof WallPost && ((WallPost) post).getVidUrl() != null) {
                    videoView.setVisibility(View.VISIBLE);
                    imageHeader.setVisibility(View.GONE);
                    videoView.setVideoURI(Uri.parse(((WallPost) post).getVidUrl()));
                    videoView.start();
                }
                postContainer.setVisibility(View.VISIBLE);

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
                ImageLoader.displayImage(news.getCoverImageUrl(), imageHeader, null);
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
        }
    }

    protected void hideReadMoreIfShort() {
        content.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boolean isNotEllipsized = ((content.getLayout().getText().toString())
                        .equalsIgnoreCase(mPost.getBodyText()));
                if (isNotEllipsized && content.getMaxLines() == 3) {
                    getView().findViewById(R.id.read_more_text).setVisibility(View.GONE);
                } else {
                    getView().findViewById(R.id.read_more_text).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        List<PostComment> comments = event.getCommentList();

        Collections.sort(comments, new Comparator<PostComment>() {
            @Override
            public int compare(PostComment lhs, PostComment rhs) {
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

    @OnClick(R.id.post_comment_button)
    public void postComment() {
        if (Model.getInstance().isRealUser()) {
            if (commentForEdit == null) {
                sendComment();
            } else {
                updateComment();
            }
        } else {
            Toast.makeText(getActivity(), "Please login to post comments", Toast.LENGTH_LONG).show();
        }
    }

    private void sendComment() {
        PostComment comment = new PostComment();
        comment.setComment(post.getText().toString());
        comment.setPosterId(new Id(Model.getInstance().getUserInfo().getUserId()));
        comment.setWallId(mPost.getWallId());
        comment.setPostId(mPost.getPostId());
        comment.setTimestamp((double) (Utility.getCurrentTime() / 1000));
        WallModel.getInstance().postComment(comment, mPost);
        post.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
        Utility.hideKeyboard(getActivity());
    }

    private void updateComment() {
        commentForEdit.setComment(post.getText().toString());
        WallModel.getInstance().updateComment(commentForEdit);
        post.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
    }

    PostComment commentForEdit;

    @Subscribe
    public void setCommentForEdit(CommentSelectedEvent event) {
        this.commentForEdit = event.getSelectedComment();
        post.setText(commentForEdit.getComment());
        post.requestFocus();
        post.setSelection(post.getText().length());
        Utility.showKeyboard(getContext());
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
        if (mPost != null) {
            initializeWithData(true, mPost);
        }
    }

    @Subscribe
    public void onCommentUpdated(final CommentUpdatedEvent event) {
        PostComment receivedComment = event.getComment();
        PostComment commentToUpdate = null;
        List<PostComment> commentsInAdapter = commentsAdapter.getComments();
        for (PostComment comment : commentsInAdapter) {
            if (comment.getId().getOid().equals(receivedComment.getId().getOid())) {
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

    @Subscribe
    public void onCommentReceived(final CommentReceiveEvent event) {
        WallBase wallItem = event.getWallItem();
        if (wallItem != null) {
            if (wallItem.getWallId().equals(mPost.getWallId())
                    && wallItem.getPostId().equals(mPost.getPostId())) {

                mPost.setCommentsCount(event.getWallItem().getCommentsCount());
                final PostComment comment = event.getComment();

                if (event.getComment() != null) {
                    Model.getInstance().getUserInfoById(comment.getPosterId().getOid())
                            .addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                                @Override
                                public void onComplete(@NonNull Task<UserInfo> task) {
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

    @Subscribe
    public void onDeleteComment(CommentDeleteEvent event) {
        if (event.getComment().getWallId().equals(mPost.getWallId())) {
            PostComment commentToDelete = event.getComment();

            for (PostComment comment : commentsAdapter.getComments()) {
                if (comment.getId().getOid().equals(commentToDelete.getId().getOid())) {
                    commentsAdapter.remove(comment);
                    commentsAdapter.notifyDataSetChanged();

                    commentsCount.setText(String.valueOf(event.getNewCommentCount()));
                    EventBus.getDefault().post(new ItemUpdateEvent(mPost));
                }
            }
        }
    }

    @OnClick({R.id.likes_container})
    public void togglePostLike() {
        if (Model.getInstance().isRealUser()) {
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

                mPost.toggleLike();
                if (likesIconLiked != null) {
                    likesIconLiked.setEnabled(false);
                }
                if (likesIcon != null) {
                    likesIcon.setVisibility(mPost.isLikedByUser() ? View.GONE : View.VISIBLE);
                }
                if (likesIconLiked != null) {
                    likesIconLiked.setVisibility(mPost.isLikedByUser() ? View.VISIBLE : View.GONE);
                }
                SoundEffects.getDefault().playSound(mPost.isLikedByUser() ? SoundEffects.ROLL_OVER : SoundEffects.SOFT);
            }
        }
    }

    @Subscribe
    public void onPostUpdate(ItemUpdateEvent event) {
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
    public void deletePostOnClick(View view) {
        WallModel.getInstance().deletePost(mPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().onBackPressed();
            }
        });
    }

    @Optional
    @OnClick(R.id.share_container)
    public void sharePost() {
        if (Model.getInstance().isRealUser()) {
            SharingManager.getInstance().share(getContext(), mPost);
        } else {
            Toast.makeText(getContext(),
                    "Please login to share posts",
                    Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.translate)
    public void onTranslateClick(View view) {
        TaskCompletionSource<WallBase> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<WallBase>() {
            @Override
            public void onComplete(@NonNull Task<WallBase> task) {
                if (task.isSuccessful()) {
                    WallBase translatedPost = task.getResult();
                    updateWithTranslatedItem(translatedPost);
                }
            }
        });
        translationView.showTranslationPopup(view, mPost.getPostId(), source,
                TranslationType.TRANSLATE_WALL, mPost.getType());
    }

    private void updateWithTranslatedItem(WallBase translatedPost) {
        initializeWithData(false, translatedPost);
    }

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }
}