package base.app.ui.fragment.popup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import base.app.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandar Marinkovic on 30/05/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class SignUpLoginVideoFragment extends SignUpLoginFragment {


    public SignUpLoginVideoFragment() {
        // Required empty public constructor
    }

    @Nullable
    @BindView(R.id.description)
    TextView description;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_video_login_sing_up, container, false);
        ButterKnife.bind(this, view);
        if (text != null) {
            text.setText(Html.fromHtml(getString(R.string.video_chat_promo_1)));
        }
        if (description != null) {
            description.setText(Html.fromHtml(getString(R.string.video_chat_promo_2)));
        }

        return view;

    }



}
