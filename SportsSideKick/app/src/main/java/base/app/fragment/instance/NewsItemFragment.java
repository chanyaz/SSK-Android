package base.app.fragment.instance;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import base.app.R;
import base.app.adapter.CommentsAdapter;
import base.app.events.GetCommentsCompleteEvent;
import base.app.events.PostCommentCompleteEvent;
import base.app.events.PostUpdateEvent;
import base.app.fragment.BaseFragment;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.news.NewsModel;
import base.app.model.news.NewsPageEvent;
import base.app.model.sharing.SharingManager;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallNews;
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
public class NewsItemFragment extends BaseFragment {

    private static final String TAG = "NewsItemFragment";
    @BindView(R.id.content_image)
    ImageView imageHeader;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.strap)
    TextView strap;
    @BindView(R.id.content_text)
    TextView content;
    @Nullable
    @BindView(R.id.close_news_button)
    Button close;
    @BindView(R.id.share_news_to_wall_button)
    Button share;

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
    @BindView(R.id.read_more_arrow_image)
    ImageView readMoreArrowImage;


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
    @BindView(R.id.rumours_swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;

    CommentsAdapter commentsAdapter;
    WallNews item;

    public NewsItemFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.share_facebook)
    public void sharePostFacebook(View view) {
        if (Model.getInstance().getLoggedInUserType() == Model.LoggedInUserType.REAL) {
            SharingManager.getInstance().share(getContext(),  item, false, SharingManager.ShareTarget.facebook, view);
        } else {
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
        } else {
            //TODO Notify user that need to login in order to SHARE
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        LinearLayoutManager commentLayouManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentsAdapter = new CommentsAdapter();
        commentsList.setLayoutManager(commentLayouManager);
        commentsList.setAdapter(commentsAdapter);


        NewsModel.NewsType type = NewsModel.NewsType.OFFICIAL;
        if (id.contains("UNOFFICIAL$$$")) {
            id = id.replace("UNOFFICIAL$$$", "");
            type = NewsModel.NewsType.UNOFFICIAL;
        }

        item = NewsModel.getInstance().getCachedItemById(id, type);

        if (NewsModel.getInstance().getAllCachedItems(type).size() > 0) {
            GetCommentsCompleteEvent event = new GetCommentsCompleteEvent(WallModel.getInstance().getAllCommentsCachedItems(WallModel.CommentType.NEWS_COMMENT));
            if (event != null) {
                onCommentsReceivedEvent(event);
            } else {
                WallModel.getInstance().setLoading(false, WallModel.CommentType.NEWS_COMMENT);
                WallModel.getInstance().getCommentsForPost(item,WallModel.CommentType.NEWS_COMMENT);
            }
        } else {
            WallModel.getInstance().getCommentsForPost(item,WallModel.CommentType.NEWS_COMMENT);
        }

        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        if (item.getCoverImageUrl() != null) {
            ImageLoader.getInstance().displayImage(item.getCoverImageUrl(), imageHeader, imageOptions);
        }
        title.setText(item.getTitle());
        String time = "" + DateUtils.getRelativeTimeSpanString(item.getTimestamp().longValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
        if (item.getSubTitle() != null) {
            strap.setText(item.getSubTitle() + " - " + time);
        } else {
            strap.setText(time);
        }

        content.setText(item.getBodyText());


        postContainer.setVisibility(View.VISIBLE);
        WallModel.getInstance().getCommentsForPost(item,WallModel.CommentType.NEWS_COMMENT);
        commentsList.setNestedScrollingEnabled(false);

        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(item.getCommentsCount()));
        }
        if (likesCount != null) {
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

        if (close != null) {
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
        if(Model.getInstance().isRealUser()){
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
        }else {
            post.setEnabled(false);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    WallModel.getInstance().setLoading(false,WallModel.CommentType.NEWS_COMMENT);
                    WallModel.getInstance().getCommentsForPost(item,WallModel.CommentType.NEWS_COMMENT);
                  //  NewsModel.getInstance().setLoading(false, type);
                  //  NewsModel.getInstance().loadPage(type);

                }
            });
        }


        return view;
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        List<PostComment> commentList = event.getCommentList();

//        //Delete duplicates from list
//        Set<PostComment> set = new HashSet<>();
//        set.addAll(commentList);
//        commentList.clear();
//        commentList.addAll(set);

        // Sort by timestamp
        Collections.sort(commentList, new Comparator<PostComment>() {
            @Override
            public int compare(PostComment lhs, PostComment rhs) {
                return rhs.getTimestamp().compareTo(lhs.getTimestamp());
            }
        });
        commentsAdapter.getComments().addAll(commentList);
        commentsAdapter.notifyDataSetChanged();
        item.setCommentsCount(commentsAdapter.getComments().size());
        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.post_comment_button)
    public void postComment() {
        if(Model.getInstance().isRealUser()){
            PostComment comment = new PostComment();
            comment.setComment(post.getText().toString());
            comment.setPosterId(Model.getInstance().getUserInfo().getUserId());
            comment.setWallId(item.getWallId());
            comment.setPostId(item.getPostId());
            comment.setTimestamp((double) (System.currentTimeMillis() / 1000));
            WallModel.getInstance().postComment(item, comment);
            post.getText().clear();
            postCommentProgressBar.setVisibility(View.VISIBLE);
        }else {
            //TODO notify user
        }
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        postCommentProgressBar.setVisibility(View.GONE);
        commentsAdapter.getComments().add(0, event.getComment());
        commentsAdapter.notifyDataSetChanged();
        commentsList.scrollToPosition(commentsAdapter.getComments().size() - 1);
        item.setCommentsCount(commentsAdapter.getComments().size());
        if (commentsCount != null) {
            commentsCount.setText(String.valueOf(commentsAdapter.getComments().size()));
        }

    }

    @OnClick({R.id.likes_icon_liked,R.id.likes_icon})
    public void togglePostLike() {
        if(Model.getInstance().isRealUser()) {
            if (item != null) {
                item.toggleLike();
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

    @OnClick(R.id.pin_container)
    public void pinToWall() {
        if(Model.getInstance().isRealUser()){
            AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.news_post_to_wall_title), getContext().getResources().getString(R.string.news_post_to_wall_message),
                    new View.OnClickListener() {// Cancel
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    }, new View.OnClickListener() { // Confirm
                        @Override
                        public void onClick(View v) {
                            WallNews itemToPost = new WallNews();
                            itemToPost.setBodyText(item.getBodyText());
                            itemToPost.setTitle(item.getTitle());

                            if(item.getSource()!=null){
                                itemToPost.setSubTitle(item.getSource());
                            } else {
                                itemToPost.setSubTitle("");
                            }

                            itemToPost.setTimestamp((double) System.currentTimeMillis());
                            itemToPost.setCoverAspectRatio(0.666666f);
                            if(item.getCoverImageUrl()!=null){
                                itemToPost.setCoverImageUrl(item.getCoverImageUrl());
                            }
                            WallModel.getInstance().mbPost(itemToPost);
                            getActivity().onBackPressed();
                        }
                    });
        }else {
            //TODO notify user
        }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);
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
