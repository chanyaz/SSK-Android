package base.app.ui.fragment.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import base.app.util.events.FragmentEvent;
import base.app.ui.fragment.content.wall.DetailFragment;
import base.app.util.commons.Constant;
import base.app.ui.fragment.content.news.NewsDetailFragment;
import base.app.ui.fragment.popup.EditChatFragment;
import base.app.ui.fragment.popup.JoinChatFragment;
import base.app.util.ui.NavigationDrawerItems;


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
        arguments.putString(BaseFragment.PRIMARY_ARG_TAG, event.getItemId());
        arguments.putString(BaseFragment.SECONDARY_ARG_TAG, event.getSecondaryId());
        arguments.putSerializable(BaseFragment.ITEM_ARG_TAG, event.getItem());

        if (event.getInitiatorFragment() != null) {
            arguments.putString(BaseFragment.INITIATOR, event.getInitiatorFragment().getName());
        }
        if (event.getStringArrayList() != null) {
            arguments.putStringArrayList(BaseFragment.STRING_ARRAY_ARG_TAG, event.getStringArrayList());
        }
        openFragment(createFragment(event.getType()), arguments, getFragmentContainer(event.getType()));
    }

    /**
     * Handles system back button and returns
     * @return true in case this is the last fragment
     */
    @Override
    public boolean handleBackNavigation() {
        Fragment currentFragment = getCurrentFragment();
        // In case this fragment is annotated with IgnoreBackHandling annotation
        if (currentFragment.getClass().isAnnotationPresent(IgnoreBackHandling.class)) {
            return true;
        }
        // If this fragment is initial fragment, return false
        // in order to close the app
        if (currentFragment.getClass().equals(initialFragment)) {
            return false;
        // This is a fragment that should be closed
        } else {
            fragmentManager.popBackStack();
            return true;
        }

    }

    /**
     * Phone only - used to update navigation menu
     */
    public boolean handleNavigationFragment() {
        if (getCurrentFragment().getClass().equals(initialFragment)) {
            return false;
        }
        fragmentManager.popBackStack();
        Fragment fragment = getPreviousFragment();
        for (int i = 0; i < Constant.PHONE_MENU_OPTIONS.size(); i++) {
            if (fragment.getClass().equals(Constant.PHONE_MENU_OPTIONS.get(i))) {
                NavigationDrawerItems.getInstance().setByPosition(i);
                return true;
            }
        }
        if (fragment.getClass().equals(EditChatFragment.class)) {
            NavigationDrawerItems.getInstance().setByPosition(1);
            return true;
        } else if (fragment.getClass().equals(JoinChatFragment.class)) {
            NavigationDrawerItems.getInstance().setByPosition(1);
            return true;
        } else if (fragment.getClass().equals(DetailFragment.class)) {
            NavigationDrawerItems.getInstance().setByPosition(0);
            return true;
        } else if (fragment.getClass().equals(NewsDetailFragment.class)) {
            NavigationDrawerItems.getInstance().setByPosition(2);
            return true;
        }
        return true;
    }

    protected int getFragmentContainer(Class fragment) {
        for (int i = 0; i < containersMap.size(); i++) {
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

    public void setUpContainer(int containerResourceId, ArrayList<Class> containerFragments) {
        setUpContainer(containerResourceId, containerFragments, false);
    }

    public void setUpContainer(int containerResourceId, ArrayList<Class> containerFragments, boolean withoutBackStack) {
        containersMap.put(containerResourceId, containerFragments);
        if (withoutBackStack) {
            containersWithoutBackStack.add(containerResourceId);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
