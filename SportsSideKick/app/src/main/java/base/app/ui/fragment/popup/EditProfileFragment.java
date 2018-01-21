package base.app.ui.fragment.popup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import base.app.BuildConfig;
import base.app.R;
import base.app.util.commons.GSConstants;
import base.app.util.commons.UserRepository;
import base.app.data.user.User;
import base.app.data.content.wall.WallModel;
import base.app.util.ui.BaseFragment;
import base.app.util.events.FragmentEvent;
import base.app.util.commons.Connection;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.util.commons.Constants.REQUEST_CODE_EDIT_PROFILE_IMAGE_CAPTURE;
import static base.app.util.commons.Constants.REQUEST_CODE_EDIT_PROFILE_IMAGE_PICK;

/**
 * Created by Filip on 1/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@RuntimePermissions
public class EditProfileFragment extends BaseFragment {

    User user;
    boolean isMuted;

    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.camera_button)
    Button camButton;
    @BindView(R.id.picture_button)
    Button picButton;
    @BindView(R.id.first_name_edit_text)
    EditText firstNameEditText;
    @BindView(R.id.last_name_edit_text)
    EditText lastNameEditText;
    @BindView(R.id.nickname_edit_text)
    EditText nicNameEditText;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.telephone_edit_text)
    EditText phoneEditText;
    @Nullable
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @Nullable
    @BindView(R.id.language_edit_text)
    TextView languageEditText;
    @Nullable
    @BindView(R.id.language_image)
    ImageView languageImage;

    @BindView(R.id.wall_notifications)
    TextView wallNotifications;

    private static final String TAG = "Edit Profile Fragment";
    String currentPath;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_edit_profile, container, false);
        ButterKnife.bind(this, view);

        user = UserRepository.getInstance().getUser();
        if (user != null) {
            ImageLoader.displayImage(user.getAvatar(), profileImage, null);
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            nicNameEditText.setText(user.getNicName());
            emailEditText.setText(user.getEmail());
            phoneEditText.setText(user.getPhone());
            if (languageEditText != null) {
                languageEditText.setText(user.getLanguage());
            }
            if (user.getLanguage() != null) {
                Drawable drawable = getDrawable(user.getLanguage().toLowerCase());
                if (drawable != null && languageImage != null) {
                    languageImage.setImageDrawable(drawable);
                }
            }
            isMuted = user.isWallMute();
            updateMuteUI();
        }
        return view;
    }

    @Optional
    @OnClick(R.id.wall_notifications)
    public void muteWall() {
        isMuted = !isMuted;
        WallModel.getInstance().wallSetMuteValue(user.isWallMute() ? "true" : "false").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.setWallMute(isMuted);
                updateMuteUI();
            }
        });
    }

    private void updateMuteUI() {
        wallNotifications.setText(R.string.notify_wall);
    }

    @Optional
    @OnClick(R.id.language_container)
    public void changeLanguageOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LanguageFragment.class));
    }

    @Optional
    @OnClick(R.id.your_wallet_button)
    public void walletOnClick() {
        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
    }

    @Optional
    @OnClick(R.id.your_stash_button)
    public void stashOnClick() {
        EventBus.getDefault().post(new FragmentEvent(StashFragment.class));
    }

    @OnClick(R.id.camera_button)
    public void cameraButtonOnClick() {
        EditProfileFragmentPermissionsDispatcher.invokeImageCaptureWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageCapture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = UserRepository.createImageFile(getContext());
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
            startActivityForResult(takePictureIntent, REQUEST_CODE_EDIT_PROFILE_IMAGE_CAPTURE);
        }
    }


    @OnClick(R.id.picture_button)
    public void selectImageOnClick() {
        EditProfileFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_EDIT_PROFILE_IMAGE_PICK);
    }

    @OnClick(R.id.close)
    public void closeOnClick() {
        EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_PROFILE_IMAGE_CAPTURE:
                    Log.d(TAG, "CAPTURED IMAGE PATH IS: " + currentPath);
                    uploadImage(currentPath);
                    break;
                case REQUEST_CODE_EDIT_PROFILE_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    Log.d(TAG, "SELECTED IMAGE URI IS: " + selectedImageURI.toString());
                    String realPath = UserRepository.getRealPathFromURI(getContext(), selectedImageURI);
                    Log.d(TAG, "SELECTED IMAGE REAL PATH IS: " + realPath);
                    uploadImage(realPath);
                    break;
            }
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForCameraAndStorage(final PermissionRequest request) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setMessage("Please enable camera permissions in settings.")
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForCameraAndStorage() {
        Toast.makeText(getContext(), "You need to enable camera in settings to take image.", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForCameraAndStorage() {
        Toast.makeText(getContext(), "Never ask for camera permission again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditProfileFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        if (!Connection.getInstance().alertIfNotReachable(getActivity(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isPhone(getContext())) {
                            EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
                        } else {
                            getActivity().onBackPressed();
                        }
                    }
                }
        )) {
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put(GSConstants.FIRST_NAME, firstNameEditText.getText().toString());
        map.put(GSConstants.LAST_NAME, lastNameEditText.getText().toString());
        map.put(GSConstants.NICNAME, nicNameEditText.getText().toString());
        map.put(GSConstants.EMAIL, emailEditText.getText().toString());
        map.put(GSConstants.PHONE, phoneEditText.getText().toString());
        map.put(GSConstants.CLUB_ID_TAG, String.valueOf(CLUB_ID));
        //Todo @refactoring  put password and language
        UserRepository.getInstance().setDetails(map);
        if (Utility.isPhone(getContext())) {
            EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
        } else {
            getActivity().onBackPressed();
        }
    }


    private void uploadImage(String path) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        UserRepository.getInstance().uploadImageForProfile(path, getContext().getFilesDir(), source);
        source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String uploadedImageUrl = task.getResult();
                    UserRepository.getInstance().setProfileImageUrl(uploadedImageUrl, true);
                    ImageLoader.displayImage(uploadedImageUrl, profileImage, null);
                } else {
                    // TODO @Filip Handle error!
                }
            }
        });
    }


    private Drawable getDrawable(String name) {
        try {
            Context context = getActivity();
            int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            return ContextCompat.getDrawable(context, resourceId);
        } catch (Exception e) {
            return null;
        }

    }
}




