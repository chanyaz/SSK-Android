package base.app.ui.fragment.base;

import android.support.v4.app.Fragment;

import java.util.List;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    Object data;

    protected String getPrimaryArgument() {
        return getArguments().getString(PRIMARY_ARG_TAG);
    }

    protected String getSecondaryArgument() {
        return getArguments().getString(SECONDARY_ARG_TAG);
    }

    protected List<String> getStringArrayArgument() {
        if (getArguments().containsKey(STRING_ARRAY_ARG_TAG)) {
            return getArguments().getStringArrayList(STRING_ARRAY_ARG_TAG);
        }
        return null;
    }
}