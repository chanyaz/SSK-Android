package base.app.ui.fragment.other;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import base.app.util.ui.ImageLoader;

import base.app.R;
import base.app.ui.fragment.base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**

 */
public class FantasyFragment extends BaseFragment {


    @BindView(R.id.image)
    ImageView image;

    public FantasyFragment() {  }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fantasy, container, false);
        ButterKnife.bind(this,view);
        String url = getResources().getString(R.string.fantasy_url);
        ImageLoader.displayImage(url,image);
        return view;
    }

}
