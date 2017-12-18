package base.app.util.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import base.app.R;
import base.app.model.ticker.NewsTickerInfo;
import base.app.model.ticker.NextMatchModel;
import base.app.util.NextMatchCountdown;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Filip on 10/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NextMatchView extends RelativeLayout {

    private static final String TAG = "Next Match View";

    @BindView(R.id.next_match_container)
    View nextMatchContainer;

    @BindView(R.id.left_image)
    ImageView logoOfFirstTeam;

    @BindView(R.id.right_image)
    ImageView logoOfSecondTeam;

    @BindView(R.id.left_name)
    TextView nameOfFirstTeam;

    @BindView(R.id.right_name)
    TextView nameOfSecondTeam;

    @BindView(R.id.date)
    TextView dateTextView;

    @BindView(R.id.countdown)
    TextView countdownTextView;

    long timestamp;

    public NextMatchView(Context context) {
        super(context);
        initView();
    }

    public NextMatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NextMatchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.next_match_view, this);
        ButterKnife.bind(view);
        NewsTickerInfo info = NextMatchModel.getInstance().loadTickerInfoFromCache();
        if(info!=null && NextMatchModel.getInstance().isNextMatchUpcoming()){
            updateCountdownTimer();
            if(logoOfFirstTeam.getDrawable()==null){
                Glide.with(this).load(info.getFirstClubUrl()).into(logoOfFirstTeam);
                Glide.with(this).load(info.getSecondClubUrl()).into(logoOfSecondTeam);
            }
            timestamp = Long.parseLong(info.getMatchDate());
            nameOfFirstTeam.setText(info.getFirstClubName());
            nameOfSecondTeam.setText(info.getSecondClubName());
            dateTextView.setText(NextMatchCountdown.getTextValue(getContext(),timestamp,true));


            final Handler handler = new Handler();
            final int delay = 100; //milliseconds

            handler.postDelayed(new Runnable(){
                public void run(){
                    updateCountdownTimer();
                    handler.postDelayed(this, delay);
                }
            }, delay);
            nextMatchContainer.setVisibility(View.VISIBLE);
        } else {
            nextMatchContainer.setVisibility(View.GONE);
        }
    }

    private void updateCountdownTimer(){
        countdownTextView.setText(NextMatchCountdown.getCountdownValue(timestamp));
    }
}
