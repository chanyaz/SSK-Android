package tv.sportssidekick.sportssidekick.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Djordje Krutil on 5.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splash_container) View fragmentContainer;
    @BindView(R.id.progress_bar) View progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);

        final Intent intent = new Intent(this, LoungeActivity.class);
        if(!Prefs.getBoolean(Constant.IS_FIRST_TIME,true)){
            Prefs.putBoolean(Constant.IS_FIRST_TIME, false);
            Bundle extras = getIntent().getExtras();
            if(extras!=null){
                intent.putExtras(extras);
            }
        }
        Intent main = new Intent(this, LoungeActivity.class);
        startActivity(main);
        finish();
    }
}
