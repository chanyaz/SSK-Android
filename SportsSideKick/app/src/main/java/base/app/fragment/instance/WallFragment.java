package base.app.fragment.instance;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.text.WordUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import base.app.BuildConfig;
import base.app.R;
import base.app.adapter.WallAdapter;
import base.app.events.CommentDeleteEvent;
import base.app.events.CommentUpdateEvent;
import base.app.events.PostCompleteEvent;
import base.app.events.PostUpdateEvent;
import base.app.events.WallLikeUpdateEvent;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.IgnoreBackHandling;
import base.app.fragment.popup.LoginFragment;
import base.app.fragment.popup.SignUpFragment;
import base.app.model.Model;
import base.app.model.friendship.FriendsListChangedEvent;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.ticker.NextMatchModel;
import base.app.model.ticker.NextMatchUpdateEvent;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserInfo;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallBetting;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallNewsShare;
import base.app.model.wall.WallPost;
import base.app.model.wall.WallRumor;
import base.app.model.wall.WallStats;
import base.app.model.wall.WallStoreItem;
import base.app.util.NextMatchCountdown;
import base.app.util.Utility;
import base.app.util.ui.GridItemDecoration;
import base.app.util.ui.StaggeredLayoutManagerItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static base.app.Constant.REQUEST_CODE_POST_IMAGE_CAPTURE;
import static base.app.Constant.REQUEST_CODE_POST_IMAGE_PICK;
import static base.app.Constant.REQUEST_CODE_POST_VIDEO_CAPTURE;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
 */

@RuntimePermissions
@IgnoreBackHandling
public class WallFragment extends BaseFragment implements LoginStateReceiver.LoginStateListener {

    private static final String TAG = "WALL FRAGMENT";
    WallAdapter adapter;

    @BindView(R.id.fragment_wall_new_post)
    Button buttonNewPost;
    @Nullable
    @BindView(R.id.scroll)
    NestedScrollView scroll;

    @Nullable
    @BindView(R.id.container)
    RelativeLayout containerRelativeLayout;

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
    @BindView(R.id.fragment_wall_bottom_bar)
    RelativeLayout wallBottomBarContainer;

    @Nullable
    @BindView(R.id.wall_team_left_name)
    TextView wallLeftTeamName;
    @Nullable
    @BindView(R.id.wall_team_left_image)
    ImageView wallLeftTeamImage;
    @Nullable
    @BindView(R.id.wall_team_right_image)
    ImageView wallRightTeamImage;
    @Nullable
    @BindView(R.id.wall_team_right_name)
    TextView wallRightTeamName;
    @Nullable
    @BindView(R.id.wall_team_time)
    TextView wallTeamTime;

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
    String uploadedImageUrl;
    String videoDownloadUrl;
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

    @BindView(R.id.uploaded_image_progress_bar)
    View imageUploadProgressBar;
    @Nullable
    @BindView(R.id.login_holder)
    LinearLayout loginHolder;
    @BindView(R.id.swipe_refresh_layout)
    SwipyRefreshLayout swipeRefreshLayout;

    @Nullable
    @BindView(R.id.wall_top_image_container)
    RelativeLayout nextMatchContainer;

    boolean isNewPostVisible, isFilterVisible, isSearchVisible;
    List<WallBase> wallItems;
    private List<WallBase> filteredWallItems;
    private LoginStateReceiver loginStateReceiver;

    boolean creatingPostInProgress;
    boolean isTablet;

    public WallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Wall lifecycle", "On Create View");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_wall, container, false);
        ButterKnife.bind(this, view);
        updateButtons();
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
                if (s.length() != 0) {
                    postCommentButton.setVisibility(View.VISIBLE);
                } else {
                    postCommentButton.setVisibility(View.GONE);
                }
            }
        });

        isTablet = Utility.isTablet(getActivity());

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new WallAdapter(getActivity());
        boolean includeEdge = isTablet;
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            if (Utility.isTablet(getActivity())) {
                recyclerView.addItemDecoration(new StaggeredLayoutManagerItemDecoration(16, includeEdge, isTablet));
            } else {
                int space = (int) getResources().getDimension(R.dimen.padding_12);
                recyclerView.addItemDecoration(new GridItemDecoration(space, 2));
            }
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            searchText.addTextChangedListener(textWatcher);
            filterPosts();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(!fetchingPageOfPosts) {
                    fetchingPageOfPosts = true;
                    TaskCompletionSource<List<WallBase>> competition = new TaskCompletionSource<>();
                    competition.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<WallBase>> task) {
                            fetchingPageOfPosts = false;
                        }
                    });
                    loadWallItemsPage(false,competition);

                }
            }
        });
        if (wallItems.size() > 0) {
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     *     Update Match info - this method is for Phone only
     */
    @Subscribe
    public void updatePhoneNextMatchDisplay(NextMatchUpdateEvent event){
        if (Utility.isPhone(getActivity())) {
            if(NextMatchModel.getInstance().isNextMatchUpcoming()){
                nextMatchContainer.setVisibility(View.VISIBLE);
                NewsTickerInfo newsTickerInfo = NextMatchModel.getInstance().getTickerInfo();
                if(wallLeftTeamImage.getDrawable()==null){
                    ImageLoader.getInstance().displayImage(newsTickerInfo.getFirstClubUrl(), wallLeftTeamImage, Utility.getImageOptionsForTicker());
                    ImageLoader.getInstance().displayImage(newsTickerInfo.getSecondClubUrl(), wallRightTeamImage, Utility.getImageOptionsForTicker());
                    wallLeftTeamName.setText(newsTickerInfo.getFirstClubName());
                    wallRightTeamName.setText(newsTickerInfo.getSecondClubName());
                }
                long timestamp = Long.parseLong(newsTickerInfo.getMatchDate());
                if(wallTeamTime!=null){
                    wallTeamTime.setText(NextMatchCountdown.getTextValue(getContext(),timestamp,false));
                }
            } else {
                nextMatchContainer.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick() {
        WallFragmentPermissionsDispatcher.invokeCameraCaptureWithPermissionCheck(this);
    }

    @OnClick(R.id.image_button)
    public void selectImageOnClick() {
        WallFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(this);
    }

    @Optional
    @OnClick(R.id.join_now_button)
    public void joinOnClick() {
        EventBus.getDefault().post(new FragmentEvent(SignUpFragment.class));
    }

    @Optional
    @OnClick(R.id.login_button)
    public void loginOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LoginFragment.class));
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_POST_IMAGE_PICK);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeCameraCapture() {
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
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
                        if (Utility.isKitKat()) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        if (Utility.isLollipopAndUp()) {
                            Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        }
                    }
                    startActivityForResult(takePictureIntent, REQUEST_CODE_POST_IMAGE_CAPTURE);
                }
            }
        });
        chooseDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WallFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_POST_IMAGE_CAPTURE:
                    imageUploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    uploadImagePost(currentPath);
                    makePostContainerVisible();
                    break;
                case REQUEST_CODE_POST_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(), selectedImageURI);
                    uploadImagePost(realPath);
                    imageUploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    makePostContainerVisible();
                    break;
                case REQUEST_CODE_POST_VIDEO_CAPTURE:
                    Uri videoUri = intent.getData();
                    currentPath = Model.getRealPathFromURI(getContext(), videoUri);
                    uploadVideoPost(currentPath);
                    imageUploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedImage.setVisibility(View.VISIBLE);
                    makePostContainerVisible();
                    break;
            }
        }
    }

    private void uploadImagePost(String path){
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    uploadedImageUrl = task.getResult();
                    ImageLoader.getInstance().displayImage(uploadedImageUrl, uploadedImage,
                            Utility.getDefaultImageOptions(),
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    imageUploadProgressBar.setVisibility(View.GONE);
                                }
                            });
                } else {
                    // TODO @Filip Handle error!
                }
            }
        });
        Model.getInstance().uploadImageForWallPost(path,getActivity().getFilesDir(),source);

    }
    private void uploadVideoPost(final String path) {
        final TaskCompletionSource<String> thumbnailUploadSource = new TaskCompletionSource<>();
        // Firstly, upload thumbnail of the video
        thumbnailUploadSource.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // store url of it, and then display it
                    videoThumbnailDownloadUrl = task.getResult();
                    ImageLoader.getInstance().displayImage(
                            videoThumbnailDownloadUrl,
                            uploadedImage,
                            Utility.getDefaultImageOptions());
                    // then, upload video itself, and hide progressbar when its done
                    final TaskCompletionSource<String> videoUploadSource = new TaskCompletionSource<>();
                    videoUploadSource.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            videoDownloadUrl = task.getResult();
                            if (task.isSuccessful()) {
                                imageUploadProgressBar.setVisibility(View.GONE);
                            } else {
                                Log.e(TAG, "Video can't be uploaded.");
                            }
                        }
                    });
                    Model.getInstance().uploadWallPostVideoRecording(path,getActivity().getFilesDir(),videoUploadSource);
                } else {
                    Log.e(TAG, "Video thumbnail can't be uploaded.");
                }
            }
        });
        // invoke video thumbnail uploading
        Model.getInstance().uploadWallPostVideoRecordingThumbnail(
                path,
                getActivity().getFilesDir(),
                thumbnailUploadSource
        );

    }


    @OnClick({R.id.uploaded_image, R.id.remove_uploaded_photo_button})
    public void removeUploadedContent() {
        uploadedImageUrl = null;
        videoDownloadUrl = null;
        videoThumbnailDownloadUrl = null;
        uploadedImage.setVisibility(View.GONE);
        removeUploadedImage.setVisibility(View.GONE);
        imageUploadProgressBar.setVisibility(View.GONE);
        postCommentButton.setVisibility(View.GONE);
    }

    @Subscribe
    public void onPostUpdate(PostUpdateEvent event) {
        final WallBase post = event.getPost();
        if (post != null) {
            Log.d(TAG, "GOT POST with id: " + post.getPostId());
            for (WallBase item : wallItems) {
                if (item.getWallId() == post.getWallId() && item.getPostId() == post.getPostId()) {
                    item.setEqualTo(post);
                    filterPosts();
                    return;
                }
            }
            if (post.getPoster() == null && post instanceof WallPost) {
                Model.getInstance().getUserInfoById(post.getWallId()).addOnCompleteListener(new OnCompleteListener<UserInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<UserInfo> task) {
                        if (task.isSuccessful()) {
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

        isNewPostVisible = creatingPostInProgress;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    @Subscribe
    public void onUpdateLikeCount(WallLikeUpdateEvent event) {
        if (event.getWallId() != null) {
            for (WallBase item : wallItems) {
                if (event.getWallId().equals(item.getWallId())
                        && event.getPostId().equals(item.getPostId())) {
                    item.setLikeCount(event.getCount());
                    filterPosts();
                    return;
                }
            }
        }
    }

    @Subscribe
    public void onUpdateComment(CommentUpdateEvent event) {
        if (event.getWallItem() != null) {
            for (WallBase item : wallItems) {
                if (event.getWallItem().getWallId().equals(item.getWallId())
                        && event.getWallItem().getPostId().equals(item.getPostId())) {
                    item.setCommentsCount(event.getWallItem().getCommentsCount());
                    filterPosts();
                    return;
                }
            }
        }
    }

    private List<WallBase> filterList(final Class<?> cls){
         return Lists.newArrayList(Iterables.filter(filteredWallItems, new Predicate<WallBase>() {
            @Override
            public boolean apply(@Nullable WallBase input) {
                return !(cls.isInstance(input));
            }
        }));
    }

    @OnClick({R.id.news_filter_toggle, R.id.user_filter_toggle, R.id.stats_filter_toggle, R.id.rumours_filter_toggle, R.id.store_filter_toggle})
    public void filterPosts() {
        filteredWallItems = wallItems;
        if (newsToggleButton.isChecked()) {
            filteredWallItems = filterList(WallNewsShare.class);
        }
        if (userToggleButton.isChecked()) {
            filteredWallItems = filterList(WallPost.class);
        }
        if (statsToggleButton.isChecked()) {
            filteredWallItems = filterList(WallStats.class);
        }
        if (rumoursToggleButton.isChecked()) {
            filteredWallItems = filterList(WallRumor.class);
        }
        if (storeToggleButton.isChecked()) {
            filteredWallItems = filterList(WallStoreItem.class);

        }
        if (!TextUtils.isEmpty(searchText.getText())) {
            final String searchTerm = searchText.getText().toString();
            filteredWallItems = Lists.newArrayList(Iterables.filter(wallItems, new Predicate<WallBase>() {
                @Override
                public boolean apply(@Nullable WallBase input) {
                    return searchWallItem(searchTerm, input);
                }
            }));
        }
        adapter.replaceAll(filteredWallItems);
        adapter.notifyDataSetChanged();
    }

    private boolean searchWallItem(String searchTerm, WallBase item) {
        searchTerm = searchTerm.toLowerCase();
        UserInfo poster = item.getPoster();
        if (poster == null) {
            poster = Model.getInstance().getCachedUserInfoById(item.getWallId());
            if (poster != null) {
                if (poster.getNicName() != null && poster.getFirstName() != null && poster.getLastName() != null) {
                    String allNames = poster.getNicName() + poster.getFirstName() + poster.getLastName();
                    if (allNames.toLowerCase().contains(searchTerm)) {
                        return true;
                    }
                }
            } else {
                Log.e(TAG, "There is no User info in cache for this wall item - check out whats going on?");
            }
        }
        if (item instanceof WallNewsShare || item instanceof WallPost) {
            if (item.getTitle().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (item.getSubTitle().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (item.getBodyText().toLowerCase().contains(searchTerm)) {
                return true;
            }
        }
        if (item instanceof WallRumor || item instanceof WallStoreItem) {
            if (item.getTitle().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (item.getSubTitle().toLowerCase().contains(searchTerm)) {
                return true;
            }
        }
        if (item instanceof WallStats) {
            WallStats statsItem = (WallStats) item;
            if (statsItem.getStatName().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (statsItem.getSubText().toLowerCase().contains(searchTerm)) {
                return true;
            }
        }
        if (item instanceof WallBetting) {
            WallBetting betItem = (WallBetting) item;
            if (betItem.getBetName().toLowerCase().contains(searchTerm)) {
                return true;
            }
            if (betItem.getOutcome().toLowerCase().contains(searchTerm)) {
                return true;
            }
        }
        return false;
    }

    TextWatcher textWatcher = new TextWatcher() {
        private final long DELAY = 500; // milliseconds
        private Timer timer = new Timer();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

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

                if (!TextUtils.isEmpty(searchText.getText())) {
                    final String searchTerm = searchText.getText().toString();
                    filteredWallItems = Lists.newArrayList(Iterables.filter(wallItems, new Predicate<WallBase>() {
                        @Override
                        public boolean apply(@Nullable WallBase input) {
                            return searchWallItem(searchTerm, input);
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
    public void onPostCompleted(PostCompleteEvent event) {
        creatingPostInProgress = false;
        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = false;
        postCommentButton.setVisibility(View.GONE);
        updateButtons();
        recyclerView.smoothScrollToPosition(0);
    }

    private void makePostContainerVisible() {
        creatingPostInProgress = true;
        isNewPostVisible = true;
        isFilterVisible = false;
        isSearchVisible = false;
        postCommentButton.setVisibility(View.VISIBLE);
        updateButtons();
    }

    private void updateButtons() {
        int defaultColor;
        int selectedColor;
        int containerColor;
        if (Utility.isTablet(getActivity())) { //Tablet
            defaultColor = R.color.white;
            selectedColor = R.color.colorAccent;
            //TODO Change this (drawable)
            if(BuildConfig.FLAVOR.equals("brugge")){
                selectedColor = R.color.colorGrayDark;
            }
        } else { //Phone
            defaultColor = R.color.colorPrimary;
            selectedColor = R.color.colorPrimaryVeryLight;
            containerColor = R.color.colorGrayLight;
            int wallBottomBarColor;

            if (isNewPostVisible || isFilterVisible || isSearchVisible) {
                wallBottomBarColor = R.color.white;
            } else {
                wallBottomBarColor = R.color.colorGrayLight;
            }
            wallBottomBarContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), wallBottomBarColor));
            newPostContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), containerColor));
            filterContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), containerColor));
            searchWallContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), containerColor));
        }
        newPostContainer.setVisibility(isNewPostVisible ? View.VISIBLE : View.GONE);
        //TODO @Filip deprecation
        buttonNewPost.getBackground().setColorFilter(getResources().getColor(isNewPostVisible ? selectedColor : defaultColor), PorterDuff.Mode.MULTIPLY);
        filterContainer.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        buttonFilter.getBackground().setColorFilter(getResources().getColor(isFilterVisible ? selectedColor : defaultColor), PorterDuff.Mode.MULTIPLY);
        searchWallContainer.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        buttonSearch.getBackground().setColorFilter(getResources().getColor(isSearchVisible ? selectedColor : defaultColor), PorterDuff.Mode.MULTIPLY);
    }

    @OnClick(R.id.fragment_wall_new_post)
    public void newPostOnClick() {
        isNewPostVisible = !isNewPostVisible;
        isFilterVisible = false;
        isSearchVisible = false;
        updateButtons();
    }

    @OnClick(R.id.fragment_wall_filter)
    public void wallFilterOnClick() {
        creatingPostInProgress = false;
        isNewPostVisible = false;
        isFilterVisible = !isFilterVisible;
        isSearchVisible = false;
        updateButtons();
    }

    @OnClick(R.id.fragment_wall_search)
    public void searchOnClick() {
        creatingPostInProgress = false;
        isNewPostVisible = false;
        isFilterVisible = false;
        isSearchVisible = !isSearchVisible;
        updateButtons();
    }

    @OnClick(R.id.post_post_button)
    public void postPost() {
        if (imageUploadProgressBar.getVisibility() == View.VISIBLE) {
            Utility.toast(getActivity(), getString(R.string.uploading));
        } else {
            String postContent = commentText.getText().toString();
            commentText.setText("");
            WallPost newPost = new WallPost();
            newPost.setType(WallBase.PostType.post);
            UserInfo user = Model.getInstance().getUserInfo();
            newPost.setTitle(WordUtils.capitalize(user.getFirstName()) + "," + WordUtils.capitalize(user.getLastName()));
            newPost.setSubTitle(getContext().getResources().getString(R.string.wall_new_post_subtitle));
            newPost.setTimestamp((double) Utility.getCurrentTime());

            if (!TextUtils.isEmpty(postContent)) {
                newPost.setBodyText(postContent);
            }

            if (uploadedImageUrl != null) {
                newPost.setCoverImageUrl(uploadedImageUrl);
            } else if (videoDownloadUrl != null && videoThumbnailDownloadUrl != null) {
                newPost.setCoverImageUrl(videoThumbnailDownloadUrl);
                newPost.setVidUrl(videoDownloadUrl);
            }
            uploadedImageUrl = null;
            videoDownloadUrl = null;
            videoThumbnailDownloadUrl = null;
            uploadedImage.setVisibility(View.GONE);
            removeUploadedImage.setVisibility(View.GONE);
            imageUploadProgressBar.setVisibility(View.GONE);

            WallModel.getInstance().mbPost(newPost);
            Utility.hideKeyboard(getActivity());
        }

    }

    int offset = 0;
    int pageSize = 20;
    boolean fetchingPageOfPosts = false;

    private void reloadWallFromModel() {
        Log.d("Wall lifecycle", "Reloading wall from model.");
        wallItems.clear();
        filteredWallItems.clear();
        adapter.notifyDataSetChanged();

        offset = 0;
        fetchingPageOfPosts = true;
        final TaskCompletionSource<List<WallBase>> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallBase>> task) {
                fetchingPageOfPosts = false;
            }
        });
        loadWallItemsPage(true,source);
    }

    private void loadWallItemsPage(boolean withSpinner,  final TaskCompletionSource<List<WallBase>> completion){
        if(withSpinner){
            progressBar.setVisibility(View.VISIBLE);
        }
        TaskCompletionSource<List<WallBase>> getWallPostCompletion = new TaskCompletionSource<>();
        getWallPostCompletion.getTask().addOnCompleteListener(new OnCompleteListener<List<WallBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<WallBase>> task) {
                if(task.isSuccessful()){
                    List<WallBase> items = task.getResult();
                    Log.d("Wall lifecycle", "Loaded Wall Items -  number of items: " + items.size());
                    wallItems.addAll(items);
                    completion.setResult(items);
                    filterPosts();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    offset +=pageSize;
                } else {
                    Log.d("Wall lifecycle", "Loaded Wall Items -  Failed");
                    Log.d(TAG,"Failed to get posts!");
                }
            }
        });
        WallModel.getInstance().loadWallPosts(offset,pageSize,null,getWallPostCompletion);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Wall lifecycle", "On Destroy");
        EventBus.getDefault().unregister(loginStateReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("Wall lifecycle", "On Attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Wall lifecycle", "On Detach");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomBar();
        if (loginHolder!=null) {
            if(Model.getInstance().isRealUser()) {
                Log.d("Wall lifecycle", "On Resume - Real user");
                loginHolder.setVisibility(View.GONE);
            } else {
                Log.d("Wall lifecycle", "On Resume - Anonymous user");
                loginHolder.setVisibility(View.VISIBLE);
            }
        }
    }

    private void reset() {
        Log.d("Wall lifecycle", "Reset - wall is cleared");
        WallModel.getInstance().clear();
        updateBottomBar();
    }

    @Override
    public void onLogout() {
        Log.d("Wall lifecycle", "Logout");
        reset();
        updateBottomBar();
    }

    @Override
    public void onLoginAnonymously() {
        if (!Model.getInstance().isRealUser() && loginHolder!=null) {
            Log.d("Wall lifecycle", "Login Anonymously - Anonymous user");
            loginHolder.setVisibility(View.VISIBLE);
        } else {
            Log.d("Wall lifecycle", "Login Anonymously - Real user -- (THIS SHOULD NOT HAPPEN!");
        }
        reset();
        reloadWallFromModel();
        updateBottomBar();
    }

    @Override
    public void onLogin(UserInfo user) {
        if (Model.getInstance().isRealUser() && loginHolder!=null) {
            Log.d("Wall lifecycle", "Login - Real user");
            loginHolder.setVisibility(View.GONE);
        } else {
            Log.d("Wall lifecycle", "Login - Anonymous user");
        }
        reset();
        reloadWallFromModel();
        updateBottomBar();

    }

    @Subscribe
    public void handleFriendListChanged(FriendsListChangedEvent event){
        Log.d("Wall lifecycle", "Friend List Changed");
        reset();
        reloadWallFromModel();
        updateBottomBar();
    }

    @Override
    public void onLoginError(Error error) {
        Log.d("Wall lifecycle", "Login Error");
        reset();
    }

    public void updateBottomBar() {
        buttonNewPost.setEnabled(Model.getInstance().isRealUser());
    }
}
