package base.app.fragment.instance;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import base.app.R;
import base.app.events.GameSparksEvent;
import base.app.fragment.BaseFragment;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallStats;
import base.app.util.SoundEffects;
import base.app.util.Utility;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * A simple {@link BaseFragment} subclass.
 */
@RuntimePermissions
public class StatisticsFragment extends BaseFragment {


    public StatisticsFragment() {
        // Required empty public constructor
    }

    Bitmap bitmap = null;
    WebView webView;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, view);
        webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setVisibility(View.VISIBLE);
        String url = getResources().getString(R.string.stats_url);
        webView.loadUrl(url);
        return view;
    }

    @OnClick(R.id.pin_button)
    public void pinOnClick() {
        if (Model.getInstance().isRealUser()) {
            AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.news_post_to_wall_title), getContext().getResources().getString(R.string.news_post_to_wall_message),
                    new View.OnClickListener() {// Cancel
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    }, new View.OnClickListener() { // Confirm
                        @Override
                        public void onClick(View v) {
                            StatisticsFragmentPermissionsDispatcher.invokeImageSelectionWithCheck(StatisticsFragment.this);
                            getActivity().onBackPressed();
                        }
                    });
        }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);


    }

    @OnClick(R.id.share_button)
    public void shareOnClick() {

    }


    @Subscribe
    @SuppressWarnings("Unchecked cast")
    public void onEventDetected(GameSparksEvent event) {
        switch (event.getEventType()) {
            case STATS_IMAGE_FILE_UPLOADED:
                if (event.getData() != null) {
                    float imageAspectRatio = bitmap.getHeight() / bitmap.getWidth();
                    WallStats wallPost = new WallStats();
                    //TODO Copied from iOS
                    wallPost.setTitle("A Stats post");
                    wallPost.setSubTitle("Some subtitle");
                    wallPost.setTimestamp((double) Utility.getCurrentNTPTime());
                    wallPost.setBodyText("...");
                    wallPost.setCoverAspectRatio(imageAspectRatio);
                    wallPost.setCoverImageUrl((String) event.getData());
                    WallModel.getInstance().mbPost(wallPost);
                }
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        try {
            webView.setDrawingCacheEnabled(true);
            bitmap = webView.getDrawingCache();
            Model.getInstance().uploadImageForStats(saveToInternalStorage(bitmap));
        } catch (Exception e) {

        }
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForStorage(final PermissionRequest request) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setMessage(R.string.permission_storage_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForStorage() {
        Toast.makeText(getContext(), R.string.permission_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskStorage() {
        Toast.makeText(getContext(), R.string.permission_storage_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StatisticsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        File photoFile = null;
        try {
            photoFile = Model.createImageFile(getContext());
        } catch (IOException ex) {
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(photoFile.getAbsolutePath());
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 85, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photoFile.getAbsolutePath();
    }


}
