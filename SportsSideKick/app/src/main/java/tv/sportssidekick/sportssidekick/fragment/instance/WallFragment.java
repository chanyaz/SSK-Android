package tv.sportssidekick.sportssidekick.fragment.instance;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pixplicity.easyprefs.library.Prefs;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.WallAdapter;
import tv.sportssidekick.sportssidekick.events.GameSparksEvent;
import tv.sportssidekick.sportssidekick.events.PostCompleteEvent;
import tv.sportssidekick.sportssidekick.events.PostLoadCompleteEvent;
import tv.sportssidekick.sportssidekick.events.PostUpdateEvent;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.tutorial.TutorialModel;
import tv.sportssidekick.sportssidekick.model.tutorial.WallTip;
import tv.sportssidekick.sportssidekick.model.user.LoginStateReceiver;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallBetting;
import tv.sportssidekick.sportssidekick.model.wall.WallModel;
import tv.sportssidekick.sportssidekick.model.wall.WallNewsShare;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;
import tv.sportssidekick.sportssidekick.model.wall.WallRumor;
import tv.sportssidekick.sportssidekick.model.wall.WallStats;
import tv.sportssidekick.sportssidekick.model.wall.WallStoreItem;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.StaggeredLayoutManagerItemDecoration;
import tv.sportssidekick.sportssidekick.util.ui.ThemeManager;

import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_POST_IMAGE_CAPTURE;
import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_POST_IMAGE_PICK;
import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_POST_VIDEO_CAPTURE;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

@RuntimePermissions
@IgnoreBackHandling
public class WallFragment extends BaseFragment implements LoginStateReceiver.LoginStateListener {

    private static final String TAG = "WALL FRAGMENT";
    WallAdapter adapter;

    @BindView(R.id.fragment_wall_new_post)
    Button buttonNewPost;
    @BindView(R.id.fragment_wall_filter)
    Button buttonFilter;
    @BindView(R.id.fragment_wall_search)
    Button buttonSearch;

    @BindView(R.id.fragment_wall_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.your_post_container)
    RelativeLayout newPostContainer;
    @BindView(R.id.wall_filter_container)
    RelativeLayout filterContainer;
    @BindView(R.id.search_wall_post)
    RelativeLayout searchWallContainer;

    @BindView(R.id.news_filter_toggle)
    ToggleButton newsToggleButton;
    @BindView(R.id.user_filter_toggle)
    ToggleButton userToggleButton;
    @BindView(R.id.stats_filter_toggle)
    ToggleButton statsToggleButton;
    @BindView(R.id.rumours_filter_toggle)
    ToggleButton rumoursToggleButton;
    @BindView(R.id.store_filter_toggle)
    ToggleButton storeToggleButton;


    @BindView(R.id.post_post_button)
    ImageView postCommentButton;
    @BindView(R.id.post_text)
    EditText commentText;
    @BindView(R.id.search_text)
    EditText searchText;

    String currentPath;
    String videoDownloadUrl;
    String uploadedImageUrl;
    String videoThumbnailDownloadUrl;

    @BindView(R.id.camera_button)
    ImageView cameraButton;
    @BindView(R.id.image_button)
    ImageView imageButton;
    @BindView(R.id.uploaded_image)
    ImageView uploadedImage;
    @BindView(R.id.remove_uploaded_photo_button)
    ImageView removeUploadedImage;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;

    boolean isNewPostVisible, isFilterVisible, isSearchVisible;
    List<WallBase> wallItems;
    private List<WallBase> filteredWallItems;
    private LoginStateReceiver loginStateReceiver;

    boolean creatingPostInProgress;

    public WallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);

        TutorialModel.getInstance().initialize(getActivity());
        this.loginStateReceiver = new LoginStateReceiver(this);

        wallItems = new ArrayList<>();
        wallItems.addAll(WallBase.getCache().values());
        filteredWallItems = new ArrayList<>();
        WallModel.getInstance();
        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = false;

        creatingPostInProgress = false;

        commentText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    postCommentButton.setVisibility(View.VISIBLE);
                }
                else {
                    postCommentButton.setVisibility(View.GONE);
                }
            }
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new WallAdapter(getActivity());
        if(recyclerView !=null){
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new StaggeredLayoutManagerItemDecoration(16));
            recyclerView.setLayoutManager(layoutManager);
            searchText.addTextChangedListener(textWatcher);
            filterPosts();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                WallModel.getInstance().fetchPreviousPageOfPosts(0);
            }
        });

        if(wallItems.size()>0){
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }



    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick(){
       WallFragmentPermissionsDispatcher.invokeCameraCaptureWithCheck(this);
    }

    @OnClick(R.id.image_button)
    public void selectImageOnClick(){
        WallFragmentPermissionsDispatcher.invokeImageSelectionWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_POST_IMAGE_PICK);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeCameraCapture() {
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity());
        chooseDialog.setTitle(getContext().getResources().getString(R.string.choose));
        chooseDialog.setMessage(getContext().getResources().getString(R.string.chat_image_or_video));
        chooseDialog.setNegativeButton(getContext().getResources().getString(R.string.chat_video), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_CODE_POST_VIDEO_CAPTURE);
                }
            }
        });
        chooseDialog.setPositiveButton("Image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = Model.createImageFile(getContext());
                        currentPath = photoFile.getAbsolutePath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), "tv.sportssidekick.sportssidekick.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }
                    startActivityForResult(takePictureIntent, REQUEST_CODE_POST_IMAGE_CAPTURE);
                }
            }
        });
        chooseDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_POST_IMAGE_CAPTURE:
                    Model.getInstance().uploadImageForPost(currentPath);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    makePostContainerVisible();
                    break;
                case REQUEST_CODE_POST_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(),selectedImageURI);
                    Model.getInstance().uploadImageForPost(realPath);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    makePostContainerVisible();
                    break;
                case REQUEST_CODE_POST_VIDEO_CAPTURE:
                    Uri videoUri = intent.getData();
                    currentPath = Model.getRealPathFromURI(getContext(),videoUri);
                    Model.getInstance().uploadPostVideoRecording(currentPath);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    makePostContainerVisible();
                    break;
            }
        }
    }

    @Subscribe
    @SuppressWarnings("Unchecked cast")
    public void onEventDetected(GameSparksEvent event){
        switch (event.getEventType()) {
            case POST_IMAGE_FILE_UPLOADED:
                if(event.getData()!=null){
                    uploadedImageUrl = (String)event.getData();
                    ImageLoader.getInstance().displayImage(uploadedImageUrl, uploadedImage, Utility.imageOptionsImageLoader());
                }
            case VIDEO_FILE_UPLOADED:
                videoDownloadUrl = (String) event.getData();
                Model.getInstance().uploadPostVideoRecordingThumbnail(currentPath,getActivity().getFilesDir());
                break;
            case VIDEO_IMAGE_FILE_UPLOADED:
                videoThumbnailDownloadUrl = (String) event.getData();
                ImageLoader.getInstance().displayImage(videoThumbnailDownloadUrl, uploadedImage, Utility.imageOptionsImageLoader());
                break;
        }
    }

    @OnClick({R.id.uploaded_image,R.id.remove_uploaded_photo_button})
    public void removeUploadedContent(){
        uploadedImageUrl = null;
        videoDownloadUrl = null;
        videoThumbnailDownloadUrl = null;
        uploadedImage.setVisibility(View.GONE);
        removeUploadedImage.setVisibility(View.GONE);
    }


    @Subscribe
    public void onPostUpdate(PostUpdateEvent event){
       Log.d(TAG,"GOT POST with id: " + event.getId());
       final WallBase post = event.getPost();
        if(post!=null){
            for(WallBase item : wallItems){
                if(item.getWallId()==post.getWallId() && item.getPostId()==post.getPostId()){
                    item.setEqualTo(post);
                    filterPosts();
                    return;
                }
            }
            if(post.getPoster() == null && post instanceof WallPost){
                Model.getInstance().getUserInfoById(post.getWallId()).addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if(task.isSuccessful()){
                            post.setPoster(task.getResult());
                            wallItems.add(post);
                        }
                        filterPosts();
                    }
                });
            } else {
                wallItems.add(post);
                filterPosts();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.news_filter_toggle,R.id.user_filter_toggle,R.id.stats_filter_toggle,R.id.rumours_filter_toggle,R.id.store_filter_toggle})
    public void filterPosts() {
        filteredWallItems = wallItems;
        if(newsToggleButton.isChecked()){
            filteredWallItems = Lists.newArrayList(Iterables.filter(filteredWallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return !(input instanceof WallNewsShare);
                }
            }));
        }
        if(userToggleButton.isChecked()){
            filteredWallItems = Lists.newArrayList(Iterables.filter(filteredWallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return !(input instanceof WallPost);
                }
            }));
        }
        if(statsToggleButton.isChecked()) {
            filteredWallItems = Lists.newArrayList(Iterables.filter(filteredWallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return !(input instanceof WallStats);
                }
            }));
        }
        if(rumoursToggleButton.isChecked()){
            filteredWallItems = Lists.newArrayList(Iterables.filter(filteredWallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return !(input instanceof WallRumor);
                }
            }));
        }
        if(storeToggleButton.isChecked()){
            filteredWallItems = Lists.newArrayList(Iterables.filter(filteredWallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return !(input instanceof WallStoreItem);
                }
            }));
        }
        if(!TextUtils.isEmpty(searchText.getText())){
            final String searchTerm = searchText.getText().toString();
            filteredWallItems = Lists.newArrayList(Iterables.filter(wallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return searchWallItem(searchTerm,input);
                }
            }));
        }
        //sortByTimestamp();
        adapter.replaceAll(filteredWallItems);
        adapter.notifyDataSetChanged();
    }

    private void sortByTimestamp() {
        Collections.sort(wallItems,new Comparator<WallBase>() {
            @Override
            public int compare(WallBase o1, WallBase o2) {
                return (int) (o2.getTimestamp()-o1.getTimestamp());
            }
        });
        Collections.sort(Lists.newArrayList(filteredWallItems),new Comparator<WallBase>() {
            @Override
            public int compare(WallBase o1, WallBase o2) {
                return (int) (o2.getTimestamp()-o1.getTimestamp());
            }
        });
    }



    private boolean searchWallItem(String searchTerm, WallBase item){
        UserInfo poster = item.getPoster();
        if(poster==null) {
            poster = Model.getInstance().getCachedUserInfoById(item.getWallId());
            if (poster != null) {
                if (poster.getNicName() != null && poster.getFirstName() != null && poster.getLastName() != null) {
                    String allNames = poster.getNicName() + poster.getFirstName() + poster.getLastName();
                    if (allNames.toLowerCase().contains(searchTerm.toLowerCase())) {
                        return true;
                    }
                }
            } else {
                Log.e(TAG, "There is no User info in cache for this wall item - check out whats going on?");
            }
        }

        if(item instanceof WallNewsShare || item instanceof WallPost){
            if(item.getTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
            if(item.getSubTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
            if(item.getBodyText().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
        }
        if(item instanceof WallRumor || item instanceof WallStoreItem){
            if(item.getTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
            if(item.getSubTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
        }
        if(item instanceof WallStats){
            WallStats statsItem = (WallStats) item;
            if(statsItem.getStatName().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
            if(statsItem.getSubText().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
        }
        if(item instanceof WallBetting){
            WallBetting betItem = (WallBetting) item;
            if(betItem.getBetName().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
            if(betItem.getOutcome().toLowerCase().contains(searchTerm.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    TextWatcher textWatcher = new TextWatcher() {
        private final long DELAY = 500; // milliseconds
        private Timer timer = new Timer();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            performSearch();
                        }
                    },
                    DELAY
            );
        }
    };

    public void performSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(!TextUtils.isEmpty(searchText.getText())){
                    final String searchTerm = searchText.getText().toString();
                    filteredWallItems = Lists.newArrayList(Iterables.filter(wallItems, new Predicate<WallBase>() {
                        @Override
                        public boolean apply(@Nullable WallBase input) {
                            return searchWallItem(searchTerm,input);
                        }
                    }));
                    adapter.replaceAll(filteredWallItems);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                }
            }
        });
    }

    @Subscribe
    public void onPostsLoaded(PostLoadCompleteEvent event){
        Log.d(TAG,"ALL POSTS LOADED!");
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        getNextTip();
    }

    @Subscribe
    public void onPostUpdated(PostUpdateEvent event){
        isNewPostVisible = false || creatingPostInProgress;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    @Subscribe
    public void onPostCompleted(PostCompleteEvent event){
        creatingPostInProgress = false;
        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    private void makePostContainerVisible(){
        creatingPostInProgress = true;
        isNewPostVisible= true;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    private void updateButtons(){
        int defaultColor ;
        int selectedColor;
        if (!ThemeManager.getInstance().isLightTheme()) {
            defaultColor = R.color.white;
            selectedColor = R.color.colorAccent;
        }else {
            defaultColor = R.color.colorPrimary;
            selectedColor = R.color.light_radio_button_background;
        }
        newPostContainer.setVisibility(isNewPostVisible ? View.VISIBLE : View.GONE);
        buttonNewPost.getBackground().setColorFilter(getResources().getColor(isNewPostVisible ? selectedColor : defaultColor), PorterDuff.Mode.MULTIPLY);
        filterContainer.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        buttonFilter.getBackground().setColorFilter(getResources().getColor(isFilterVisible ? selectedColor : defaultColor), PorterDuff.Mode.MULTIPLY);
        searchWallContainer.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        buttonSearch.getBackground().setColorFilter(getResources().getColor(isSearchVisible ? selectedColor : defaultColor), PorterDuff.Mode.MULTIPLY);
    }
    @OnClick(R.id.fragment_wall_new_post)
    public void newPostOnClick() {
        isNewPostVisible=!isNewPostVisible;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    @OnClick(R.id.fragment_wall_filter)
    public void wallFilterOnClick() {
        creatingPostInProgress = false;
        isNewPostVisible=false;
        isFilterVisible = !isFilterVisible;
        isSearchVisible = false;
        updateButtons();
    }

    @OnClick(R.id.fragment_wall_search)
    public void searchOnClick() {
        creatingPostInProgress = false;
        isNewPostVisible=false;
        isFilterVisible = false;
        isSearchVisible = !isSearchVisible;
        updateButtons();
    }

    @OnClick(R.id.post_post_button)
    public void postPost() {
        String postContent = commentText.getText().toString();
        if(!TextUtils.isEmpty(postContent)){
            commentText.setText("");
            WallPost newPost = new WallPost();
            newPost.setType(WallBase.PostType.post);
            newPost.setTitle(getContext().getResources().getString(R.string.wall_new_post_title));
            newPost.setSubTitle(getContext().getResources().getString(R.string.wall_new_post_subtitle));
            newPost.setTimestamp((double) System.currentTimeMillis());
            newPost.setBodyText(postContent);

            if(uploadedImageUrl!=null){
                newPost.setCoverImageUrl(uploadedImageUrl);
            } else if(videoDownloadUrl!=null && videoThumbnailDownloadUrl!=null){
                newPost.setCoverImageUrl(uploadedImageUrl);
                newPost.setVidUrl(videoDownloadUrl);
            }
            uploadedImageUrl = null;
            videoDownloadUrl = null;
            videoThumbnailDownloadUrl = null;
            uploadedImage.setVisibility(View.GONE);
            removeUploadedImage.setVisibility(View.GONE);
            WallModel.getInstance().mbPost(newPost);
        } else {
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.wall_text_for_post), Toast.LENGTH_SHORT).show();
        }
    }

    private void getNextTip()
    {
        WallTip tip = null;

        if (TutorialModel.getInstance().getTutorialItems() != null)
        {
            for (int i = 0; i < TutorialModel.getInstance().getTutorialItems().size(); i++)
            {
                if (!TutorialModel.getInstance().getTutorialItems().get(i).hasBeenSeen())
                {
                    tip = TutorialModel.getInstance().getTutorialItems().get(i);
                    break;
                }
            }
        }

        if (Model.getInstance().getUserInfo() != null && Model.getInstance().getUserInfo().getUserType() == UserInfo.UserType.fan)
        {
            if (tip!=null)
            {
                tip.setType(WallBase.PostType.tip);
                tip.setTimestamp((double)System.currentTimeMillis()/1000);
                tip.setPostId(Model.getInstance().getUserInfo().getUserId());
                WallBase.getCache().put(tip.getPostId(), tip);
                wallItems.add(tip);
                EventBus.getDefault().post(new PostUpdateEvent(tip));
            }
        }
    }

    private void reloadWallFromModel(){
        wallItems.clear();
        filteredWallItems.clear();
        adapter.notifyDataSetChanged();
        WallModel.getInstance().fetchPosts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(loginStateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNextTip();
    }

    private void reset(){
        WallModel.getInstance().clear();
    }

    @Override
    public void onLogout() {
        reset();
    }

    @Override
    public void onLoginAnonymously() {
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLogin(UserInfo user) {
        reset();
        reloadWallFromModel();
    }

    @Override
    public void onLoginError(Error error) {
        reset();
    }
}
