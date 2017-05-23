package base.app.fragment.instance;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import base.app.R;
import base.app.adapter.ClubTVPlaylistAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.club.ClubModel;
import base.app.events.ClubTVEvent;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ClubTvPlaylistFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.caption)
    TextView captionTextView;

    ClubTVPlaylistAdapter adapter;
    Playlist playlist;
    public ClubTvPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_tv, container, false);
        ButterKnife.bind(this, view);
        playlist = ClubModel.getInstance().getPlaylistById(getPrimaryArgument());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ClubTVPlaylistAdapter(getContext());
        recyclerView.setAdapter(adapter);
        captionTextView.setText(playlist.getSnippet().getTitle());


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ClubModel.getInstance().requestPlaylist(playlist.getId());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayPlaylist(ClubTVEvent event){
        if(event.getEventType().equals(ClubTVEvent.Type.PLAYLIST_DOWNLOADED)){
            adapter.getValues().addAll(ClubModel.getInstance().getPlaylistsVideos(event.getId()));
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.back_button)
    public void goBack(){
        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class, true));
    }
}
