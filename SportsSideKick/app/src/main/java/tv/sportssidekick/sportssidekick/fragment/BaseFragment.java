package tv.sportssidekick.sportssidekick.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.sportssidekick.sportssidekick.service.BusEvent;


/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Base fragment that can cary argument with itself
 */

public abstract class BaseFragment extends Fragment {

    public static final String PRIMARY_ARG_TAG = "PRIMARY_ARG_TAG";
    private static final String TAG = "Base Fragment";


    public BaseFragment() { }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    Object data;

    @Subscribe
    public void onEvent(BusEvent event){
        Log.d(TAG, "Base Fragment - onEvent triggered with id : " + event.getId());
    }

    protected boolean hasPrimaryArgument(){
        return null!=getArguments().getString(BaseFragment.PRIMARY_ARG_TAG);
    }

    protected String getPrimaryArgument(){
        return getArguments().getString(BaseFragment.PRIMARY_ARG_TAG);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
