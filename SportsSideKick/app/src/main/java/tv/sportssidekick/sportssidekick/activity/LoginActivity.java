package tv.sportssidekick.sportssidekick.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.LoginFragmentOrganizer;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginActivity extends AppCompatActivity {

    LoginFragmentOrganizer loginFragmentOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFragmentOrganizer = new LoginFragmentOrganizer(getSupportFragmentManager(), R.id.login_container);
    }

    @Override
    public void onBackPressed() {
        if (!loginFragmentOrganizer.handleBackNavigation())
        {
            finish();
        }
    }
}
