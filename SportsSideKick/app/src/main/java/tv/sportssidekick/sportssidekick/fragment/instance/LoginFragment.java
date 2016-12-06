package tv.sportssidekick.sportssidekick.fragment.instance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.activity.LoungeActivity;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginFragment extends BaseFragment {

    private static final String TAG = "Login fragment";
    AlertDialog alertDialog;
    EditText email, password;
    Button loginButton;
    AVLoadingIndicatorView progressBar;
    TextView forgotPassword;
    private FirebaseAuth mAuth;
    TextView slideTextOne, slideTextTwo;
    View circleOne, circleTwo;
    int postion = 0;
    Context context;
    Timer slideText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        this.context = getActivity();
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        slideTextOne = (TextView) view.findViewById(R.id.login_slide_text_1);
        slideTextTwo = (TextView) view.findViewById(R.id.login_slide_text_2);
        circleOne = (View) view.findViewById(R.id.login_circle_one);
        circleTwo = (View) view.findViewById(R.id.login_circle_two);

        slideText = new Timer();
        slideText.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (postion) {
                                case 0:
                                    postion = 1;
                                    Utility.slideText(slideTextOne, slideTextTwo, circleOne, circleTwo,  true, context);
                                    break;
                                case 1:
                                    postion = 0;
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
                if (isEmpty(email, " email"))
                    return;
                if (isEmpty(password, " password"))
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
                if(fragmentEvent!=null){
                    EventBus.getDefault().post(fragmentEvent);
                }
            }
        });
    }

    private void loginButtonChangeLayout(boolean isTaskInProgress) {
        if (isTaskInProgress)
        {
            loginButton.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            loginButton.setText(getString(R.string.login_login_login));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void signInToFirebase(String email, String password) {
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                alertDialog.setMessage(getString(R.string.login_login_message_authentication_failed));
                                alertDialog.show();
                                loginButtonChangeLayout(false);
                            }
                            else
                            {
                                Intent main = new Intent(getActivity(), LoungeActivity.class);
                                getActivity().startActivity(main);
                                loginButtonChangeLayout(false);
                            }
                        }
                    });
        } else {
            loginButtonChangeLayout(false);
            alertDialog.setMessage(getString(R.string.login_login_message_login_failed));
        }
    }

    private boolean isEmpty(EditText text, String filedName) {
        if ("".compareTo(text.getText().toString()) == 0) {
            if (alertDialog != null) {
                alertDialog.setMessage(getString(R.string.dialog_message) + filedName + "!");
                alertDialog.show();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (slideText!=null)
        {
            slideText.cancel();
        }
    }
}
