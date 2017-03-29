package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.AlertDialogContentManager;

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

        String titleText = AlertDialogContentManager.getInstance().getTitle();
        String contentText = AlertDialogContentManager.getInstance().getContent();

        title.setText(titleText);
        content.setText(contentText);

        if(AlertDialogContentManager.getInstance().getCancelListener()!=null){
            cancelButton.setVisibility(View.VISIBLE);
            buttonDivider.setVisibility(View.VISIBLE);
        }

        confirmButton.setOnClickListener(AlertDialogContentManager.getInstance().getConfirmListener());
        cancelButton.setOnClickListener(AlertDialogContentManager.getInstance().getCancelListener());

        return view;
    }
}
