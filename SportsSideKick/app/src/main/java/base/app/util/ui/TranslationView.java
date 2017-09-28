package base.app.util.ui;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import base.app.R;
import base.app.model.TranslateManager;
import base.app.model.im.ImsMessage;
import base.app.model.wall.PostComment;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallNews;

/**
 * Created by Filip on 9/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TranslationView extends RelativeLayout {

    PopupWindow popup;
    NumberPicker languagePicker;
    View progressBar;
    View popupLayout;

    ArrayList<String> languagesList;
    BiMap<String, String> languagesBiMap;

    TaskCompletionSource completion;

    public enum TranslationType {
        TRANSLATE_WALL,
        TRANSLATE_NEWS,
        TRANSLATE_IMS,
        TRANSLATE_COMMENT
    }

    String itemId;
    TranslationType type;
    WallBase.PostType postType;

    public TranslationView(Context context) {
        super(context);
        initView();
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_translation, this);
        popupLayout = inflate(getContext(), R.layout.view_popup_translation, null);

        popupLayout.findViewById(R.id.close).setOnClickListener(onCloseClickListener);
        popupLayout.findViewById(R.id.translate).setOnClickListener(onTranslateClickListener);
        languagePicker = popupLayout.findViewById(R.id.language_picker);
        progressBar = findViewById(R.id.progress);

        languagesBiMap = HashBiMap.create(TranslateManager.getInstance().getLanguageList());
        Collection<String> languages = languagesBiMap.values();
        languagesList = new ArrayList<>(languages);
        Collections.sort(languagesList);
        languagePicker.setMinValue(0);
        languagePicker.setMaxValue(languagesList.size() - 1);
        languagePicker.setDisplayedValues(languagesList.toArray(new String[languagesList.size()]));

        popup = new PopupWindow(getContext());
        popup.setContentView(popupLayout);
        popup.setBackgroundDrawable(new BitmapDrawable(getContext().getResources()));

        popupLayout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    private Point calculateLocationOfPopup(View view) {
        // Get the x, y location and store it in the locationValues[] array
        // locationValues[0] = x, locationValues[1] = y.
        int[] locationValues = new int[2];
        view.getLocationOnScreen(locationValues);

        //Initialize the Point with x, and y positions
        Point location = new Point();
        location.x = locationValues[0] + view.getWidth();
        location.y = locationValues[1] + view.getHeight() / 2;

        return location;
    }
    public void showTranslationPopup(View view, String id, TaskCompletionSource completion, TranslationType type) {
        showTranslationPopup(view,id,completion,type,null);
    }

    public void showTranslationPopup(View view, String id, TaskCompletionSource completion, TranslationType type, WallBase.PostType postType) {
        this.type = type;
        this.completion = completion;
        itemId = id;
        this.postType = postType;
        this.setVisibility(VISIBLE);
        // offset to align the popup to view
        Point p = calculateLocationOfPopup(view);
        int OFFSET_Y = -popupLayout.getMeasuredHeight() / 2;
        // Displaying the popup at the specified location + offsets.
        popup.showAtLocation(this, Gravity.NO_GRAVITY, p.x, p.y + OFFSET_Y);
    }

    private void translateMessage() {
        TaskCompletionSource<ImsMessage> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<ImsMessage>() {
            @Override
            public void onComplete(@NonNull Task<ImsMessage> task) {
                if (task.isSuccessful()) {
                    ImsMessage translatedMessage = task.getResult();
                    completion.setResult(translatedMessage);
                } else {
                    Toast.makeText(getContext(), "Translation failed.", Toast.LENGTH_SHORT).show();
                }
                TranslationView.this.setVisibility(GONE);
                progressBar.setVisibility(GONE);

            }
        });
        TranslateManager.getInstance().translateMessage(itemId, getSelectedLanguageCode(), source);
    }


    private void translateWallItem() {
        TaskCompletionSource<WallBase> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<WallBase>() {
            @Override
            public void onComplete(@NonNull Task<WallBase> task) {
                if (task.isSuccessful()) {
                    WallBase translatedWallBaseItem = task.getResult();
                    completion.setResult(translatedWallBaseItem);
                } else {
                    Toast.makeText(getContext(), "Translation failed.", Toast.LENGTH_SHORT).show();
                }
                TranslationView.this.setVisibility(GONE);
                progressBar.setVisibility(GONE);
            }
        });
        TranslateManager.getInstance().translatePost(itemId, getSelectedLanguageCode(), source, postType);
    }

    private void translatePostComment() {
        TaskCompletionSource<PostComment> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<PostComment>() {
            @Override
            public void onComplete(@NonNull Task<PostComment> task) {
                if (task.isSuccessful()) {
                    PostComment translatedPostCommentItem = task.getResult();
                    completion.setResult(translatedPostCommentItem);
                } else {
                    Toast.makeText(getContext(), "Translation failed.", Toast.LENGTH_SHORT).show();
                }
                TranslationView.this.setVisibility(GONE);
                progressBar.setVisibility(GONE);
            }
        });
        TranslateManager.getInstance().translatePostComment(itemId, getSelectedLanguageCode(), source);
    }

    private void translateWallNews() {
        TaskCompletionSource<WallNews> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<WallNews>() {
            @Override
            public void onComplete(@NonNull Task<WallNews> task) {
                if (task.isSuccessful()) {
                    WallNews translatedWallNews = task.getResult();
                    completion.setResult(translatedWallNews);
                } else {
                    Toast.makeText(getContext(), "Translation failed.", Toast.LENGTH_SHORT).show();
                }
                TranslationView.this.setVisibility(GONE);
                progressBar.setVisibility(GONE);
            }
        });
        TranslateManager.getInstance().translateNews(itemId, getSelectedLanguageCode(), source);
    }

    OnClickListener onTranslateClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(VISIBLE);
            switch (type) {
                case TRANSLATE_WALL:
                    translateWallItem();
                    break;
                case TRANSLATE_IMS:
                    translateMessage();
                    break;
                case TRANSLATE_COMMENT:
                    translatePostComment();
                    break;
                case TRANSLATE_NEWS:
                    translateWallNews();
                    break;
            }
            popup.dismiss();
        }
    };

    private String getSelectedLanguageCode() {
        int index = languagePicker.getValue();
        String selectedLanguageName = languagesList.get(index);
        return languagesBiMap.inverse().get(selectedLanguageName);
    }

    private OnClickListener onCloseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TranslationView.this.setVisibility(GONE);
        }
    };

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        popup.dismiss();
    }
}
