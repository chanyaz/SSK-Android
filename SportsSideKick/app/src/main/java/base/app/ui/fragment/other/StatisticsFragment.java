package base.app.ui.fragment.other;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import base.app.R;
import base.app.ui.fragment.base.BaseFragment;
import base.app.data.AlertDialogManager;
import base.app.data.Model;
import base.app.data.wall.WallModel;
import base.app.data.wall.WallStats;
import base.app.util.commons.SoundEffects;
import base.app.util.commons.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**

 */
@RuntimePermissions
public class StatisticsFragment extends BaseFragment {


    public StatisticsFragment() {
        // Required empty public constructor
    }

    Bitmap bitmap = null;
    WebView webView;
    String javascriptString = "javascript:( function () { var style = document.createElement('style'); style.innerHTML = '{border-bottom: 0 none;} footer {display: none;}'; document.head.appendChild(style) } ) ()";

    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;


    @BindView(R.id.pin_button)
    ImageView pinButton;

boolean firstLoad=true;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, view);
        webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setVisibility(View.GONE);
        pinButton.setVisibility(View.GONE);
        String url = getResources().getString(R.string.stats_url);
        firstLoad=true;
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url){
                if(firstLoad) {
                    webView.loadUrl(javascriptString);
                    webView.loadUrl("javascript:(function() { document.getElementsByClassName('shsR_grid')[0].remove(); })()");
                    webView.loadUrl("javascript:(function() { document.getElementById('shs_siteNav').remove(); })()");
                    webView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    pinButton.setVisibility(View.VISIBLE);
                    firstLoad=false;
                }
            }
        });
        webView.loadUrl(url);
        return view;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }


    @OnClick(R.id.pin_button)
    public void pinOnClick() {
        if (Model.getInstance().isRealUser()) {
            AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.post_to_wall_title), getContext().getResources().getString(R.string.post_to_wall_message),
                    new View.OnClickListener() {// Cancel
                        @Override
                        public void onClick(View v) {
                            getActivity().onBackPressed();
                        }
                    }, new View.OnClickListener() { // Confirm
                        @Override
                        public void onClick(View v) {
                            StatisticsFragmentPermissionsDispatcher.invokeImageSelectionWithPermissionCheck(StatisticsFragment.this);
                            getActivity().onBackPressed();
                        }
                    });
        } else {
            Toast.makeText(getContext(),"You have to be logged in in order to pin to wall",Toast.LENGTH_SHORT).show();
        }
        SoundEffects.getDefault().playSound(SoundEffects.ROLL_OVER);


    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        try {
            webView.setDrawingCacheEnabled(true);
            bitmap = webView.getDrawingCache();
            final TaskCompletionSource<String> source = new TaskCompletionSource<>();
            Model.getInstance().uploadImageForStats(saveToInternalStorage(bitmap),getActivity().getFilesDir(),source);
            source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        float imageAspectRatio = bitmap.getHeight() / bitmap.getWidth();
                        WallStats wallPost = new WallStats();
                        wallPost.setTitle("A Stats createPost");
                        wallPost.setSubTitle("Some subtitle");
                        wallPost.setTimestamp((double) Utility.getCurrentTime());
                        wallPost.setBodyText("...");
                        wallPost.setCoverAspectRatio(imageAspectRatio);
                        wallPost.setCoverImageUrl(task.getResult());
                        WallModel.getInstance().createPost(wallPost);
                    } else {
                        //TODO @Filip Handle error
                    }
                }
            });

        } catch (Exception e) {

        }
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForStorage(final PermissionRequest request) {
        new AlertDialog.Builder(getContext(), R.style.AlertDialog)
                .setMessage(R.string.permission_storage_rationale)
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

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForStorage() {
        Toast.makeText(getContext(), R.string.permission_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskStorage() {
        Toast.makeText(getContext(), R.string.permission_storage_never_ask, Toast.LENGTH_SHORT).show();
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
