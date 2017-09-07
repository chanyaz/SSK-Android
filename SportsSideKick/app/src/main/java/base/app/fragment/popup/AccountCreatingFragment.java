package base.app.fragment.popup;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import base.app.R;
import base.app.adapter.AccountCreatingAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.fragment.IgnoreBackHandling;
import base.app.fragment.instance.WallFragment;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
@IgnoreBackHandling
public class AccountCreatingFragment extends BaseFragment implements AccountCreatingAdapter.OnClick {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    AccountCreatingAdapter accountCreatingAdapter;
    boolean animatonEnable;
    CountDownTimer waitTimer;
    @BindView(R.id.progress_bar)
    AVLoadingIndicatorView progressBar;
    @BindView(R.id.lets_go_button)
    Button letsGoButton;
    @BindView(R.id.title_text)
    TextView titleText;
    private String[] values;
    @OnClick(R.id.lets_go_button)
    void letsGo()
    {
        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
    }

    public AccountCreatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_account_creating, container, false);
        ButterKnife.bind(this, view);
        animatonEnable = true;
        values = getActivity().getResources().getStringArray(R.array.account_creating_text);
        if (titleText != null) {
            titleText.setText(Utility.fromHtml(getString(R.string.welcome_your_account_is_being_setup)));
        }
        letsGoButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 9, LinearLayoutManager.VERTICAL, false);
        accountCreatingAdapter = new AccountCreatingAdapter(getActivity(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(accountCreatingAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startTimer();
            }
        }, 5000);
        return view;
    }

    private void startTimer() {
        if (animatonEnable) {
            accountCreatingAdapter.setItemPosition(0);
            titleText.setText(Utility.fromHtml(values[0]));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;
                }
                waitTimer = new CountDownTimer(27000, 3000) {

                    public void onTick(long millisUntilFinished) {
                        if (animatonEnable) {
                            accountCreatingAdapter.setItemPosition((accountCreatingAdapter.getOldPosition() + 2));
                            titleText.setText(Utility.fromHtml(values[accountCreatingAdapter.getOldPosition()/2]));
                        }
                    }

                    public void onFinish() {
                        progressBar.setVisibility(View.INVISIBLE);
                        titleText.setText(Utility.fromHtml(getString(R.string.your_ready_to_play)));
                        letsGoButton.setVisibility(View.VISIBLE);
                        accountCreatingAdapter.setItemPosition((-1));

                    }
                }.start();
            }
        }, 3000);


    }


    @Override
    public void itemClicked(int position) {
        animatonEnable = false;
        titleText.setText(Utility.fromHtml(values[accountCreatingAdapter.getOldPosition()/2]));
    }

}
