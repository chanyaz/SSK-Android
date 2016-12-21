package tv.sportssidekick.sportssidekick.fragment.instance;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.service.FirebaseEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

import static tv.sportssidekick.sportssidekick.util.Utility.isEditTextEmpty;
import static tv.sportssidekick.sportssidekick.util.Utility.isValidEmail;
import static tv.sportssidekick.sportssidekick.util.Utility.showAlertDialog;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginFragment extends BaseFragment {

    private static final String TAG = "Login Fragment";
    AlertDialog alertDialog;
    EditText email, password;
    Button loginButton;
    AVLoadingIndicatorView progressBar;
    TextView forgotPassword;
    TextView slideTextOne, slideTextTwo;
    View circleOne, circleTwo;
    int position = 0;
    Context context;
    Timer slideText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.context = getActivity();
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        slideTextOne = (TextView) view.findViewById(R.id.login_slide_text_1);
        slideTextTwo = (TextView) view.findViewById(R.id.login_slide_text_2);
        circleOne = view.findViewById(R.id.login_circle_one);
        circleTwo = view.findViewById(R.id.login_circle_two);

        slideText = new Timer();
        slideText.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (position) {
                                case 0:
                                    position = 1;
                                    Utility.slideText(slideTextOne, slideTextTwo, circleOne, circleTwo,  true, context);
                                    break;
                                case 1:
                                    position = 0;
                                    Utility.slideText(slideTextOne, slideTextTwo, circleOne, circleTwo,  false, context);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        }, 0, Constant.LOGIN_TEXT_TIME);

        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.dialog_warning));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        email = (EditText) view.findViewById(R.id.login_login_email);
        password = (EditText) view.findViewById(R.id.login_login_password);
        progressBar = (AVLoadingIndicatorView) view.findViewById(R.id.login_login_progress_bar);

        loginButton = (Button) view.findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditTextEmpty(email, " email", alertDialog, context))
                    return;
                if (!isValidEmail(email.getText().toString()))
                {
                    showAlertDialog(getString(R.string.dialog_warning), getString(R.string.dialog_message_not_valid_email), context);
                    return;
                }
                if (isEditTextEmpty(password, " password", alertDialog, context))
                    return;
                loginButtonChangeLayout(true);
                signInToFirebase(email.getText().toString(), password.getText().toString());
            }
        });

        forgotPassword = (TextView) view.findViewById(R.id.login_login_forgot_pass);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(ForgotPasswordFramegnt.class);
                EventBus.getDefault().post(fragmentEvent);
            }
        });
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);

    }

    private void loginButtonChangeLayout(boolean isTaskInProgress) {
        if (isTaskInProgress) {
            loginButton.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            loginButton.setText(getString(R.string.login_login_login));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void signInToFirebase(String email, String password) {
        Model.getInstance().signIn(email,password);
    }



    @Override
    public void onPause() {
        super.onPause();
        stopTImerSlideText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTImerSlideText();
    }

    void stopTImerSlideText()
    {
        if (slideText!=null) {
            slideText.cancel();
        }
    }

    @Subscribe
    public void onFirebaseEvent(FirebaseEvent event){
        switch (event.getEventType()){
            case LOGIN_FAILED:
                showAlertDialog(getString(R.string.dialog_warning), getString(R.string.login_login_message_authentication_failed), context);
                loginButtonChangeLayout(false);
                break;
        }
    }
}
