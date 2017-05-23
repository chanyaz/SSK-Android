package base.app.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.model.AlertDialogManager;

/**
 * Created by Nemanja Jovanovic on 28/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AlertDialogFragment extends BaseFragment {

    @BindView(R.id.dialog_caption)
    TextView title;

    @BindView(R.id.dialog_content)
    TextView content;

    @BindView(R.id.dialog_ok_button)
    ImageView confirmButton;

    @BindView(R.id.dialog_cancel_button)
    ImageView cancelButton;

    @BindView(R.id.dialog_button_divider)
    View buttonDivider;

    public AlertDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_layout, container, false);
        ButterKnife.bind(this, view);

        String titleText = AlertDialogManager.getInstance().getTitle();
        String contentText = AlertDialogManager.getInstance().getContent();

        title.setText(titleText);
        content.setText(contentText);

        if(AlertDialogManager.getInstance().getCancelListener()!=null){
            cancelButton.setVisibility(View.VISIBLE);
            buttonDivider.setVisibility(View.VISIBLE);
        }

        confirmButton.setOnClickListener(AlertDialogManager.getInstance().getConfirmListener());
        cancelButton.setOnClickListener(AlertDialogManager.getInstance().getCancelListener());

        return view;
    }
}
