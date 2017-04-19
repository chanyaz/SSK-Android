package tv.sportssidekick.sportssidekick.fragment.instance;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.CommentsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.YourProfileFragment;
import tv.sportssidekick.sportssidekick.model.AlertDialogManager;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.wall.PostComment;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallModel;
import tv.sportssidekick.sportssidekick.model.sharing.SharingManager;
import tv.sportssidekick.sportssidekick.model.wall.WallNews;
import tv.sportssidekick.sportssidekick.model.news.NewsModel;
import tv.sportssidekick.sportssidekick.service.GetCommentsCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostCommentCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostUpdateEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 30.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class NewsItemFragment extends BaseFragment{

    private static final String TAG = "NewsItemFragment";
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

    @BindView(R.id.commnets_wall)
    RecyclerView commetsList;

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

    CommentsAdapter commentsAdapter;
    WallNews item;

    public NewsItemFragment() {
        // Required empty public constructor
    }

    WallNews item;

    @OnClick(R.id.share_icon)
    public void sharePost(View view){
        SharingManager.getInstance().share(item,true,SharingManager.ShareTarget.facebook,view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);

        String id = getPrimaryArgument();
        LinearLayoutManager commentLayouManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commentsAdapter = new CommentsAdapter();
        commetsList.setLayoutManager(commentLayouManager);
        commetsList.setAdapter(commentsAdapter);


        NewsModel.NewsType type = NewsModel.NewsType.OFFICIAL;
        if(id.contains("UNOFFICIAL$$$")){
            id = id.replace("UNOFFICIAL$$$","");
             type = NewsModel.NewsType.UNOFFICIAL;
        }
        item = NewsModel.getInstance().getCachedItemById(id,type);

        DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
        ImageLoader.getInstance().displayImage(item.getCoverImageUrl(), imageHeader, imageOptions);
        title.setText(item.getTitle());
        String time = "" + DateUtils.getRelativeTimeSpanString(item.getTimestamp().longValue(), System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
        if (item.getSubTitle() != null)
        {
            strap.setText(item.getSubTitle() + " - " + time);
        }else {
            strap.setText(time);
        }

        content.setText(item.getBodyText());


        postContainer.setVisibility(View.VISIBLE);
        WallModel.getInstance().getCommentsForPost(item);
        commetsList.setNestedScrollingEnabled(false);

        commentsCount.setText(String.valueOf(item.getCommentsCount()));
        likesCount.setText(String.valueOf(item.getLikeCount()));
        shareCount.setText(String.valueOf(item.getShareCount()));
        if (item.isLikedByUser())
        {
            likesIcon.setVisibility(View.GONE);
            likesIconLiked.setVisibility(View.VISIBLE);
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Subscribe
    public void onCommentsReceivedEvent(GetCommentsCompleteEvent event) {
        commentsAdapter.getComments().addAll(event.getCommentList());
        commentsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.post_post_button)
    public void newPost() {
        PostComment comment = new PostComment();
        comment.setComment(post.getText().toString());
        comment.setPosterId(Model.getInstance().getUserInfo().getUserId());
        comment.setWallId(item.getWallId());
        comment.setPostId(item.getPostId());
        comment.setTimestamp(Double.valueOf(System.currentTimeMillis() / 1000));

        WallModel.getInstance().postComment(item, comment);
        post.getText().clear();
    }

    @Subscribe
    public void onCommentPosted(PostCommentCompleteEvent event) {
        commentsAdapter.getComments().add(event.getComment());
        commentsAdapter.notifyDataSetChanged();
        commetsList.scrollToPosition(commentsAdapter.getComments().size() - 1);

    }

    @OnClick(R.id.likes_icon)
    public void likePost()
    {
        WallModel.getInstance().setlikeVal(item, true);
        likesIcon.setVisibility(View.GONE);
        likesIconLiked.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.likes_icon_liked)
    public void unLikePost()
    {
        WallModel.getInstance().setlikeVal(item, false);
        likesIcon.setVisibility(View.VISIBLE);
        likesIconLiked.setVisibility(View.GONE);
    }

    @Subscribe
    public void onPostUpdate(PostUpdateEvent event)
    {
        WallBase post = event.getPost();
        if ((post!=null))
        {
            commentsCount.setText(String.valueOf(post.getCommentsCount()));
            likesCount.setText(String.valueOf(post.getLikeCount()));
            shareCount.setText(String.valueOf(post.getShareCount()));
        }
    }

    @OnClick(R.id.pin_icon)
    public void pinToWall()
    {
        AlertDialogManager.getInstance().showAlertDialog("Post this to your wall?", "Would you like to post this to your wall?",
                new View.OnClickListener() {// Cancel
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        WallModel.getInstance().mbPost(item);
                        getActivity().onBackPressed();
                    }
                });
    }
}
