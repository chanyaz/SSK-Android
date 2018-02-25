package base.app.util;

import android.view.View;

import org.greenrobot.eventbus.EventBus;

import base.app.ui.fragment.base.ActivityEvent;
import base.app.ui.fragment.popup.AlertDialogActivity;

/**
 * Created by Nemanja Jovanovic on 29/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AlertDialogManager {

    private static AlertDialogManager instance;
    private String title;
    private String content;
    private View.OnClickListener cancelListener;
    private View.OnClickListener confirmListener;

    private AlertDialogManager() {
    }

    public static AlertDialogManager getInstance() {
        if (instance == null) {
            instance = new AlertDialogManager();
        }
        return instance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public View.OnClickListener getCancelListener() {
        return cancelListener;
    }

    public View.OnClickListener getConfirmListener() {
        return confirmListener;
    }

    public void showAlertDialog(String title, String content, View.OnClickListener cancelListener, View.OnClickListener confirmListener) {
        this.title = title;
        this.content = content;
        this.cancelListener = cancelListener;
        this.confirmListener = confirmListener;
        EventBus.getDefault().post(new ActivityEvent(AlertDialogActivity.class));
    }
}