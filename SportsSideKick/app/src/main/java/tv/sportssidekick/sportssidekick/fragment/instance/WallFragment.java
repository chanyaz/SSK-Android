package tv.sportssidekick.sportssidekick.fragment.instance;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.WallAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.tutorial.TutorialModel;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallModel;
import tv.sportssidekick.sportssidekick.service.PostLoadCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostUpdateEvent;
import tv.sportssidekick.sportssidekick.util.StaggeredLayoutManagerItemDecoration;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

@IgnoreBackHandling
public class WallFragment extends BaseFragment {

    private static final String TAG = "WALL FRAGMENT";
    WallAdapter adapter;

    @BindView(R.id.fragment_wall_new_post)
    Button buttonNewPost;
    @BindView(R.id.fragment_wall_notification)
    Button buttonNotification;
    @BindView(R.id.fragment_wall_filter)
    Button buttonFilter;
    @BindView(R.id.fragment_wall_following)
    Button buttonFollowing;
    @BindView(R.id.fragment_wall_search)
    Button buttonSearch;
    @BindView(R.id.fragment_wall_full_screen)
    Button buttonFullScreen;
    @BindView(R.id.fragment_wall_recycler_view)
    RecyclerView wallRecyclerView;

    @BindView(R.id.your_post_container)
    RelativeLayout newPostContainer;
    @BindView(R.id.wall_filter_container)
    RelativeLayout filterContainer;
    @BindView(R.id.search_wall_post)
    RelativeLayout searchWallContainer;

    @BindView(R.id.post_commnent_button)
    ImageView postCommentButton;
    @BindView(R.id.comment_text)
    EditText commnetText;

    public WallFragment() {
        // Required empty public constructor
    }

    List<WallBase> wallItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);
        wallItems = new ArrayList<>();
        WallModel.getInstance();

        TutorialModel.getInstance().initialize(getActivity());

        boolean isNewPostVisible, isFillterVisible, isSeacrhVisible;

        isNewPostVisible = false;
        isFillterVisible = false;
        isSeacrhVisible = false;

        commnetText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                {
                    postCommentButton.setVisibility(View.VISIBLE);
                }
                else {
                    postCommentButton.setVisibility(View.GONE);
                }
            }
        });

        List<WallBase> cacheWallItems = WallModel.getInstance().getListCacheItems();
        if (cacheWallItems != null && cacheWallItems.size() != 0)
        {
            wallItems = cacheWallItems;
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            adapter = new WallAdapter(getActivity(), wallItems);
            if(wallRecyclerView!=null){
                wallRecyclerView.setAdapter(adapter);
                wallRecyclerView.addItemDecoration(new StaggeredLayoutManagerItemDecoration(16));
                wallRecyclerView.setLayoutManager(layoutManager);
            }
            adapter.notifyDataSetChanged();
        }

        return view;
    }

    @Subscribe
    public void onPostUpdate(PostUpdateEvent event){
       Log.d(TAG,"GOT POST!");
       WallBase post = event.getPost();
        if(post!=null){
            Log.d(TAG,"Post is:" + post.toString());
            wallItems.add(post);
        }
    }

    @Subscribe
    public void onPostsLoaded(PostLoadCompleteEvent event){
        Log.d(TAG,"ALL POSTS LOADED!");
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new WallAdapter(getActivity(), wallItems);
        if(wallRecyclerView!=null){
            wallRecyclerView.setAdapter(adapter);
            wallRecyclerView.addItemDecoration(new StaggeredLayoutManagerItemDecoration(16));
            wallRecyclerView.setLayoutManager(layoutManager);
        }
        WallModel.getInstance().addToCache(wallItems);
        adapter.notifyDataSetChanged();
    }

    boolean isNewPostVisible, isFillterVisible, isSearchVisible;


    @OnClick(R.id.fragment_wall_new_post)
    public void newPostOnClick()
    {
        if (isNewPostVisible)
        {
            isNewPostVisible = false;
            buttonNewPost.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            newPostContainer.setVisibility(View.GONE);
        }
        else {
            selectedButton(1);
        }
    }

    @OnClick(R.id.fragment_wall_filter)
    public void wallFilterOnClick()
    {
        if (isFillterVisible)
        {
            isFillterVisible = false;
            buttonFilter.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            filterContainer.setVisibility(View.GONE);
        }
        else {
            selectedButton(2);

        }
    }

    @OnClick(R.id.fragment_wall_search)
    public void searchOnClick()
    {
        if (isSearchVisible)
        {
            isSearchVisible = false;
            buttonSearch.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
            searchWallContainer.setVisibility(View.GONE);
        }
        else {
            selectedButton(3);
        }
    }

    private void selectedButton (int buttonNumber) //1 = new post, 2 = filter, 3 = serach
    {
        switch (buttonNumber)
        {
            case 1:
                isNewPostVisible = true;
                buttonNewPost.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                newPostContainer.setVisibility(View.VISIBLE);

                isFillterVisible = false;
                buttonFilter.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                filterContainer.setVisibility(View.GONE);

                isSearchVisible = false;
                buttonSearch.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                searchWallContainer.setVisibility(View.GONE);
                break;
            case 2:
                isFillterVisible = true;
                buttonFilter.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                filterContainer.setVisibility(View.VISIBLE);

                isSearchVisible = false;
                buttonSearch.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                searchWallContainer.setVisibility(View.GONE);

                isNewPostVisible = false;
                buttonNewPost.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                newPostContainer.setVisibility(View.GONE);
                break;
            case 3:
                isSearchVisible = true;
                buttonSearch.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                searchWallContainer.setVisibility(View.VISIBLE);

                isNewPostVisible = false;
                buttonNewPost.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                newPostContainer.setVisibility(View.GONE);

                isFillterVisible = false;
                buttonFilter.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                filterContainer.setVisibility(View.GONE);
                break;
            default:
                isNewPostVisible = true;
                buttonNewPost.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                newPostContainer.setVisibility(View.VISIBLE);

                isFillterVisible = false;
                buttonFilter.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                filterContainer.setVisibility(View.GONE);

                isSearchVisible = false;
                buttonSearch.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
                searchWallContainer.setVisibility(View.GONE);
        }
    }
}
