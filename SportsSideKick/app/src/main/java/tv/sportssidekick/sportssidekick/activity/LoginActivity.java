package tv.sportssidekick.sportssidekick.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.ForgotPasswordFramegnt;
import tv.sportssidekick.sportssidekick.fragment.instance.InitialFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.LoginFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.SignUpFragment;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginActivity extends AppCompatActivity {

    FragmentOrganizer loginFragmentOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), InitialFragment.class);
        loginFragmentOrganizer.setAnimation(R.anim.slide_left, R.anim.slide_right, R.anim.left, R.anim.right);
        ArrayList<Class> loginFragments = new ArrayList<>();
        loginFragments.add(LoginFragment.class);
        loginFragments.add(InitialFragment.class);
        loginFragments.add(ForgotPasswordFramegnt.class);
        loginFragments.add(SignUpFragment.class);
        loginFragmentOrganizer.setUpContainer(R.id.login_container,loginFragments);
        EventBus.getDefault().post(new FragmentEvent(InitialFragment.class));
    }

    @Override
    public void onBackPressed() {
        if (!loginFragmentOrganizer.handleBackNavigation()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = loginFragmentOrganizer.getOpenFragment();
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
