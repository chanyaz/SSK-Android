package base.app.fragment.instance;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import base.app.R;
import base.app.adapter.CommentsAdapter;
import base.app.adapter.TutorialStepAdapter;
import base.app.events.CommentUpdateEvent;
import base.app.events.GetCommentsCompleteEvent;
import base.app.events.PostCommentCompleteEvent;
import base.app.events.PostUpdateEvent;
import base.app.fragment.BaseFragment;
import base.app.model.Model;
import base.app.model.sharing.SharingManager;
import base.app.model.tutorial.WallTip;
import base.app.model.user.UserInfo;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallNewsShare;
import base.app.model.wall.WallPost;
import base.app.model.wall.WallStoreItem;
import base.app.util.SoundEffects;
import base.app.util.Utility;
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
    @BindView(R.id.strap)
    TextView strap;
    @BindView(R.id.content_text)
    TextView content;
    @BindView(R.id.close_news_button)
    Button close;
    @BindView(R.id.share_news_to_wall_button)
    Button share;
    @BindView(R.id.content_video)
    VideoView videoView;
    @BindView(R.id.comments_wall)
    RecyclerView commentsList;
    @Nullable
    @BindView(R.id.read_more_arrow_image)
    ImageView readMoreArrowImage;

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
    @BindView(R.id.top_buttons_contaniner)
    LinearLayout buttonsContainer;
    @Nullable
    @BindView(R.id.header_container)
    RelativeLayout headerContainer;
    @Nullable
    @BindView(R.id.top_horizontal_split_line)
    View topSplitLine;
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
    @BindView(R.id.tutorial_container_bottom)
    LinearLayout bottomTutorialContainer;
    @Nullable
    @BindView(R.id.tutorial_bottom_message)
    TextView bottomMessage;
    @Nullable
    @BindView(R.id.share_icon)
    ImageView shareButton;

    @Nullable
    @BindView(R.id.share_buttons_container)
    View shareButtonsContainer;

    @Nullable
    @BindView(R.id.tutorial_earn_button)
    RelativeLayout tipEarnButton;

    @Nullable
    @BindView(R.id.rumours_swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;

    CommentsAdapter commentsAdapter;
    WallBase item;
    List<PostComment> comments;

    public WallItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        comments = new ArrayList<>();
        String id = getPrimaryArgument();
        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentsAdapter = new CommentsAdapter(comments);
        commentsList.setLayoutManager(commentLayoutManager);
        commentsList.setAdapter(commentsAdapter);

        if (pinContainer != null) {
            pinContainer.setVisibility(View.GONE);
        }

        item = WallBase.getCache().get(id);
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();

        WallModel.getInstance().getCommentsForPost(item);
        switch (item.getType()) {
            case post:
                WallPost post = (WallPost) item;
                Glide.with(this).load(post.getCoverImageUrl()).into(imageHeader);
                title.setText(post.getTitle());
                strap.setText(Utility.getTimeAgo(post.getTimestamp().longValue()));
                content.setText(post.getBodyText());
                if (post.getVidUrl() != null) {
                    videoView.setVisibility(View.VISIBLE);
                    imageHeader.setVisibility(View.GONE);
                    videoView.setVideoURI(Uri.parse(post.getVidUrl()));
                    videoView.start();
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
//                    videoView.setVisibility(View.GONE);
                        }
                    });
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
                ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), imageHeader, imageOptions);
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
                ImageLoader.getInstance().displayImage(storeItem.getCoverImageUrl(), imageHeader, imageOptions);
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
                if (topSplitLine != null) {
                    topSplitLine.setVisibility(View.GONE);
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
                if (bottomTutorialContainer != null) {
                    bottomTutorialContainer.setVisibility(View.VISIBLE);
                }
                if (bottomMessage != null) {
                    bottomMessage.setText(tip.getTipEnding());
                }
                if (tipEarnButton != null) {
                    tipEarnButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Model.getInstance().markWallTipComplete(String.valueOf(tip.getTipNumber()));
                            tip.markAsSeen();
                            getActivity().onBackPressed();
                        }
                    });
                }
                break;
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        post.addTextChangedListener(new TextWatcher() {

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

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        if(event.getCommentList()!=null){
            comments.addAll(event.getCommentList());

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

    @OnClick(R.id.share_container)
    public void onShareClick(View view) {
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
            PostComment comment = new PostComment();
            comment.setComment(post.getText().toString());
            comment.setPosterId(Model.getInstance().getUserInfo().getUserId());
            comment.setWallId(item.getWallId());
            comment.setPostId(item.getPostId());
            comment.setTimestamp((double) (Utility.getCurrentTime() / 1000));

            WallModel.getInstance().postComment(item, comment);
            post.getText().clear();
            postCommentProgressBar.setVisibility(View.VISIBLE);
        }
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
    public void onCommentReceived(final CommentUpdateEvent event) {
        WallBase eventsWallPost = event.getWallItem();
        if (eventsWallPost != null) {
            if (eventsWallPost.getWallId().equals(item.getWallId())
                    && eventsWallPost.getPostId().equals(item.getPostId())) {

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


    @Optional
    @OnClick(R.id.read_more_holder)
    public void readMoreClick() {
        if (content.getMaxLines() == 3) {
            content.setMaxLines(Integer.MAX_VALUE);
            if (readMoreArrowImage != null) {
                readMoreArrowImage.setRotation(90);
            }
        } else {
            content.setMaxLines(3);
            if (readMoreArrowImage != null) {
                readMoreArrowImage.setRotation(-90);
            }
        }
    }
}