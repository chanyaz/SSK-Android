package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.greenrobot.eventbus.Subscribe;

import tv.sportssidekick.sportssidekick.fragment.instance.NewsFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.RumoursFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.StoreFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.VideoChatFragment;
import tv.sportssidekick.sportssidekick.fragment.instance.WallFragment;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FragmentOrganizer extends AbstractFragmentOrganizer {

    public FragmentOrganizer(FragmentManager fragmentManager, int containerId) {
        super(fragmentManager, containerId);
    }

    // Default fragment to be shown
    @Override
    protected Fragment getInitialFragment() {
        return new WallFragment();
    }


    /**
     * Fragment factory method
     *
     */
    @Subscribe
    @Override
    public void onEvent(Object event) {
        if (event instanceof FragmentEvent) {
            FragmentEvent fragmentEvent = (FragmentEvent) event;
            Bundle arguments = new Bundle();
            arguments.putString(BaseFragment.PRIMARY_ARG_TAG, fragmentEvent.getId());
            BaseFragment fragment = null;
            switch (fragmentEvent.getType()) {
                case WALL:
                    fragment = new WallFragment();
                    break;
                case VIDEO_CHAT:
                    fragment = new VideoChatFragment();
                    break;
                case NEWS:
                    fragment = new NewsFragment();
                    break;
                case RUMOURS:
                    fragment = new RumoursFragment();
                    break;
                case STORE:
                    fragment = new StoreFragment();
                    break;
            }
            openFragment(fragment, arguments);
        }
    }

    @Override
    public boolean handleBackNavigation() {
        Fragment fragment = getOpenFragment();
        if (fragment instanceof WallFragment) {
            return false;
        } else {
            fragmentManager.popBackStack();
            return true;
        }
    }
}
