package tv.sportssidekick.sportssidekick.fragment.instance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.activity.LoginActivity;
import tv.sportssidekick.sportssidekick.activity.LoungeActivity;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginMainFragment extends BaseFragment {

    Button logInButton, signUpButton;
    TextView slideTextOne, slideTextTwo;
    Animation rightSwipe, leftSwipe, fadeIn, fadeOut;
    AnimationSet moveRight, moveLeft;
    View circleOne, circleTwo;
    int postion = 0;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        logInButton = (Button) view.findViewById(R.id.login_log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getActivity(), LoungeActivity.class);
                getActivity().startActivity(main);
            }
        });
        signUpButton = (Button) view.findViewById(R.id.login_sign_up_button);

        slideTextOne = (TextView) view.findViewById(R.id.login_slide_text_1);
        slideTextTwo = (TextView) view.findViewById(R.id.login_slide_text_2);

        circleOne = (View) view.findViewById(R.id.login_circle_one);
        circleTwo = (View) view.findViewById(R.id.login_circle_two);

        rightSwipe = AnimationUtils.loadAnimation(context, R.anim.slide_right);
        leftSwipe = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);

        moveRight = new AnimationSet(false);
        moveRight.addAnimation(rightSwipe);
        moveRight.addAnimation(fadeOut);

        moveLeft = new AnimationSet(false);
        moveLeft.addAnimation(leftSwipe);
        moveLeft.addAnimation(fadeIn);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (postion) {
                            case 0:
                                postion = 1;
                                //TODO move to function
                                slideTextOne.startAnimation(moveLeft);
                                slideTextOne.setVisibility(View.VISIBLE);
                                slideTextTwo.startAnimation(moveRight);
                                slideTextTwo.setVisibility(View.GONE);
                                circleOne.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_white));
                                circleTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
                                break;
                            case 1:
                                postion = 0;
                                //TODO move to function
                                slideTextTwo.startAnimation(moveLeft);
                                slideTextTwo.setVisibility(View.VISIBLE);
                                slideTextOne.startAnimation(moveRight);
                                slideTextOne.setVisibility(View.GONE);
                                circleOne.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_green));
                                circleTwo.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_white));
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
            }
        }, 0, 3000);
    }
}
