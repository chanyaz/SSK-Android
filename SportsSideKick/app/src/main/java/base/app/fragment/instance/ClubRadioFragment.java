package base.app.fragment.instance;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.activity.PhoneLoungeActivity;
import base.app.fragment.FragmentEvent;
import base.app.util.Utility;
import base.app.util.ui.GridItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import base.app.R;
import base.app.adapter.ClubRadioAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.IgnoreBackHandling;
import base.app.model.club.ClubModel;
import base.app.model.club.Station;

/**
 * A simple {@link BaseFragment} subclass.
 */
@IgnoreBackHandling
public class ClubRadioFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;

    ClubRadioAdapter adapter;

    public ClubRadioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() instanceof PhoneLoungeActivity)
            ((PhoneLoungeActivity) getActivity()).setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_club_radio, container, false);

        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        if (!Utility.isTablet(getActivity())) {
            int space = (int) getResources().getDimension(R.dimen.padding_12);
            recyclerView.addItemDecoration(new GridItemDecoration(space, 2));
        }

        adapter = new ClubRadioAdapter(getContext());
        recyclerView.setAdapter(adapter);

        Task<List<Station>> getStationsTask = ClubModel.getInstance().getStations();
        getStationsTask.addOnCompleteListener(new OnCompleteListener<List<Station>>() {
            @Override
            public void onComplete(@NonNull Task<List<Station>> task) {
                if (task.isSuccessful()) {
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    if(!Utility.isTablet(getActivity())){
                        FragmentEvent fragmentEvent = new FragmentEvent(ClubRadioStationFragment.class);
                        fragmentEvent.setId(task.getResult().get(0).getName());
                        EventBus.getDefault().post(fragmentEvent);
                    }
                }
            }
        });
        return view;
    }

}
