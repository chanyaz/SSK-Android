package base.app.util.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import base.app.R;
import base.app.data.Translator;
import base.app.data.im.ImsMessage;
import base.app.data.wall.PostComment;
import base.app.data.wall.WallBase;
import base.app.data.wall.News;

/**
 * Created by Filip on 9/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class TranslationView extends RelativeLayout {

    private static final String TAG = "Translation View";
    private static final String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";

    View container;
    View progressBar;
    NumberPicker languagePicker;
    RelativeLayout popupLayout;

    ArrayList<String> languagesList;
    BiMap<String, String> languagesBiMap;

    TaskCompletionSource completion;

    String itemId;
    TranslationType type;
    WallBase.PostType postType;

    int[] referenceLocation;
    int referenceHeight, referenceWidth;
    private View parentView;

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public enum TranslationType {
        TRANSLATE_WALL,
        TRANSLATE_NEWS,
        TRANSLATE_IMS,
        TRANSLATE_COMMENT
    }


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
        findViewById(R.id.close).setOnClickListener(onCloseClickListener);
        findViewById(R.id.translate).setOnClickListener(onTranslateClickListener);
        container = findViewById(R.id.root);
        popupLayout = findViewById(R.id.popup);
        languagePicker = findViewById(R.id.language_picker);
        progressBar = findViewById(R.id.progress);
        container.setOnClickListener(onCloseClickListener);

        HashMap<String, String> mapOfLanguages = Translator.getInstance().getLanguageList();
        languagePicker.setMinValue(0);
        if (mapOfLanguages != null && mapOfLanguages.size() > 0) {
            languagesBiMap = HashBiMap.create(mapOfLanguages);
            Collection<String> languages = languagesBiMap.values();
            languagesList = new ArrayList<>(languages);
            Collections.sort(languagesList);
            if (languagesList.size() > 1) {
                languagePicker.setMaxValue(languagesList.size() - 1);
                languagePicker.setDisplayedValues(languagesList.toArray(new String[languagesList.size()]));
            }
        }
        languagePicker.setValue(Prefs.getInt(SELECTED_LANGUAGE, 0));
    }

    public void showTranslationPopup(View clickedView, String id, TaskCompletionSource completion, TranslationType type) {
        showTranslationPopup(clickedView, id, completion, type, null);
    }

    public void showTranslationPopup(View referenceView, String id, TaskCompletionSource completion, TranslationType type, WallBase.PostType postType) {
        this.type = type;
        this.completion = completion;
        itemId = id;
        this.postType = postType;
        popupLayout.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        container.requestLayout();
        popupLayout.requestLayout();

        referenceLocation = new int[2];
        referenceView.getLocationOnScreen(referenceLocation);
        referenceHeight = referenceView.getHeight();
        referenceWidth = referenceView.getWidth();
        this.setVisibility(VISIBLE);
    }

    private void positionPopup(int[] referenceLocation, int referenceHeight, int referenceWidth) {
        popupLayout.measure(
                MeasureSpec.makeMeasureSpec(popupLayout.getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(popupLayout.getHeight(), MeasureSpec.EXACTLY));

        popupLayout.measure(popupLayout.getWidth(), popupLayout.getHeight());

        int parentHeight = parentView.getHeight();
        int parentWidth = parentView.getWidth();
        int popupHeight = popupLayout.getMeasuredHeight();
        int popupWidth = popupLayout.getMeasuredWidth();

        Log.d(TAG, "Parent h:" + parentHeight + " w: " + parentWidth);
        Log.d(TAG, "Popup h:" + popupHeight + " w: " + popupWidth);

        int[] parentLocation = new int[2];
        container.getLocationOnScreen(parentLocation);

        if (referenceLocation != null) {
            int startMargin = referenceLocation[0] + referenceWidth - parentLocation[0];
            int topMargin = referenceLocation[1] + (referenceHeight / 2) - parentLocation[1];
            // offset to align the popup to view
            topMargin = topMargin - (popupHeight / 2);

            int maxStartMargin = parentWidth - popupWidth;
            int maxTopMargin = parentHeight - popupHeight;

            if (parentWidth > 0 && startMargin > maxStartMargin) {
                startMargin = maxStartMargin;
            }
            if (parentHeight > 0 && topMargin > maxTopMargin) {
                topMargin = maxTopMargin;
            }

            LayoutParams params = (RelativeLayout.LayoutParams) popupLayout.getLayoutParams();
            params.setMargins(startMargin, topMargin, 0, 0);
            popupLayout.requestLayout();
        }
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
        Translator.getInstance().translateMessage(itemId, getSelectedLanguageCode(), source);
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
        Translator.getInstance().translatePost(itemId, getSelectedLanguageCode(), source, postType);
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
        Translator.getInstance().translatePostComment(itemId, getSelectedLanguageCode(), source);
    }

    private void translateWallNews() {
        TaskCompletionSource<News> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<News>() {
            @Override
            public void onComplete(@NonNull Task<News> task) {
                if (task.isSuccessful()) {
                    News translatedNews = task.getResult();
                    completion.setResult(translatedNews);
                } else {
                    Toast.makeText(getContext(), "Translation failed.", Toast.LENGTH_SHORT).show();
                }
                TranslationView.this.setVisibility(GONE);
                progressBar.setVisibility(GONE);
            }
        });
        Translator.getInstance().translateNews(itemId, getSelectedLanguageCode(), source);
    }

    OnClickListener onTranslateClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Prefs.putInt(SELECTED_LANGUAGE, languagePicker.getValue());
            popupLayout.setVisibility(GONE);
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
        }
    };

    private String getSelectedLanguageCode() {
        if (languagesBiMap != null && languagesList != null) {
            if (languagesBiMap.size() > 0 && languagesList.size() > 0) {
                int index = languagePicker.getValue();
                String selectedLanguageName = languagesList.get(index);
                return languagesBiMap.inverse().get(selectedLanguageName);
            }
        }
        return "en";
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
        if (popupLayout != null) {
            removeView(popupLayout);
        }
        if (visibility == VISIBLE) {
            positionPopup(referenceLocation, referenceHeight, referenceWidth);
        }
    }
}
