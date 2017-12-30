package base.app.ui.fragment.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import base.app.data.wall.WallBase;
import base.app.util.events.BusEvent;
import base.app.util.commons.Utility;


/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * Base fragment that can cary argument with itself
 */

public abstract class BaseFragment extends Fragment {

    public static final String PRIMARY_ARG_TAG = "PRIMARY_ARG_TAG";
    public static final String SECONDARY_ARG_TAG = "SECONDARY_ARG_TAG";
    public static final String ITEM_ARG_TAG = "ITEM_ARG_TAG";
    public static final String STRING_ARRAY_ARG_TAG = "STRING_ARRAY_ARG_TAG";
    public static final String INITIATOR = "INITIATOR_ARG_TAG";
    private static final String TAG = "Base Fragment";

    public BaseFragment() {
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    Object data;

    @Subscribe
    public void onEvent(BusEvent event) {
        // Log.d(TAG, "Base Fragment - onEvent triggered with id : " + event.getId());
    }

    protected boolean hasPrimaryArgument() {
        return null != getArguments().getString(PRIMARY_ARG_TAG);
    }

    protected String getPrimaryArgument() {
        return getArguments().getString(PRIMARY_ARG_TAG);
    }

    protected String getSecondaryArgument() {
        return getArguments().getString(SECONDARY_ARG_TAG);
    }

    @Nullable
    protected WallBase getItemArgument() {
        return (WallBase) getArguments().getSerializable(ITEM_ARG_TAG);
    }

    protected List<String> getStringArrayArguement() {
        if (getArguments().containsKey(STRING_ARRAY_ARG_TAG)) {
            return getArguments().getStringArrayList(STRING_ARRAY_ARG_TAG);
        }
        return null;
    }

    public Class getInitiator() {
        if (getArguments().containsKey(INITIATOR)) {
            String className = getArguments().getString(INITIATOR);
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "Initator class not found: " + className);
            }
        }
        return null;
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
        Utility.hideKeyboard(getActivity());
    }

}
