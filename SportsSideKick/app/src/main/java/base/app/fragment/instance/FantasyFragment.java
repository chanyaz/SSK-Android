package base.app.fragment.instance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import base.app.R;
import base.app.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class FantasyFragment extends BaseFragment {


    @BindView(R.id.image)
    ImageView image;

    public FantasyFragment() {  }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_fantasy, container, false);
        ButterKnife.bind(this,view);
        String url = getResources().getString(R.string.fantasy_url);
        ImageLoader.getInstance().displayImage(url,image);
        return view;
    }

}
