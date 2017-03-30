package tv.sportssidekick.sportssidekick.model.tutorial;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.R;

/**
 * Created by Filip on 3/30/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TutorialModel {

    private List<WallTip> tutorialItems;

    private TutorialModel(){}

    private static TutorialModel instance;

    public TutorialModel getInstance(){
        if(instance==null){
            instance = new TutorialModel();
        }
        return instance;
    }

    public void initialize(Context context){
        tutorialItems = new ArrayList<>();

        List<WallStep> wallSteps = new ArrayList<WallStep>();
        // Wall steps for Tip 1
        wallSteps.add(new WallStep(
            Resources.getSystem().getString(R.string.tip_1_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_friends)
        ));
        wallSteps.add(new WallStep(
            Resources.getSystem().getString(R.string.tip_1_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_sportingcp)
        ));
        wallSteps.add(new WallStep(
                Resources.getSystem().getString(R.string.tip_1_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_toggle)
        ));
        // Wall steps for Tip 1
        tutorialItems.add(new WallTip(
                1,
                Resources.getSystem().getString(R.string.tip_1_text),
                Resources.getSystem().getString(R.string.tip_1_title),
                wallSteps,
                Resources.getSystem().getString(R.string.tip_1_description),
                Resources.getSystem().getString(R.string.tip_1_end),
                5));

    }
}
