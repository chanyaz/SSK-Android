package tv.sportssidekick.sportssidekick.fragment.instance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class InitialFragment extends BaseFragment {

    Button logInButton, signUpButton;
    TextView slideTextOne, slideTextTwo;
    View circleOne, circleTwo;
    int postion = 0;
    Context context;
    Timer slideText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_initial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        logInButton = (Button) view.findViewById(R.id.login_log_in_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(FragmentEvent.Type.LOGIN);
                if(fragmentEvent!=null){
                    EventBus.getDefault().post(fragmentEvent);
                }
            }
        });
        signUpButton = (Button) view.findViewById(R.id.login_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(FragmentEvent.Type.SIGN_UP);
                if(fragmentEvent!=null){
                    EventBus.getDefault().post(fragmentEvent);
                }
            }
        });

        slideTextOne = (TextView) view.findViewById(R.id.login_slide_text_1);
        slideTextTwo = (TextView) view.findViewById(R.id.login_slide_text_2);
        circleOne = (View) view.findViewById(R.id.login_circle_one);
        circleTwo = (View) view.findViewById(R.id.login_circle_two);


        final Utility utility = new Utility();

        slideText = new Timer();
        slideText.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (postion) {
                            case 0:
                                postion = 1;
                                utility.slideText(slideTextOne, slideTextTwo, circleOne, circleTwo,  true, context);
                                break;
                            case 1:
                                postion = 0;
                                utility.slideText(slideTextOne, slideTextTwo, circleOne, circleTwo,  false, context);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
            }
        }, 0, Constant.LOGIN_TEXT_TIME);
    }


    @Override
    public void onPause() {
        super.onPause();
        stopTImerSlideText();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTImerSlideText();
    }

    void stopTImerSlideText()
    {
        if (slideText!=null)
        {
            slideText.cancel();
        }
    }
}
