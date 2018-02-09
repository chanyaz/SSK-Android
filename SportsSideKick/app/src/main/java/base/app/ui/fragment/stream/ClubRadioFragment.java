package base.app.ui.fragment.stream;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.ui.adapter.stream.ClubRadioAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.base.IgnoreBackHandling;
import base.app.data.club.ClubModel;
import base.app.data.club.Station;
import base.app.util.commons.Utility;
import base.app.util.ui.GridItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;

/**

 */
@IgnoreBackHandling
public class ClubRadioFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    View progressBar;

    ClubRadioAdapter adapter;

    public ClubRadioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_radio, container, false);

        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        if (Utility.isPhone(getActivity())) {
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
                    if(Utility.isPhone(getActivity()) && task.getResult().size()>0){
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
