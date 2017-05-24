package base.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.Connection;
import base.app.Constant;
import base.app.R;
import base.app.model.Model;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Djordje Krutil on 5.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splash_container)
    View fragmentContainer;
    @BindView(R.id.progress_bar)
    View progressBar;
    @BindView(R.id.waiting_connection)
    View waitingConnection;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.is_tablet))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);

        if (Connection.getInstance().reachable()) {
            initialiseModel();
        } else {
            EventBus.getDefault().register(this);
            waitingConnection.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onConnectionEvent(Connection.OnChangeEvent event) {
        if (event.getStatus() == Connection.Status.reachable) {
            initialiseModel();
        }
    }

    private void initialiseModel() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        Model.getInstance();
        final Intent intent = new Intent(this, LoungeActivity.class);
        if (!Prefs.getBoolean(Constant.IS_FIRST_TIME, true)) {
            Prefs.putBoolean(Constant.IS_FIRST_TIME, false);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
        }
        Intent main;
        if (getResources().getBoolean(R.bool.is_tablet))
            main = new Intent(this, LoungeActivity.class);
        else
            main = new Intent(this, PhoneLoungeActivity.class);
        startActivity(main);
        finish();
    }
}