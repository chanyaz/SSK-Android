package tv.sportssidekick.sportssidekick.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginActivity extends Activity {

    Button logInButton, signUpButton;
    TextView slideTextOne, slideTextTwo;
    Animation rightSwipe, leftSwipe, fadeIn, fadeOut;
    AnimationSet moveRight, moveLeft;
    View circleOne, circleTwo;
    int postion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logInButton = (Button) findViewById(R.id.login_log_in_button);
        signUpButton = (Button) findViewById(R.id.login_sign_up_button);

        slideTextOne = (TextView) findViewById(R.id.login_slide_text_1);
        slideTextTwo = (TextView) findViewById(R.id.login_slide_text_2);

        circleOne = (View) findViewById(R.id.login_circle_one);
        circleTwo = (View) findViewById(R.id.login_circle_two);

        rightSwipe = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        leftSwipe = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        moveRight = new AnimationSet(false);
        moveRight.addAnimation(rightSwipe);
        moveRight.addAnimation(fadeOut);

        moveLeft = new AnimationSet(false);
        moveLeft.addAnimation(leftSwipe);
        moveLeft.addAnimation(fadeIn);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
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
                                circleOne.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_white));
                                circleTwo.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_green));
                                break;
                            case 1:
                                postion = 0;
                                //TODO move to function
                                slideTextTwo.startAnimation(moveLeft);
                                slideTextTwo.setVisibility(View.VISIBLE);
                                slideTextOne.startAnimation(moveRight);
                                slideTextOne.setVisibility(View.GONE);
                                circleOne.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_green));
                                circleTwo.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_white));
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }, 0, 3000);
    }
}
