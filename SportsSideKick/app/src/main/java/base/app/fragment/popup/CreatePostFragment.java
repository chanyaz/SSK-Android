package base.app.fragment.popup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import base.app.BuildConfig;
import base.app.R;
import base.app.events.PostCompleteEvent;
import base.app.fragment.BaseFragment;
import base.app.model.Model;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallPost;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static base.app.Constant.REQUEST_CODE_POST_IMAGE_CAPTURE;
import static base.app.Constant.REQUEST_CODE_POST_IMAGE_PICK;
import static base.app.Constant.REQUEST_CODE_POST_VIDEO_CAPTURE;

/**
 * Created by Filip on 11/21/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@RuntimePermissions
public class CreatePostFragment extends BaseFragment {

    private static final String TAG = "CREATE POST FRAGMENT";
    String currentPath;
    String uploadedImageUrl;
    String videoDownloadUrl;
    String videoThumbnailDownloadUrl;
    boolean creatingPostInProgress;
    
    @BindView(R.id.camera_button)
    ImageView cameraButton;
    @BindView(R.id.image_button)
    ImageView imageButton;
    @BindView(R.id.uploaded_image)
    ImageView uploadedImage;
    @BindView(R.id.remove_uploaded_photo_button)
    ImageView removeUploadedContent;
    @BindView(R.id.uploaded_image_progress_bar)
    View uploadProgressBar;
    
    @BindView(R.id.caption)
    EditText captionText;
    @BindView(R.id.post_text)
    EditText contentText;

    @BindView(R.id.post_post_button)
    ImageView postButton;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        ButterKnife.bind(this, view);
        creatingPostInProgress = false;
        return view;
    }

    private void makePostContainerVisible() {
        creatingPostInProgress = true;
        postButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.post_post_button)
    public void postPost() {
        if (uploadProgressBar.getVisibility() == View.VISIBLE) {
            Utility.toast(getActivity(), getString(R.string.uploading));
        } else {
            WallPost newPost = new WallPost();
            String captionContent = captionText.getText().toString();
            String postContent = contentText.getText().toString();
            if (!TextUtils.isEmpty(captionContent)) {
                newPost.setTitle(captionContent);
            } else {
                // TODO - Display warning that caption is missing
            }

            if (!TextUtils.isEmpty(postContent)) {
                newPost.setBodyText(postContent);
            } else {
               // TODO - Display warning that body text is missing
            }

            newPost.setType(WallBase.PostType.post);
            newPost.setSubTitle(getContext().getResources().getString(R.string.wall_new_post_subtitle));
            newPost.setTimestamp((double) Utility.getCurrentTime());

            if (uploadedImageUrl != null) {
                newPost.setCoverImageUrl(uploadedImageUrl);
            } else if (videoDownloadUrl != null && videoThumbnailDownloadUrl != null) {
                newPost.setCoverImageUrl(videoThumbnailDownloadUrl);
                newPost.setVidUrl(videoDownloadUrl);
            }
            WallModel.getInstance().mbPost(newPost);
            Utility.hideKeyboard(getActivity());

            // TODO - Wait or close fragment?
        }
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

    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick() {
        CreatePostFragmentPermissionsDispatcher.invokeCameraCaptureWithPermissionCheck(this);
    }

    @OnClick(R.id.image_button)
    public void selectImageOnClick() {
        CreatePostFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(this);
    }

    @OnClick({R.id.uploaded_image, R.id.remove_uploaded_photo_button})
    public void removeUploadedContent() {
        uploadedImageUrl = null;
        videoDownloadUrl = null;
        videoThumbnailDownloadUrl = null;
        uploadedImage.setVisibility(View.GONE);
        removeUploadedContent.setVisibility(View.GONE);
        uploadProgressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onPostCompleted(PostCompleteEvent event) {
        creatingPostInProgress = false;
        postButton.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // TODO Uncomment on build
        CreatePostFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_POST_IMAGE_CAPTURE:
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedContent.setVisibility(View.VISIBLE);
                    uploadImagePost(currentPath);
                    makePostContainerVisible();
                    break;
                case REQUEST_CODE_POST_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(), selectedImageURI);
                    uploadImagePost(realPath);
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedContent.setVisibility(View.VISIBLE);
                    makePostContainerVisible();
                    break;
                case REQUEST_CODE_POST_VIDEO_CAPTURE:
                    Uri videoUri = intent.getData();
                    currentPath = Model.getRealPathFromURI(getContext(), videoUri);
                    uploadVideoPost(currentPath);
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    uploadedImage.setVisibility(View.VISIBLE);
                    removeUploadedContent.setVisibility(View.VISIBLE);
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
                                    uploadProgressBar.setVisibility(View.GONE);
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
                                uploadProgressBar.setVisibility(View.GONE);
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
}
