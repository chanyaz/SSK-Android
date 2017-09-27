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
import base.app.model.wall.WallBase;

/**
 * Created by Filip on 9/27/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TranslationView extends RelativeLayout {

    PopupWindow popup;
    NumberPicker languagePicker;

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
        View popupLayout = inflate(getContext(),R.layout.view_popup_translation, null);
        popupLayout.findViewById(R.id.close).setOnClickListener(onCloseClickListener);

        popupLayout.findViewById(R.id.translate).setOnClickListener(onTranslateClickListener);
        languagesBiMap = HashBiMap.create(TranslateManager.getInstance().getLanguageList());

        Collection<String> languages = languagesBiMap.values();
        languagesList = new ArrayList<>(languages);
        Collections.sort(languagesList);

        languagePicker = popupLayout.findViewById(R.id.language_picker);

        languagePicker.setMinValue(0);
        languagePicker.setMaxValue(languagesList.size()-1);
        languagePicker.setDisplayedValues(languagesList.toArray(new String[languagesList.size()]));

        popup = new PopupWindow(getContext());
        popup.setContentView(popupLayout);
        popup.setBackgroundDrawable(new BitmapDrawable(getContext().getResources()));
    }

    public void showWallTranslationPopup(Point p, String wallItemId, final TaskCompletionSource<WallBase> completion) {
        this.wallItemTranslationCompletion = completion;
        this.wallItemId = wallItemId;
        displayPopup(p);
    }



        // The method that displays the popup.
    public void showMessageTranslationPopup(Point p, String messageId, final TaskCompletionSource<ImsMessage> completion) {
        this.messageTranslationCompletion = completion;
        this.messageId = messageId;
        displayPopup(p);
    }

    private void displayPopup(Point p){
        this.setVisibility(VISIBLE);
        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_Y = - popup.getContentView().getHeight()/2;
        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(this, Gravity.NO_GRAVITY, p.x, p.y + OFFSET_Y);
    }

    ArrayList<String> languagesList;
    BiMap<String, String> languagesBiMap;

    TaskCompletionSource<ImsMessage> messageTranslationCompletion;
    TaskCompletionSource<WallBase> wallItemTranslationCompletion;
    String messageId;
    String wallItemId;

    public void translateMessage(){
        TaskCompletionSource<ImsMessage> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<ImsMessage>() {
            @Override
            public void onComplete(@NonNull Task<ImsMessage> task) {
                if(task.isSuccessful()){
                    ImsMessage translatedMessage = task.getResult();
                    messageTranslationCompletion.setResult(translatedMessage);
                } else {
                    // translation has failed - Display error message?
                }
                TranslationView.this.setVisibility(GONE);
                // TODO - Hide progress dialog

            }
        });
        TranslateManager.getInstance().translateMessage(messageId,getSelectedLanguageCode(),source);
    }


    public void translateWallItem(){
        TaskCompletionSource<WallBase> source = new TaskCompletionSource<>();
        source.getTask().addOnCompleteListener(new OnCompleteListener<WallBase>() {
            @Override
            public void onComplete(@NonNull Task<WallBase> task) {
                if(task.isSuccessful()){
                    WallBase translatedWallBaseItem = task.getResult();
                    wallItemTranslationCompletion.setResult(translatedWallBaseItem);
                } else {
                    // translation has failed - Display error message?
                }
                TranslationView.this.setVisibility(GONE);
                // TODO - Hide progress dialog

            }
        });
        TranslateManager.getInstance().translatePost(messageId,getSelectedLanguageCode(),source);
    }


    OnClickListener onTranslateClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO - Display progress dialog
            if(messageId!=null){
                translateMessage();
            } else if(wallItemId!=null){
                translateWallItem();
            }
            popup.dismiss();
        }
    };

    private String getSelectedLanguageCode(){
        int index = languagePicker.getValue();
        String selectedLanguageName = languagesList.get(index);
        return languagesBiMap.inverse().get(selectedLanguageName);
    }

    private OnClickListener onCloseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TranslationView.this.setVisibility(GONE);
            popup.dismiss();
        }
    };
}
