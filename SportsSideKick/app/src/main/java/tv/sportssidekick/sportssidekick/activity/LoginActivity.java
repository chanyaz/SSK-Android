package tv.sportssidekick.sportssidekick.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.FragmentOrganizer;
import tv.sportssidekick.sportssidekick.fragment.instance.ForgotPasswordFramegnt;
import tv.sportssidekick.sportssidekick.fragment.instance.InitialFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.LoginFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.SignUpFragment;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.service.GameSparksEvent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginActivity extends AppCompatActivity {

    FragmentOrganizer loginFragmentOrganizer;
    @BindView(R.id.login_container) View fragmentContainer;
    @BindView(R.id.progress_bar) View progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        EventBus.getDefault().register(this);
        Model.getInstance().attachAuthStateListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    @Subscribe
    public void onFirebaseEvent(GameSparksEvent event){
        switch (event.getEventType()){
            case SIGNED_OUT:
                progressBar.setVisibility(View.GONE);
                showLoginForms();
                break;
            case LOGIN_SUCCESSFUL:
                fragmentContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case ALL_DATA_ACQUIRED:
                Intent main = new Intent(this, LoungeActivity.class);
                startActivity(main);
                finish();
                break;
        }
    }

    private void showLoginForms(){
        loginFragmentOrganizer = new FragmentOrganizer(getSupportFragmentManager(), InitialFragment.class);
        loginFragmentOrganizer.setAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.left, R.anim.right);
        ArrayList<Class> loginFragments = new ArrayList<>();
        loginFragments.add(LoginFragment.class);
        loginFragments.add(InitialFragment.class);
        loginFragments.add(ForgotPasswordFramegnt.class);
        loginFragments.add(SignUpFragment.class);
        loginFragmentOrganizer.setUpContainer(R.id.login_container,loginFragments);
        EventBus.getDefault().post(new FragmentEvent(InitialFragment.class));
    }
}
