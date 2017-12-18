package base.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import base.app.Connection;
import base.app.R;
import base.app.model.Model;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Djordje Krutil on 5.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.waiting_connection)
    View waitingConnection;
    @BindView(R.id.background)
    ImageView background;
    @BindView(R.id.nextMatchOverlay)
    View nextMatchOverlay;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.putBoolean(Utility.IS_TABLET, Utility.isTablet(this));
        if (Utility.isTablet(this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.next_match_view);
        ButterKnife.bind(this);

        if (Connection.getInstance().reachable()) {
            initialiseModel();
        } else {
            EventBus.getDefault().register(this);
        }
        if (Utility.isPhone(this)) {
            Utility.setSystemBarColor(this);
        }
        Glide.with(this).load(R.drawable.video_chat_background).into(background);
    }

    @Subscribe
    public void onConnectionEvent(Connection.OnChangeEvent event) {
        if (event.getStatus() == Connection.Status.reachable) {
            initialiseModel();
        } else {
            waitingConnection.setVisibility(View.VISIBLE);
        }
    }

    private void initialiseModel() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        Model.getInstance();
        Intent main;

        if (Utility.isTablet(this)) {
            main = new Intent(this, LoungeActivity.class);
        } else {
            main = new Intent(this, PhoneLoungeActivity.class);
        }

        // get data that we received trough intent and forward it to main activity
        Bundle extras = getIntent().getExtras();
        String action = getIntent().getAction();
        Uri data = getIntent().getData();
        if (extras != null) {
            main.putExtras(extras);
        }
        if(action!=null) {
            main.setAction(action);
        }
        if(data!= null){
            main.setData(data);
        }

        // start activity
        startActivity(main);
        finish();

        nextMatchOverlay.setVisibility(View.GONE);
    }
}
