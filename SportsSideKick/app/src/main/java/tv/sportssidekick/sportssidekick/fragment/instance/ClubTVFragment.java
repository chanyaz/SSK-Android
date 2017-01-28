package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.annotation.MainThread;
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
import tv.sportssidekick.sportssidekick.model.club.ClubTVModel;
import tv.sportssidekick.sportssidekick.service.ClubTVEvent;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ClubTVFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ClubTVAdapter adapter;

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

        ClubTVModel.getInstance(); // This is first time we request club tv instance and playlists too
        return view;
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void displayPlaylists(ClubTVEvent event){
        if(event.getEventType().equals(ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED)){
            adapter.getValues().addAll(ClubTVModel.getInstance().getPlaylists());
            adapter.notifyDataSetChanged();
        }
    }

}
