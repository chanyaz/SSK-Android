package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import tv.sportssidekick.sportssidekick.fragment.instance.ForgotPasswordFrament;
import tv.sportssidekick.sportssidekick.fragment.instance.InitialFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.LoginFrament;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class LoginFragmentOrganizer extends FragmentOrganizer {
    public LoginFragmentOrganizer(FragmentManager fragmentManager, int containerId) {
        super(fragmentManager, containerId);
    }

    @Override
    public void onEvent(Object event) {
        if (event instanceof FragmentEvent) {
            FragmentEvent fragmentEvent = (FragmentEvent) event;
            Bundle arguments = new Bundle();
            arguments.putString(BaseFragment.PRIMARY_ARG_TAG, fragmentEvent.getId());
            BaseFragment fragment = null;
            switch (fragmentEvent.getType()) {
                case INITIAL:
                    fragment = new InitialFragment();
                    break;
                case LOGIN:
                    fragment = new LoginFrament();
                    break;
                case FORGOT_PASSWORD:
                    fragment = new ForgotPasswordFrament();
                    break;
            }
            openFragment(fragment, arguments);
        }
    }

    @Override
    protected Fragment getInitialFragment() {
        return  new InitialFragment();
    }

    @Override
    public boolean handleBackNavigation() {
        Fragment fragment = getOpenFragment();
        if (fragment instanceof InitialFragment) {
            return false;
        } else {
            fragmentManager.popBackStack();
            return true;
        }
    }
}
