package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FragmentOrganizer extends AbstractFragmentOrganizer {

    HashMap<Integer, List<Class>> containersMap;
    Class baseFragment;

    public FragmentOrganizer(FragmentManager fragmentManager, Class baseFragment) {
        super(fragmentManager);
        containersMap = new HashMap<>();
        this.baseFragment = baseFragment;
    }

    /**
     * Fragment factory method
     */
    @Subscribe
    @Override
    public void onEvent(FragmentEvent event) {
            FragmentEvent fragmentEvent = (FragmentEvent) event;
            Bundle arguments = new Bundle();
            arguments.putString(BaseFragment.PRIMARY_ARG_TAG, fragmentEvent.getId());
            openFragment(createFragment(fragmentEvent.getType()), arguments, getFragmentContainer(fragmentEvent.getType())); // TODO clean up
    }

    @Override
    public boolean handleBackNavigation() {
        Fragment fragment = getOpenFragment();
        if (fragment.getClass().equals(baseFragment)) {
            return false;
        } else {
            fragmentManager.popBackStack();
            return true;
        }
    }


    private int getFragmentContainer(Class fragment) {
        Set<Integer> keys = containersMap.keySet();
        for (Integer key : keys) {
            List<Class> fragments = containersMap.get(key);
            for (Class f : fragments) {
                if (f.equals(fragment)) {
                    return key;
                }
            }
        }
        return -1;
    }

    public void setUpContainer(int tabs_container_1, ArrayList<Class> leftContainerFragments) {
        containersMap.put(tabs_container_1, leftContainerFragments);
    }
}
