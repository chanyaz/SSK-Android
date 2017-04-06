package tv.sportssidekick.sportssidekick.fragment.instance;


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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.ClubRadioAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.IgnoreBackHandling;
import tv.sportssidekick.sportssidekick.model.club.ClubModel;
import tv.sportssidekick.sportssidekick.model.club.Station;

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
        View view = inflater.inflate(R.layout.fragment_club_radio, container, false);

        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ClubRadioAdapter(getContext());
        recyclerView.setAdapter(adapter);

        Task<List<Station>> getStationsTask = ClubModel.getInstance().getStations();
        getStationsTask.addOnCompleteListener(new OnCompleteListener<List<Station>>() {
            @Override
            public void onComplete(@NonNull Task<List<Station>> task) {
                if(task.isSuccessful()){
                    adapter.getValues().addAll(task.getResult());
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

}
