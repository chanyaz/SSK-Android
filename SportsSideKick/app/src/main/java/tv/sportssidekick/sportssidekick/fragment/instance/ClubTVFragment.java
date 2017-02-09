package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ClubTVAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.club.ClubModel;
import tv.sportssidekick.sportssidekick.service.ClubTVEvent;

/**
 * A simple {@link BaseFragment} subclass.
 */
@IgnoreBackHandling
public class ClubTVFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ClubTVAdapter adapter;
    @BindView(R.id.back_button)
    View backButton;
    public ClubTVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_tv, container, false);

        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ClubTVAdapter(getContext());
        recyclerView.setAdapter(adapter);

        backButton.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ClubModel.getInstance().requestAllPlaylists(); // This is first time we request club tv instance and playlists too
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void displayPlaylists(ClubTVEvent event){
        if(event.getEventType().equals(ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED)){
            adapter.getValues().addAll(ClubModel.getInstance().getPlaylists());
            adapter.notifyDataSetChanged();
        }
    }

}
