package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.WallAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.TemporaryWallModel;
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

    public WallFragment() {
        // Required empty public constructor
    }

    List<TemporaryWallModel> fakeModelList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        populateFakeList();
        adapter = new WallAdapter(getActivity(),fakeModelList);
        if(wallRecyclerView!=null){
            wallRecyclerView.setAdapter(adapter);
            wallRecyclerView.addItemDecoration(new StaggeredLayoutManagerItemDecoration(16));
            wallRecyclerView.setLayoutManager(layoutManager);
        }
        WallModel.getInstance();
        return view;
    }

    @Subscribe
    public void onPostUpdate(PostUpdateEvent event){
       Log.d(TAG,"GOT POST!");
    }


    @Subscribe
    public void onPostsLoaded(PostLoadCompleteEvent event){
     Log.d(TAG,"ALL POSTS LOADED!");
    }


    private void populateFakeList(){
        fakeModelList = new ArrayList<>();
        for(int i=0;i<50;i++){
            //add video
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_POST_IMAGE,0));
            //add small cell
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_SMALL_CELL,0));
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_SMALL_CELL,1));
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_SMALL_CELL,3));
            //add small cell with progress bar
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_SMALL_CELL_WITH_CIRCLE_PROGRESS,0));
            //add shop
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_SHOP,0));
            //add comment
            fakeModelList.add(new TemporaryWallModel(WallAdapter.VIEW_TYPE_COMMENT,0));

        }
        Collections.shuffle(fakeModelList);
    }

}
