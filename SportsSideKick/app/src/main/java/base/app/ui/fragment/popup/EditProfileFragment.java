package base.app.ui.fragment.popup;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import base.app.R;
import base.app.data.GSConstants;
import base.app.data.Model;
import base.app.data.user.UserInfo;
import base.app.data.wall.WallModel;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.util.commons.Connection;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static base.app.ClubConfig.CLUB_ID;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * Created by Filip on 1/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@RuntimePermissions
public class EditProfileFragment extends BaseFragment {

    UserInfo user;
    boolean isMuted;

    @BindView(R.id.profileImage)
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

        user = Model.getInstance().getUserInfo();
        if (user != null) {
            ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), profileImage);
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
    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void cameraButtonOnClick() {
        RxPaparazzo.single(EditProfileFragment.this)
                .usingCamera()
                .map(new Function<Response<EditProfileFragment,FileData>, Object>() {
                    @Override
                    public Object apply(Response<EditProfileFragment, FileData> fileData) throws Exception {
                        return fileData.data().getFile();
                    }
                })
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Object o) {
                        File imageFile = (File) o;
                        currentPath = imageFile.getAbsolutePath();
                        uploadImage(currentPath);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @OnClick(R.id.picture_button)
    public void selectImageOnClick() {
        RxPaparazzo.single(EditProfileFragment.this)
                .usingGallery()
                .map(new Function<Response<EditProfileFragment,FileData>, Object>() {
                    @Override
                    public Object apply(Response<EditProfileFragment, FileData> fileData) throws Exception {
                        return fileData.data().getFile();
                    }
                })
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Object o) {
                        File imageFile = (File) o;
                        currentPath = imageFile.getAbsolutePath();
                        uploadImage(currentPath);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @OnClick(R.id.close)
    public void closeOnClick() {
        EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class, true));
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        if (!Connection.alertIfNotReachable(getActivity(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isPhone(getContext())) {
                            EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class, true));
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
        Model.getInstance().setDetails(map);
        if (Utility.isPhone(getContext())) {
            EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class, true));
        } else {
            getActivity().onBackPressed();
        }
    }


    private void uploadImage(String path) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        Model.getInstance().uploadImage(path, getContext().getFilesDir(), source);
        source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String uploadedImageUrl = task.getResult();
                    Model.getInstance().setProfileImageUrl(uploadedImageUrl, true);
                    ImageLoader.displayRoundImage(uploadedImageUrl, profileImage);
                } else {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
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




