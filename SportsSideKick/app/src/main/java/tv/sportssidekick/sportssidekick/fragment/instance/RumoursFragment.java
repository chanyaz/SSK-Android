package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.RumoursBigAdapter;
import tv.sportssidekick.sportssidekick.adapter.RumoursSmallAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.util.GridSpacingItemDecoration;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje on 01/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

public class RumoursFragment extends BaseFragment {

    RumoursSmallAdapter listViewAdapter;
    RumoursBigAdapter recyclerViewAdapte;

    @BindView(R.id.fragment_rumors_list_view)
    ListView rumourListView;
    @BindView(R.id.fragment_rumors_single_rumour_info_1)
    TextView rumourDescription1;
    @BindView(R.id.fragment_rumors_single_rumour_info_2)
    TextView rumourDescription2;
    @BindView(R.id.fragment_rumors_single_rumour_info_3)
    TextView rumourDescription3;
    @BindView(R.id.fragment_rumors_single_rumour_info_4)
    TextView rumourDescription4;
    @BindView(R.id.fragment_rumors_single_rumour_time_1)
    TextView rumourTime1;
    @BindView(R.id.fragment_rumors_single_rumour_time_2)
    TextView rumourTime2;
    @BindView(R.id.fragment_rumors_single_rumour_time_3)
    TextView rumourTime3;
    @BindView(R.id.fragment_rumors_single_rumour_time_4)
    TextView rumourTime4;
    @BindView(R.id.fragment_rumors_top_headline)
    TextView headline;
    @BindView(R.id.fragment_rumors_top_image)
    ImageView image;
    @BindView(R.id.fragment_rumors_all_big_list)
    RecyclerView rumourBigList;
    @BindView(R.id.fragment_rumors_root)
    ScrollView root;

    public RumoursFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rumours, container, false);
        ButterKnife.bind(this, view);
        listViewAdapter = new RumoursSmallAdapter(getActivity());
        rumourListView.setAdapter(listViewAdapter);
        Utility.setListViewHeight(rumourListView,listViewAdapter);

        recyclerViewAdapte = new RumoursBigAdapter(getActivity());
        rumourBigList.setAdapter(recyclerViewAdapte);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        rumourBigList.addItemDecoration(new GridSpacingItemDecoration(2,16,true));
        rumourBigList.setLayoutManager(layoutManager);
        return view;
    }

}