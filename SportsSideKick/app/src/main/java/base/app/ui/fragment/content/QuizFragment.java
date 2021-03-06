package base.app.ui.fragment.content;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import base.app.R;
import base.app.util.ui.BaseFragment;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Filip on 12/6/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *

 */

public class QuizFragment extends BaseFragment {

    @BindView(R.id.image)
    ImageView image;

    public QuizFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fantasy, container, false);
        String url = getResources().getString(R.string.quiz_url);
        ButterKnife.bind(this,view);
        ImageLoader.displayImage(url,image, null);
        return view;
    }
}
