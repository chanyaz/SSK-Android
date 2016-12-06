package tv.sportssidekick.sportssidekick.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

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

    private FirebaseAuth mAuth;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    Object data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onEvent(BusEvent event){
        Log.d(TAG, "Base Fragment - onEvent triggered with id : " + event.getId());
    }

    protected boolean hasPrimaryArgument(){
        return null!=getArguments().getString(BaseFragment.PRIMARY_ARG_TAG);
    }

    protected String getPrimaryArgument(){
        return getArguments().getString(BaseFragment.PRIMARY_ARG_TAG);
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }
}
