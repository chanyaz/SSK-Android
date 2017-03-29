package tv.sportssidekick.sportssidekick.model;

import android.view.View;

import org.greenrobot.eventbus.EventBus;

import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.AlertDialogFragment;

/**
 * Created by Nemanja Jovanovic on 29/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AlertDialogContentManager {

    private static AlertDialogContentManager instance;

    private AlertDialogContentManager() {
    }

    public static AlertDialogContentManager getInstance() {
        if (instance == null) {
            instance = new AlertDialogContentManager();
        }
        return instance;
    }

    private String title;
    private String content;

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

    private View.OnClickListener cancelListener;
    private View.OnClickListener confirmListener;

    public View.OnClickListener getCancelListener() {
        return cancelListener;
    }

    public View.OnClickListener getConfirmListener() {
        return confirmListener;
    }

    public void showDialog(String title, String content, View.OnClickListener cancelListener, View.OnClickListener confirmListener){
        EventBus.getDefault().post(new FragmentEvent(AlertDialogFragment.class));
        this.title = title;
        this.content = content;
        this.cancelListener = cancelListener;
        this.confirmListener = confirmListener;
    }
}