package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Constructor;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Abstract fragment organizer
 */


abstract class AbstractFragmentOrganizer {

    private static final String TAG = "FRAGMENT ORGANIZER";
    FragmentManager fragmentManager;
    int enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation;

    AbstractFragmentOrganizer(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
        setAnimation(0,0, 0, 0);
        EventBus.getDefault().register(this);
//        openFragment(createFragment(initialFragment), containerId);
    }

    protected Fragment createFragment(Class fragmentClass){
        try {
            Constructor constructor = fragmentClass.getConstructor();
            return (Fragment)constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Subscribe
    protected abstract void onEvent(Object event);

    public abstract boolean handleBackNavigation();

    public void freeUpResources(){
        EventBus.getDefault().unregister(this);
    }

    //TODO change to be private
    public Fragment getOpenFragment(){
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

    String openFragment(Fragment fragment, Bundle arguments , int containerId) {
        if(arguments!=null){
            fragment.setArguments(arguments);
        }
        return openFragment(fragment, containerId);
    }

    public void setAnimation (int enter, int exit, int popEnter, int popExit)
    {
        enterAnimation = enter;
        exitAnimation = exit;
        popEnterAnimation = popEnter;
        popExitAnimation = popExit;
    }

    private String openFragment(Fragment fragment, int containerId) {
        if(isFragmentOpen(fragment)||containerId<=0){
            return "";
        }
        String fragmentTag = createFragmentTag(fragment, true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (enterAnimation !=0 && exitAnimation !=0 && popEnterAnimation !=0 && popExitAnimation !=0){
            transaction.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation,  popExitAnimation);
        }
        Log.d(TAG,"OPEN FRAGMENT: " + fragmentTag);
        Log.d(TAG,"OPEN IN : " + containerId);

        transaction.replace(containerId, fragment, fragmentTag);
        transaction.addToBackStack(fragmentTag);
        transaction.commit();
        return fragmentTag;
    }
}
