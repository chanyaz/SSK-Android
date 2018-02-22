package base.app.ui.fragment.content;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import base.app.BuildConfig;
import base.app.R;
import base.app.data.Id;
import base.app.data.Model;
import base.app.data.Translator;
import base.app.data.news.NewsModel;
import base.app.data.sharing.SharingManager;
import base.app.data.user.UserInfo;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallBase;
import base.app.data.wall.WallModel;
import base.app.data.wall.WallNews;
import base.app.ui.activity.MainActivity;
import base.app.ui.adapter.content.CommentsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import base.app.util.events.comment.CommentDeleteEvent;
import base.app.util.events.comment.CommentSelectedEvent;
import base.app.util.events.comment.CommentUpdatedEvent;
import base.app.util.events.comment.GetCommentsCompleteEvent;
import base.app.util.events.post.ItemUpdateEvent;
import base.app.util.events.post.PostCommentCompleteEvent;
import base.app.util.ui.ImageLoader;
import base.app.util.ui.TranslationView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.data.wall.WallBase.PostType.newsShare;
import static base.app.data.wall.WallBase.PostType.rumourShare;
import static base.app.data.wall.WallBase.PostType.social;
import static base.app.data.wall.WallBase.PostType.socialShare;
import static base.app.ui.fragment.popup.ProfileFragment.isAutoTranslateEnabled;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;
import static base.app.util.commons.Utility.getCurrentTime;
import static base.app.util.commons.Utility.hideKeyboard;
import static base.app.util.commons.Utility.showKeyboard;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsItemFragment extends BaseFragment {

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
    @BindView(R.id.dateLabel)
    TextView dateLabel;
    @Nullable
    @BindView(R.id.share_news_to_wall_button)
    Button share;
    @BindView(R.id.comments_wall)
    RecyclerView commentsListView;
    @BindView(R.id.post_container)
    View postContainer;
    @BindView(R.id.post_text)
    EditText inputFieldComment;
    @BindView(R.id.post_comment_button)
    ImageButton postButton;
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
    SwipeRefreshLayout swipeRefreshLayout;
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
    @BindView(R.id.comments_container)
    View commentsContainer;
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
    @BindView(R.id.read_more_text)
    TextView readMoreText;
    @BindView(R.id.contentTextWrapper)
    ViewGroup contentTextWrapper;

    CommentsAdapter commentsAdapter;
    WallNews item;
    private WallBase sharedChildPost;
    List<PostComment> comments;

    public NewsItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_news_item,
                container,
                false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        if (getSecondaryArgument() != null && getSecondaryArgument().contains("UNOFFICIAL$$$")) {
            id = "UNOFFICIAL$$$" + id;
        }
        id = id.replace("UNOFFICIAL$$$UNOFFICIAL$$$", "UNOFFICIAL$$$");

        item = loadFromCacheBy(id);
        if (item == null) return view;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setClickListeners();
        if (item == null) return;

        showHeaderImage();
        showTextContent(item);

        showSharingPreviewImage();
        showSharingAvatar();

        WallModel.getInstance().getCommentsForPost(item);
        showCommentsLikesCount();
        showComments(view);

        autoHideShowPostButton();
        if (!Model.getInstance().isRealUser()) {
            inputFieldComment.setEnabled(false);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    WallModel.getInstance().getCommentsForPost(item, comments.size());
                }
            });
        }

        sharedMessageField.setHint(R.string.pin_message_hint);

        if (getSecondaryArgument() != null) {
            setSharedMessageBarVisible(true);
            sharedChildPost = WallBase.getCache().get(getSecondaryArgument());
            if (sharedChildPost != null) {
                showSharedMessageAvatar();
                sharedMessageField.setText(sharedChildPost.getSharedComment());
            }
        } else {
            setSharedMessageBarVisible(false);
        }
        autoTranslateIfNeeded();

        if (BuildConfig.DEBUG) {
            showDummyInfo();
        }
    }

    protected void showDummyInfo() {
        EditText sharedMessageField = commentInputOverlay.findViewById(R.id.post_text);
        sharedMessageField.setText("Check this out!");
        inputFieldComment.setText("Test");
    }

    private void autoTranslateIfNeeded() {
        if (isAutoTranslateEnabled() && item.isNotTranslated()) {
            TaskCompletionSource<WallNews> task = new TaskCompletionSource<>();
            task.getTask().addOnCompleteListener(new OnCompleteListener<WallNews>() {
                @Override
                public void onComplete(@NonNull Task<WallNews> task) {
                    if (task.isSuccessful()) {
                        WallNews translatedItem = task.getResult();
                        updateWithTranslatedPost(translatedItem);
                    }
                }
            });
            if (item.getType() == WallBase.PostType.social) {
                Translator.getInstance().translateSocial(
                        item.getPostId(),
                        Prefs.getString(CHOSEN_LANGUAGE, "en"),
                        task
                );
            } else {
                Translator.getInstance().translateNews(
                        item.getPostId(),
                        Prefs.getString(CHOSEN_LANGUAGE, "en"),
                        task
                );
            }
        }
    }

    private void showSharedMessageAvatar() {
        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(sharedChildPost.getWallId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    UserInfo user = task.getResult();
                    if (user != null) {
                        if (getContext() != null) {
                            ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), sharedMessageAvatar);
                        }
                    }
                }
            }
        });
    }

    @Optional
    @OnClick(R.id.delete)
    public void deletePostOnClick(View view) {
        WallModel.getInstance().deletePost(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setSharedMessageBarVisible(boolean setVisible) {
        if (setVisible) {
            sharedMessageBar.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
        } else {
            sharedMessageBar.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
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
                WallModel.getInstance().deletePost(sharedChildPost)
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
                if (getPrimaryArgument().contains("UNOFFICIAL")) {
                    pin(rumourShare);
                } else if (item.getType() == social) {
                    pin(socialShare);
                } else {
                    pin(newsShare);
                }
            }
        });
        commentsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Model.getInstance().isRealUser()) {
                    inputFieldComment.requestFocus();
                    Utility.showKeyboard(getContext());
                } else {
                    Toast.makeText(getActivity(), "Please login to post comments", Toast.LENGTH_LONG).show();
                }
            }
        });
        commentInputOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentInputOverlay.setVisibility(View.GONE);
            }
        });
        contentTextWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getMaxLines() == 3) {
                    content.setMaxLines(Integer.MAX_VALUE);
                    contentTextWrapper.setClickable(false);
                    contentTextWrapper.forceLayout();
                    contentTextWrapper.requestLayout();
                    content.setTextIsSelectable(true);
                    readMoreText.setText(R.string.read_less);
                }
            }
        });
    }

    private void showCommentsLikesCount() {
        postContainer.setVisibility(View.VISIBLE);

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
            likesCountHeader.setTextColor(getContext().getResources()
                    .getColor(R.color.colorAccent));
        }
        if (item.getReferencedItemId() != null && !item.getReferencedItemId().isEmpty()) {
            pinIcon.setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.colorAccentSemiDark),
                    PorterDuff.Mode.MULTIPLY);
        }
    }

    private WallNews loadFromCacheBy(String id) {
        NewsModel.NewsType type = NewsModel.NewsType.OFFICIAL;
        if (id.contains("UNOFFICIAL$$$")) {
            type = NewsModel.NewsType.UNOFFICIAL;
        }
        if (NewsModel.getInstance().loadItemFromCache(id, type) == null) {
            item = NewsModel.getInstance().loadItemFromCache(id, NewsModel.NewsType.SOCIAL);
            if (item != null) {
                item.setType(WallBase.PostType.social);
            }
            return item;
        }
        return NewsModel.getInstance().loadItemFromCache(id, type);
    }

    private void showHeaderImage() {
        imageHeader.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.displayImage(item.getCoverImageUrl(), imageHeader,
                R.drawable.wall_detail_header_placeholder);
    }

    private void showSharingPreviewImage() {
        ImageLoader.displayImage(item.getCoverImageUrl(),
                image,
                R.drawable.wall_detail_header_placeholder);
    }

    private void showTextContent(WallNews item) {
        title.setText(item.getTitle());
        textContent.setText(item.getTitle());
        if (item.getContent() != null && !item.getContent().trim().isEmpty()) {
            // double conversion to change html symbol codes to corresponding characters
            content.setText(Html.fromHtml(Html.fromHtml(item.getContent()).toString()));
        } else {
            content.setVisibility(View.GONE);
        }

        TextView pinPreviewBody = commentInputOverlay.findViewById(R.id.subheadTextView);
        pinPreviewBody.setText(item.getContent());

        if (item.getTimestamp() != null) {
            String time = "" + DateUtils.getRelativeTimeSpanString(
                    (long) (item.getTimestamp() * 1000),
                    Utility.getCurrentTime(),
                    DateUtils.MINUTE_IN_MILLIS
            );
            dateLabel.setText(time);
        }
        if (item.getSource() != null) {
            if (!dateLabel.getText().toString().isEmpty()
                    && !dateLabel.getText().toString().equals(item.getSource())) {
                dateLabel.setText(item.getSource() + " - " + dateLabel.getText());
            } else {
                dateLabel.setText(item.getSource());
            }
        }

        hideReadMoreIfShort();
    }

    private void showSharingAvatar() {
        Task<UserInfo> getUserTask = Model.getInstance().getUserInfoById(item.getWallId());
        getUserTask.addOnCompleteListener(new OnCompleteListener<UserInfo>() {
            @Override
            public void onComplete(@NonNull Task<UserInfo> task) {
                if (task.isSuccessful()) {
                    UserInfo user = task.getResult();
                    if (user != null) {
                        ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), authorUserImage);
                    }
                }
            }
        });
    }

    private void showComments(View view) {
        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        comments = new ArrayList<>();
        String imgUri = "drawable://" + getResources().getIdentifier("blank_profile_rounded", "drawable", getActivity().getPackageName());
        commentsAdapter = new CommentsAdapter(imgUri, item.getTypeAsInt());
        commentsListView.setLayoutManager(commentLayoutManager);
        commentsListView.setAdapter(commentsAdapter);
        commentsAdapter.setTranslationView(translationView);
        translationView.setParentView(view);
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
                    return Double.compare(rhs.getTimestamp(), lhs.getTimestamp());
                }
            });
            commentsAdapter.clear();
            commentsAdapter.addAll(comments);
            commentsAdapter.notifyDataSetChanged();
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.post_comment_button)
    public void postComment() {
        if (Model.getInstance().isRealUser()) {
            if (commentForEdit == null) {
                PostComment comment = new PostComment();
                comment.setComment(inputFieldComment.getText().toString());
                comment.setPosterId(new Id(Model.getInstance().getUserInfo().getUserId()));
                comment.setWallId(item.getWallId());
                comment.setPostId(item.getPostId());
                comment.setTimestamp((double) (getCurrentTime() / 1000));
                WallModel.getInstance().postComment(comment, item);
            } else {
                commentForEdit.setWallId(item.getPostId());
                updateComment();
            }
            Utility.hideKeyboard(getActivity());
            postCommentProgressBar.setVisibility(View.VISIBLE);
            inputFieldComment.getText().clear();
        } else {
            Toast.makeText(getActivity(), "Please login to post comments", Toast.LENGTH_LONG).show();
        }
    }

    private void updateComment() {
        commentForEdit.setComment(inputFieldComment.getText().toString());
        WallModel.getInstance().updateComment(commentForEdit);
        inputFieldComment.getText().clear();
        postCommentProgressBar.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        postCommentProgressBar.setVisibility(View.GONE);
        commentsAdapter.getComments().add(0, event.getComment());
        commentsAdapter.notifyItemInserted(0);
        commentsListView.scrollToPosition(commentsAdapter.getComments().size() - 1);
        item.setCommentsCount(commentsAdapter.getComments().size());
        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
            commentsCountHeader.setText(String.valueOf(commentsAdapter.getComments().size()));
        }
    }

    @Subscribe
    public void onDeleteComment(CommentDeleteEvent event) {
        PostComment deletedComment = event.getComment();
        if (deletedComment.getPostId().equals(item.getPostId())) {
            for (PostComment comment : commentsAdapter.getComments()) {
                if (comment.getId().getOid().equals(deletedComment.getId().getOid())) {
                    commentsAdapter.remove(comment);
                    commentsAdapter.notifyDataSetChanged();

                    item.setCommentsCount(event.getNewCommentCount());
                    commentsCount.setText(String.valueOf(item.getCommentsCount()));
                    commentsCountHeader.setText(String.valueOf(item.getCommentsCount()));

                    EventBus.getDefault().post(new ItemUpdateEvent(item));
                }
            }
        }
    }

    @OnClick({R.id.likes_container})
    public void togglePostLike() {
        if (!Model.getInstance().isRealUser()) {
            Utility.toast(getActivity(), "Please sign in to like posts");
            return;
        }
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
            if (item.isLikedByUser()) {
                likesCountHeader.setTextColor(getContext().getResources()
                        .getColor(R.color.colorAccent));
            } else {
                likesCountHeader.setTextColor(getContext().getResources()
                        .getColor(R.color.white));
            }
            SoundEffects.getDefault().playSound(item.isLikedByUser() ? SoundEffects.ROLL_OVER : SoundEffects.SOFT);
        }
    }

    @Subscribe
    public void onPostUpdate(ItemUpdateEvent event) {
        WallBase post = event.getPost();
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

    @Optional
    @OnClick(R.id.share_container)
    public void sharePost() {
        if (Model.getInstance().isRealUser()) {
            SharingManager.getInstance().share(getContext(), item);
        } else {
            Toast.makeText(getContext(),
                    "Please login to share news",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void pin(WallBase.PostType type) {
        EditText sharedMessageField = commentInputOverlay.findViewById(R.id.post_text);
        String sharingMessage = sharedMessageField.getText().toString();

        WallBase itemToPost = new WallNews();
        itemToPost.setTimestamp((double) Utility.getCurrentTime());
        itemToPost.setTitle(item.getTitle() != null ? item.getTitle() : item.getMessage());
        if (type == socialShare) {
            itemToPost.setStrap(item.getTitle() != null ? item.getTitle() : item.getMessage());
        }
        itemToPost.setCoverImageUrl(item.getCoverImageUrl() != null ? item.getCoverImageUrl() : item.getImage());
        if (sharingMessage.isEmpty()) {
            itemToPost.setBodyText(item.getBodyText());
            itemToPost.setCoverAspectRatio(0.6f);
            itemToPost.setSubTitle(item.getSource() != null ? item.getSource() : "");
        } else {
            itemToPost.setType(type);
            itemToPost.setReferencedItemClub(Utility.getClubConfig().get("ID"));
            itemToPost.setReferencedItemId(item.getPostId());
            itemToPost.setSharedComment(sharingMessage);
        }
        itemToPost.setReferencedItem(item);
        WallModel.getInstance().createPost(itemToPost);

        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
    }

    @OnClick(R.id.pin_container)
    public void showSharedCommentOverlay() {
        if (Model.getInstance().isRealUser()) {
            commentInputOverlay.setVisibility(View.VISIBLE);

            showKeyboard(getContext());

            EditText sharedMessageField = commentInputOverlay.findViewById(R.id.post_text);
            sharedMessageField.requestFocus();
        } else {
            Toast.makeText(getContext(),
                    "Please login to pin articles",
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
                sharedChildPost.setSharedComment(newSharedComment);
                sharedChildPost.setTimestamp((double) (getCurrentTime() / 1000));
                WallModel.getInstance().updatePost(sharedChildPost)
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
        if (item.getType() == WallBase.PostType.social) {
            translationView.showTranslationPopup(
                    view, postId, source, TranslationView.TranslationType.TRANSLATE_SOCIAL);
        } else {
            translationView.showTranslationPopup(
                    view, postId, source, TranslationView.TranslationType.TRANSLATE_NEWS);
        }
    }

    private void updateWithTranslatedPost(WallNews translatedNews) {
        showTextContent(translatedNews);
    }

    protected void hideReadMoreIfShort() {
        content.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boolean isNotEllipsized = ((content.getLayout().getText().toString())
                        .equalsIgnoreCase(item.getContent()));
                if (isNotEllipsized && content.getMaxLines() == 3) {
                    getView().findViewById(R.id.read_more_text).setVisibility(View.GONE);
                } else {
                    getView().findViewById(R.id.read_more_text).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        translationView.setVisibility(View.GONE);
    }

    PostComment commentForEdit;

    @Subscribe
    public void setCommentForEdit(CommentSelectedEvent event) {
        this.commentForEdit = event.getSelectedComment();
        inputFieldComment.setText(commentForEdit.getComment());
        inputFieldComment.requestFocus();
        inputFieldComment.setSelection(inputFieldComment.getText().length());
        Utility.showKeyboard(getContext());
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
}
