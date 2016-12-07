package tv.sportssidekick.sportssidekick.fragment.instance;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wang.avi.AVLoadingIndicatorView;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.util.Utility;

import static tv.sportssidekick.sportssidekick.util.Utility.isValidEmail;
import static tv.sportssidekick.sportssidekick.util.Utility.showAlertDialog;


/**
 * Created by Djordje Krutil on 6.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class ForgotPasswordFramegnt extends BaseFragment {

    private static final String TAG = "Forgot password fragment";
    AlertDialog alertDialog;
    EditText email;
    Button resetEmail;
    AVLoadingIndicatorView progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (AVLoadingIndicatorView) view.findViewById(R.id.forgot_password_progress_bar);

        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.dialog_warning));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        email =(EditText)view.findViewById(R.id.forgot_password_email);
        resetEmail = (Button)view.findViewById(R.id.forgot_password_resert_button);
        resetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetEmail.setText("");
                progressBar.setVisibility(View.VISIBLE);

                if (!isValidEmail(email.getText().toString()))
                {
                    showAlertDialog(getString(R.string.dialog_warning), getString(R.string.dialog_message_not_valid_email), getContext());

                    resetEmail.setText(getString(R.string.forgot_password_button_text));
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else
                {
                    //TODO reset password Firebase
//                    resetEmail.setText(getString(R.string.forgot_password_button_text));
//                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
