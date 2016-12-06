package tv.sportssidekick.sportssidekick.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.LoungeFragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.InitialFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.LoginFragment;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginActivity extends AppCompatActivity {

    LoungeFragmentOrganizer loginFragmentOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFragmentOrganizer = new LoungeFragmentOrganizer(getSupportFragmentManager());

        ArrayList<Class> loginFragments = new ArrayList<>();
        loginFragments.add(LoginFragment.class);
        loginFragments.add(InitialFragment.class);
        loginFragmentOrganizer.setUpContainer(R.id.login_container,loginFragments);
        EventBus.getDefault().post(new FragmentEvent(InitialFragment.class));
    }

    @Override
    public void onBackPressed() {
        if (!loginFragmentOrganizer.handleBackNavigation()) {
            finish();
        }
    }
}
