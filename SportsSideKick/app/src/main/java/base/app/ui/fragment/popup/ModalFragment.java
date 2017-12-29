package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import base.app.R;
import base.app.ui.fragment.base.BaseFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Djordje on 22/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class ModalFragment extends BaseFragment {

    public ModalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_modal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.cancel_button)
    public void cancelOnClick() {
        getActivity().onBackPressed();
    }
}
