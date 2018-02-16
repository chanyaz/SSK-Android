package base.app.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import base.app.util.commons.Utility;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * Abstract fragment organizer
 */

abstract class AbstractFragmentOrganizer {

    FragmentManager fragmentManager;
    List<Integer> containersWithoutBackStack;

    AbstractFragmentOrganizer(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        EventBus.getDefault().register(this);
        containersWithoutBackStack = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    Fragment createFragment(Class fragmentClass) {
        try {
            Constructor constructor = fragmentClass.getConstructor();
            return (Fragment) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Subscribe
    protected abstract void onEvent(FragmentEvent event);

    public abstract boolean handleBackNavigation();

    public void freeUpResources() {
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    private String getFragmentTagAt(int position) {
        if (fragmentManager.getBackStackEntryCount() >= position) {
            return fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - position).getName();
        }
        return null;
    }

    @Nullable
    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentByTag(getFragmentTagAt(1));
    }

    /**
     * @return previous fragment
     */
    @Nullable
    public Fragment getPreviousFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag(getFragmentTagAt(2));
        if (fragment == null) {
            fragment = getCurrentFragment();
        }
        return fragment;
    }

    /**
     * @return fragment previous to last fragment
     */
    @Nullable
    public Fragment getPenultimateFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag(getFragmentTagAt(3));
        if (fragment == null) {
            fragment = getPreviousFragment();
        }
        if (fragment == null) {
            fragment = getCurrentFragment();
        }
        return fragment;
    }

    private boolean isFragmentOpen(Fragment fragment) {
        return isFragmentOpen(fragment, true);
    }

    private boolean isFragmentOpen(Fragment fragment, boolean useArgs) {
        String fragmentTag = createFragmentTag(fragment, useArgs);
        if (fragmentManager.getBackStackEntryCount() != 0) {
            String name = getFragmentTagAt(1);
            if (!useArgs) {
                name = name.substring(0, name.indexOf('-'));
            }
            return name.equals(fragmentTag);
        }
        return false;
    }

    private String createFragmentTag(Fragment fragment, boolean addArgs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fragment.getClass().getSimpleName());
        if (addArgs) {
            stringBuilder.append("-");
            if (fragment.getArguments() != null) {
                stringBuilder.append(fragment.getArguments().toString());
            }
        }
        return stringBuilder.toString();
    }

    String openFragment(Fragment fragment, Bundle arguments, int containerId) {
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        return openFragment(fragment, containerId);
    }

    protected abstract int getFragmentContainer(Class fragment);

    private String openFragment(Fragment fragment, int containerId) {
        if (isFragmentOpen(fragment) || containerId <= 0) {
            return "";
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // this is for containers without back stack
        if (containersWithoutBackStack.contains(containerId)) {
            Fragment currentFragment = getCurrentFragment();
            // check if current fragment is in this type of containers
            if (currentFragment != null && containerId == getFragmentContainer(currentFragment.getClass())) {
                // if currentFragment is member of the same container, remove it
                if (Utility.isTablet(fragment.getContext())) {
                    // On tablet, fragment is removed in transaction
                    transaction.remove(currentFragment);
                } else {
                    // On phone, fragment is popped from back stack
                    fragmentManager.popBackStack();
                }
            }
        }

        String fragmentTag = createFragmentTag(fragment, true);
        transaction.addToBackStack(fragmentTag);
        transaction.replace(containerId, fragment, fragmentTag);
        transaction.commitAllowingStateLoss();

        return fragmentTag;
    }
}