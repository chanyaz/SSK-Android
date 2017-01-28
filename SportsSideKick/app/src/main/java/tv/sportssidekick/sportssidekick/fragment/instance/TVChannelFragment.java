package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ChannelTVAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.club.ClubTVModel;
import tv.sportssidekick.sportssidekick.service.ClubTVEvent;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class TVChannelFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.caption)
    TextView captionTextView;

    ChannelTVAdapter adapter;
    public TVChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_tv, container, false);
        ButterKnife.bind(this, view);
        Playlist playlist = ClubTVModel.getInstance().getPlaylistById(getPrimaryArgument());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChannelTVAdapter(getContext());
        recyclerView.setAdapter(adapter);
        captionTextView.setText(playlist.getSnippet().getTitle());

        ClubTVModel.getInstance().requestPlaylist(playlist.getId());

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayPlaylist(ClubTVEvent event){
        if(event.getEventType().equals(ClubTVEvent.Type.PLAYLIST_DOWNLOADED)){
            adapter.getValues().addAll(ClubTVModel.getInstance().getPlaylistsVideos(event.getId()));
            adapter.notifyDataSetChanged();
        }
    }

}
