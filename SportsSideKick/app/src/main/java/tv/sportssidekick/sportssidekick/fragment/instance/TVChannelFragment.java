package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChannelTVAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.club.ClubTVModel;
import tv.sportssidekick.sportssidekick.model.club.TvCategory;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class TVChannelFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.caption)
    TextView captionTextView;

    public TVChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_tv, container, false);

        ButterKnife.bind(this, view);
        TvCategory category = ClubTVModel.getInstance().getTVCategoryById(getPrimaryArgument());

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        ChannelTVAdapter adapter = new ChannelTVAdapter(getContext());
        adapter.getValues().addAll(category.getTvChannels());
        recyclerView.setAdapter(adapter);

        captionTextView.setText(category.getName());
        return view;
    }

}
