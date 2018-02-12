package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import base.app.R;
import base.app.data.AlertDialogManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlertDialogActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);

        ButterKnife.bind(this);

        String titleText = AlertDialogManager.getInstance().getTitle();
        String contentText = AlertDialogManager.getInstance().getContent();

        title.setText(titleText);
        content.setText(contentText);

        if(AlertDialogManager.getInstance().getCancelListener()!=null){
            cancelButton.setVisibility(View.VISIBLE);
            buttonDivider.setVisibility(View.VISIBLE);
        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogManager.getInstance().getConfirmListener().onClick(v);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
