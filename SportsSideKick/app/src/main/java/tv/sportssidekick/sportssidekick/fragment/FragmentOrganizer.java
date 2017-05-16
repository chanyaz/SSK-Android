package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.util.ui.NavigationDrawerItems;

import static tv.sportssidekick.sportssidekick.Constant.CLASS_LIST;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FragmentOrganizer extends AbstractFragmentOrganizer {

    private static final String TAG = "FRAGMENT ORGANIZER";
    private SparseArray<List<Class>> containersMap;

    private Class initialFragment;

    public FragmentOrganizer(FragmentManager fragmentManager, Class fragment) {
        super(fragmentManager);
        containersMap = new SparseArray<>();
        this.initialFragment = fragment;
    }

    /**
     * Fragment factory method
     */
    @Subscribe
    @Override
    public void onEvent(FragmentEvent event) {
        Bundle arguments = new Bundle();
        arguments.putString(BaseFragment.PRIMARY_ARG_TAG, event.getId());

        if(event.getInitiatorFragment()!=null){
            arguments.putString(BaseFragment.INITIATOR,event.getInitiatorFragment().getName());
        }
        if(event.getStringArrayList()!=null){
            arguments.putStringArrayList(BaseFragment.STRING_ARRAY_ARG_TAG, event.getStringArrayList());
        }
        openFragment(createFragment(event.getType()), arguments, getFragmentContainer(event.getType()));
    }

    @Override
    public boolean handleBackNavigation() {
        Fragment fragment = getOpenFragment();
        if(fragment.getClass().isAnnotationPresent(IgnoreBackHandling.class)){
            return true;
        }
        if (fragment.getClass().equals(initialFragment)) {
            return false;
        } else {
            fragmentManager.popBackStack();
            return true;
        }

    }

    public boolean handleNavigationFragment() {
        Fragment fragment = getOpenFragment();
        for(int i=0;i<CLASS_LIST.size();i++)
            if (fragment.getClass().equals(CLASS_LIST.get(i))) {
                NavigationDrawerItems.getInstance().setByPosition(i);
                return true;
            }

        return false;
    }


    protected int getFragmentContainer(Class fragment) {
        for(int i = 0; i < containersMap.size(); i++) {
            int key = containersMap.keyAt(i);
            List<Class> fragments = containersMap.get(key);
            for (Class f : fragments) {
                if (f.equals(fragment)) {
                    return key;
                }
            }
        }
        return -1;
    }

    public void setUpContainer(int containerResourceId, ArrayList<Class> containerFragments){
        setUpContainer(containerResourceId, containerFragments, false);
    }

    public void setUpContainer(int containerResourceId, ArrayList<Class> containerFragments, boolean withoutBackStack) {
        containersMap.put(containerResourceId, containerFragments);
        if(withoutBackStack){
            containersWithoutBackStack.add(containerResourceId);
        }
    }
}
