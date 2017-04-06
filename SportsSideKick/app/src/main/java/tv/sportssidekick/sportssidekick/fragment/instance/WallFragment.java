package tv.sportssidekick.sportssidekick.fragment.instance;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.WallAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.tutorial.TutorialModel;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallModel;
import tv.sportssidekick.sportssidekick.model.wall.WallPost;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;
import tv.sportssidekick.sportssidekick.service.PostCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostLoadCompleteEvent;
import tv.sportssidekick.sportssidekick.service.PostUpdateEvent;
import tv.sportssidekick.sportssidekick.util.StaggeredLayoutManagerItemDecoration;
import tv.sportssidekick.sportssidekick.util.Utility;

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

    @BindView(R.id.post_post_button)
    ImageView postCommentButton;
    @BindView(R.id.post_text)
    EditText commentText;

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

        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = false;

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
        adapter = new WallAdapter(getActivity(), wallItems);
        if(wallRecyclerView!=null){
            wallRecyclerView.setAdapter(adapter);
            wallRecyclerView.addItemDecoration(new StaggeredLayoutManagerItemDecoration(16));
            wallRecyclerView.setLayoutManager(layoutManager);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                WallModel.getInstance().fetchPreviousPageOfPosts(0);
            }
        });

        return view;
    }

    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick(){
       WallFragmentPermissionsDispatcher.invokeImageCaptureWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageCapture(){
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

    @OnClick(R.id.image_button)
    public void selectImageOnClick(){
        WallFragmentPermissionsDispatcher.invokeImageSelectionWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_POST_IMAGE_PICK);
    }

    @OnClick(R.id.video_button)
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeVideoCapture(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_CODE_POST_VIDEO_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_POST_IMAGE_CAPTURE:
                    Model.getInstance().uploadImageForPost(currentPath);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_CODE_POST_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(),selectedImageURI);
                    Model.getInstance().uploadImageForPost(realPath);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_CODE_POST_VIDEO_CAPTURE:
                    Uri videoUri = intent.getData();
                    currentPath = Model.getRealPathFromURI(getContext(),videoUri);
                    Model.getInstance().uploadPostVideoRecording(currentPath);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }



    @Subscribe
    @SuppressWarnings("Unchecked cast")
    public void onEventDetected(GameSparksEvent event){
        switch (event.getEventType()) {
            case LOGGED_OUT:
//                adapter.clear();
//                adapter.notifyDataSetChanged();
                break;
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
       Log.d(TAG,"GOT POST!");
       WallBase post = event.getPost();
        if(post!=null){
            Log.d(TAG,"Post is:" + post.toString());
            wallItems.add(post);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onPostsLoaded(PostLoadCompleteEvent event){
        Log.d(TAG,"ALL POSTS LOADED!");
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onPostUpdated(PostUpdateEvent event){
        adapter.notifyDataSetChanged();
        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    @Subscribe
    public void onPostCompleted(PostCompleteEvent event){
        adapter.notifyDataSetChanged();
        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    private void updateButtons(){
        newPostContainer.setVisibility(isNewPostVisible ? View.VISIBLE : View.GONE);
        buttonNewPost.getBackground().setColorFilter(getResources().getColor(isNewPostVisible ? R.color.colorAccent : R.color.white), PorterDuff.Mode.MULTIPLY);
        filterContainer.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        buttonFilter.getBackground().setColorFilter(getResources().getColor(isFilterVisible ? R.color.colorAccent : R.color.white), PorterDuff.Mode.MULTIPLY);
        searchWallContainer.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        buttonSearch.getBackground().setColorFilter(getResources().getColor(isSearchVisible ? R.color.colorAccent : R.color.white), PorterDuff.Mode.MULTIPLY);
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
        isNewPostVisible=false;
        isFilterVisible = !isFilterVisible;
        isSearchVisible = false;
        updateButtons();
    }

    @OnClick(R.id.fragment_wall_search)
    public void searchOnClick() {
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
            newPost.setTitle("Not sure we need title here");
            newPost.setSubTitle("A subtitle...");
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
            Toast.makeText(getContext(),"Please enter some text for post!", Toast.LENGTH_SHORT).show();
        }
    }
}
