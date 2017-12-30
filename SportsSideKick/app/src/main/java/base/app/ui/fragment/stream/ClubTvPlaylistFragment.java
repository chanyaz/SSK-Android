package base.app.ui.fragment.stream;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import base.app.R;
import base.app.ui.adapter.stream.ClubTVPlaylistAdapter;
import base.app.util.events.stream.ClubTVEvent;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.data.club.ClubModel;
import base.app.util.commons.Utility;
import base.app.util.ui.GridItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**

 */
public class ClubTvPlaylistFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.caption)
    TextView captionTextView;

    ClubTVPlaylistAdapter adapter;
    @Nullable
    Playlist playlist;

    public ClubTvPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_tv, container, false);
        ButterKnife.bind(this, view);
        String playlistId = getPrimaryArgument();
        playlist = ClubModel.getInstance().getPlaylistById(playlistId);
        if (Utility.isTablet(getActivity())) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(layoutManager);
            int space = (int) getResources().getDimension(R.dimen.padding_8);
            recyclerView.addItemDecoration(new GridItemDecoration(space,3));
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new ClubTVPlaylistAdapter(getContext());
        recyclerView.setAdapter(adapter);
        if (captionTextView != null) {
            if (playlist != null) {
                captionTextView.setText(playlist.getSnippet().getTitle());
            }
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (playlist != null) {
            ClubModel.getInstance().requestPlaylist(playlist.getId(), false);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayPlaylist(ClubTVEvent event) {
        if (event.getEventType().equals(ClubTVEvent.Type.PLAYLIST_DOWNLOADED)) {
            adapter.getValues().addAll(ClubModel.getInstance().getPlaylistsVideos(event.getId()));
            adapter.notifyDataSetChanged();
        }
    }

    @Optional
    @OnClick(R.id.back_button)
    public void goBack() {
        EventBus.getDefault().post(new FragmentEvent(ClubTVFragment.class, true));
    }
}
