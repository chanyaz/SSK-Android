package base.app.fragment.instance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import base.app.activity.PhoneLoungeActivity;
import base.app.fragment.FragmentEvent;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.R;
import base.app.adapter.ClubTVAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.IgnoreBackHandling;
import base.app.model.club.ClubModel;
import base.app.events.ClubTVEvent;

/**
 * A simple {@link BaseFragment} subclass.
 */
@IgnoreBackHandling
public class ClubTVFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ClubTVAdapter adapter;
    @Nullable
    @BindView(R.id.back_button)
    View backButton;

    public ClubTVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_club_tv, container, false);

        ButterKnife.bind(this, view);
        if (Utility.isTablet(getActivity())) {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(layoutManager);
        } else {

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        }


        adapter = new ClubTVAdapter(getContext());
        recyclerView.setAdapter(adapter);

        if (backButton != null) {
            backButton.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String channelId = getResources().getString(R.string.clubtv_channelid);
        if (Utility.isPhone(getActivity())) {
            FragmentEvent fragmentEvent = new FragmentEvent(YoutubePlayerFragment.class);
            EventBus.getDefault().post(fragmentEvent);
        }
        ClubModel.getInstance().requestAllPlaylists(channelId); // This is first time we request club tv instance and playlists too
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayPlaylists(ClubTVEvent event) {
        if (event.getEventType().equals(ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED)) {
            adapter.getValues().addAll(ClubModel.getInstance().getPlaylists());
            adapter.notifyDataSetChanged();

        }
    }

}
