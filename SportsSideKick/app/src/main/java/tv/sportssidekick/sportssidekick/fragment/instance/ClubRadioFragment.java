package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ClubRadioAdapter;
import tv.sportssidekick.sportssidekick.adapter.ClubTVAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.club.ClubModel;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ClubRadioFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ClubRadioAdapter adapter;
    public ClubRadioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_radio, container, false);

        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ClubRadioAdapter(getContext());
        recyclerView.setAdapter(adapter);

        adapter.getValues().addAll(ClubModel.getInstance().getStations());
        return view;
    }

}
