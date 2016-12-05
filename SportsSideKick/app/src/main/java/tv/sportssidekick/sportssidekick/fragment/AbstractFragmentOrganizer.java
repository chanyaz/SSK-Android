package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Abstract fragment organizer, implement it for each container separately
 */

abstract class AbstractFragmentOrganizer {

    FragmentManager fragmentManager;
    private int containerId;


    AbstractFragmentOrganizer(FragmentManager fragmentManager, int containerId){
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        openFragment(getInitialFragment());
        EventBus.getDefault().register(this);
    }

    protected abstract Fragment getInitialFragment();

    @Subscribe
    protected abstract void onEvent(Object event);

    public abstract boolean handleBackNavigation();

    public void freeUpResources(){
        EventBus.getDefault().unregister(this);
    }

    Fragment getOpenFragment(){
        String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() -1).getName();
        return fragmentManager.findFragmentByTag(tag);
    }

    private boolean isFragmentOpen(Fragment fragment){
        return isFragmentOpen(fragment, true);
    }

    private boolean isFragmentOpen(Fragment fragment, boolean useArgs){
        String fragmentTag = createFragmentTag(fragment, useArgs);
        if (fragmentManager.getBackStackEntryCount() != 0) {
            String name = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            if(!useArgs) {
                name = name.substring(0, name.indexOf("-"));
            }
            return name.equals(fragmentTag);
        }
        return false;
    }

    private String createFragmentTag(Fragment fragment, boolean addArgs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fragment.getClass().getSimpleName());
        if(addArgs) {
            stringBuilder.append("-");
            if (fragment.getArguments() != null)
                stringBuilder.append(fragment.getArguments().toString());
        }
        return stringBuilder.toString();
    }

    String openFragment(Fragment fragment, Bundle arguments) {
        if(arguments!=null){
            fragment.setArguments(arguments);
        }
        return openFragment(fragment);
    }

    private String openFragment(Fragment fragment) {
        if(isFragmentOpen(fragment)&&containerId<0){
            return "";
        }

        String fragmentTag = createFragmentTag(fragment, true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(containerId, fragment, fragmentTag);
        transaction.addToBackStack(fragmentTag);
        transaction.commit();

        return fragmentTag;
    }
}
