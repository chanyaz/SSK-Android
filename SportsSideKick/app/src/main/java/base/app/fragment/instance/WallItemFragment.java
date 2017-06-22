package base.app.fragment.instance;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;

import base.app.R;
import base.app.activity.PhoneLoungeActivity;
import base.app.adapter.CommentsAdapter;
import base.app.adapter.TutorialStepAdapter;
import base.app.events.GetCommentsCompleteEvent;
import base.app.events.PostCommentCompleteEvent;
import base.app.events.PostUpdateEvent;
import base.app.fragment.BaseFragment;
import base.app.model.Model;
import base.app.model.sharing.SharingManager;
import base.app.model.tutorial.WallTip;
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
    @BindView(R.id.post_post_button)
    ImageView postButton;

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

    CommentsAdapter commentsAdapter;
    WallBase item;


    public WallItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof PhoneLoungeActivity) {
            ((PhoneLoungeActivity) getActivity()).setMarginTop(true);
        }
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        LinearLayoutManager commentLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentsAdapter = new CommentsAdapter();
        commentsList.setLayoutManager(commentLayoutManager);
        commentsList.setAdapter(commentsAdapter);

        pinContainer.setVisibility(View.GONE);

        item = WallBase.getCache().get(id);
        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();

        WallModel.getInstance().getCommentsForPost(item);
        switch (item.getType()) {
            case post:
                WallPost post = (WallPost) item;
                ImageLoader.getInstance().displayImage(post.getCoverImageUrl(), imageHeader, imageOptions);
                title.setText(post.getTitle());
                strap.setText(Utility.getTimeAgo(post.getTimestamp().longValue()));
                content.setText(post.getBodyText());
                if (post.getVidUrl() != null) {
                    videoView.setVisibility(View.VISIBLE);
                    imageHeader.setVisibility(View.GONE);
                    videoView.setVideoURI(Uri.parse(item.getVidUrl()));
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

                commentsCount.setText(String.valueOf(post.getCommentsCount()));
                likesCount.setText(String.valueOf(post.getLikeCount()));
                shareCount.setText(String.valueOf(post.getShareCount()));
                if (post.isLikedByUser()) {
                    likesIcon.setVisibility(View.GONE);
                    likesIconLiked.setVisibility(View.VISIBLE);
                }
                break;
            case rumor:
            case newsShare:
                WallNewsShare news = (WallNewsShare) item;
                ImageLoader.getInstance().displayImage(news.getCoverImageUrl(), imageHeader, imageOptions);
                title.setText(news.getTitle());
                content.setText(news.getBodyText());

                commentsCount.setText(String.valueOf(news.getCommentsCount()));
                likesCount.setText(String.valueOf(news.getLikeCount()));
                shareCount.setText(String.valueOf(news.getShareCount()));
                if (news.isLikedByUser()) {
                    likesIcon.setVisibility(View.GONE);
                    likesIconLiked.setVisibility(View.VISIBLE);
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

                buttonsContainer.setVisibility(View.GONE);
                headerContainer.setVisibility(View.GONE);
                topSplitLine.setVisibility(View.GONE);
                bottomSplitLine.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                tutorialContainer.setVisibility(View.VISIBLE);
                tutorialTitle.setText(tip.getTipTittle());
                tutorialDescription.setText(tip.getTipDescription());
                bottomTutorialContainer.setVisibility(View.VISIBLE);
                bottomMessage.setText(tip.getTipEnding());
                tipEarnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Model.getInstance().markWallTipComplete(String.valueOf(tip.getTipNumber()));
                        tip.markAsSeen();
                        getActivity().onBackPressed();
                    }
                });
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

        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            post.setEnabled(true);
        }else {
            post.setEnabled(false);
        }

        return view;
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        // Sort by timestamp
        Collections.sort(event.getCommentList(), new Comparator<PostComment>() {
            @Override
            public int compare(PostComment lhs, PostComment rhs) {
                return rhs.getTimestamp().compareTo(lhs.getTimestamp());
            }
        });
        commentsAdapter.getComments().addAll(event.getCommentList());
        commentsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.share_container)
    public void onShareClick(View view) {
        shareButtonsContainer.setVisibility(View.VISIBLE);

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

    @OnClick(R.id.post_post_button)
    public void newPost() {
        PostComment comment = new PostComment();
        comment.setComment(post.getText().toString());
        comment.setPosterId(Model.getInstance().getUserInfo().getUserId());
        comment.setWallId(item.getWallId());
        comment.setPostId(item.getPostId());
        comment.setTimestamp((double) (System.currentTimeMillis() / 1000));

        WallModel.getInstance().postComment(item, comment);
        post.getText().clear();
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        commentsAdapter.getComments().add(0,event.getComment());
        commentsAdapter.notifyDataSetChanged();
       // commentsList.scrollToPosition(commentsAdapter.getComments().size() - 1);

    }

    @OnClick(R.id.likes_icon)
    public void likePost() {
        if(Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL){
            if (item!=null)
            {
                likesCount.setText(String.valueOf(item.getLikeCount()+ 1));
            }
            WallModel.getInstance().setlikeVal(item, true);
            likesIcon.setVisibility(View.GONE);
            likesIconLiked.setVisibility(View.VISIBLE);
        }else {
            //TODO Notify user that need to login in order to LIKE
        }

        SoundEffects.getDefault().playSound(SoundEffects.SOFT);
    }

    @OnClick(R.id.likes_icon_liked)
    public void unLikePost() {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
        if (item != null) {
            int count = Integer.valueOf(likesCount.getText().toString());
            if (count > 0) {
                likesCount.setText(String.valueOf(count - 1));
            } else if (count == 0) {
                likesCount.setText("0");
            }
        }
        WallModel.getInstance().setlikeVal(item, false);
        likesIcon.setVisibility(View.VISIBLE);
        likesIconLiked.setVisibility(View.GONE);
    }else{
            //TODO Notify user that need to login in order to UNLIKE
           // This should never happen
    }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
    }

    @Subscribe
    public void onPostUpdate(PostUpdateEvent event) {
        WallBase post = event.getPost();
        if ((post != null)) {
            commentsCount.setText(String.valueOf(post.getCommentsCount()));
            likesCount.setText(String.valueOf(post.getLikeCount()));
            shareCount.setText(String.valueOf(post.getShareCount()));
        }
    }

    @OnClick(R.id.share_facebook)
    public void sharePostFacebook(View view) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            SharingManager.getInstance().share(getContext(), item, false, SharingManager.ShareTarget.facebook, view);
        }else {
            //TODO Notify user that need to login in order to SHARE
        }

    }

    @OnClick(R.id.share_twitter)
    public void sharePostTwitter(View view) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
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
        }else {
            //TODO Notify user that need to login in order to SHARE
        }

    }


    @Optional
    @OnClick(R.id.read_more_holder)
    public void readMoreClick() {
        if (content.getMaxLines() == 3) {
            content.setMaxLines(Integer.MAX_VALUE);
            readMoreArrowImage.setRotation(90);
        } else {
            content.setMaxLines(3);
            readMoreArrowImage.setRotation(-90);
        }
    }
}