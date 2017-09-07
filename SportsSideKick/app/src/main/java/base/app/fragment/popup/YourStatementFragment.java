package base.app.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import base.app.R;
import base.app.adapter.StatementAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Filip on 1/19/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class YourStatementFragment extends BaseFragment {

    @BindView(R.id.stats_recycler_view)
    RecyclerView statsRecyclerView;

    public YourStatementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_statement, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        statsRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Pair<String, String>> values = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            values.add(new Pair<>("Some line item " + i, "+" + i*100));
        }

        StatementAdapter adapter = new StatementAdapter();
        adapter.getValues().addAll(values);
        statsRecyclerView.setAdapter(adapter);
        return view;
    }

    @OnClick(R.id.close)
    public void closeOnClick() {
        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class, true));
    }

}




