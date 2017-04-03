package tv.sportssidekick.sportssidekick.model.tutorial;

import android.content.Context;
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

    public static TutorialModel getInstance(){
        if(instance==null){
            instance = new TutorialModel();
        }
        return instance;
    }

    public void initialize(Context context){
        tutorialItems = new ArrayList<>();

        List<WallStep> wallSteps = new ArrayList<>();
        /* TIP 1: wall steps */
       wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_1_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_friends)
        ));
        wallSteps.add(new WallStep(
            context.getResources().getString(R.string.tip_1_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_sportingcp)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_1_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_toggle)
        ));
        // Tip 1
        tutorialItems.add(new WallTip(
                1,
                context.getResources().getString(R.string.tip_1_text),
                context.getResources().getString(R.string.tip_1_title),
                wallSteps,
                context.getResources().getString(R.string.tip_1_description),
                context.getResources().getString(R.string.tip_1_end),
                5));

        wallSteps = new ArrayList<>();

        /* TIP 2: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_2_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_chat)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_2_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_sportingcp)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_2_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_typing)
        ));
        // Tip 2
        tutorialItems.add(new WallTip(
                2,
                context.getResources().getString(R.string.tip_2_text),
                context.getResources().getString(R.string.tip_2_title),
                wallSteps,
                context.getResources().getString(R.string.tip_2_description),
                context.getResources().getString(R.string.tip_2_end),
                5));

        /* TIP 3: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_3_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_tv)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_3_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_remote)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_3_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_playlist)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_3_step_4),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_play)
        ));
        // Tip 3
        tutorialItems.add(new WallTip(
                3,
                context.getResources().getString(R.string.tip_3_text),
                context.getResources().getString(R.string.tip_3_title),
                wallSteps,
                context.getResources().getString(R.string.tip_3_description),
                context.getResources().getString(R.string.tip_3_end),
                5));

        /* TIP 4: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_4_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_news)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_4_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_tap)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_4_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_close)
        ));
        // Tip 4
        tutorialItems.add(new WallTip(
                4,
                context.getResources().getString(R.string.tip_4_text),
                context.getResources().getString(R.string.tip_4_title),
                wallSteps,
                context.getResources().getString(R.string.tip_4_description),
                context.getResources().getString(R.string.tip_4_end),
                5));

         /* TIP 5: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_5_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_rumour)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_5_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_tap)
        ));
        // Tip 5
        tutorialItems.add(new WallTip(
                5,
                context.getResources().getString(R.string.tip_5_text),
                context.getResources().getString(R.string.tip_5_title),
                wallSteps,
                context.getResources().getString(R.string.tip_5_description),
                context.getResources().getString(R.string.tip_5_end),
                5));

         /* TIP 6: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_6_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_stats)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_6_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_tabletap)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_6_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_tap)
        ));
        // Tip 6
        tutorialItems.add(new WallTip(
                6,
                context.getResources().getString(R.string.tip_6_text),
                context.getResources().getString(R.string.tip_6_title),
                wallSteps,
                context.getResources().getString(R.string.tip_6_description),
                context.getResources().getString(R.string.tip_6_end),
                5));

       /* TIP 7: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_7_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_profile)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_7_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_pencil)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_7_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_wallet)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_7_step_4),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_stash)
        ));
        // Tip 7
        tutorialItems.add(new WallTip(
                7,
                context.getResources().getString(R.string.tip_7_text),
                context.getResources().getString(R.string.tip_7_title),
                wallSteps,
                context.getResources().getString(R.string.tip_7_description),
                context.getResources().getString(R.string.tip_7_end),
                5));

       /* TIP 8: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_8_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_friends)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_8_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_plus)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_8_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_typing)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_8_step_4),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_confirm)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_8_step_5),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_typing)
        ));
        // Tip 8
        tutorialItems.add(new WallTip(
                8,
                context.getResources().getString(R.string.tip_8_text),
                context.getResources().getString(R.string.tip_8_title),
                wallSteps,
                context.getResources().getString(R.string.tip_8_description),
                context.getResources().getString(R.string.tip_8_end),
                5));

       /* TIP 9: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_9_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_chat)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_9_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_chatadd)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_9_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_typing)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_9_step_4),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_friendexample)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_9_step_5),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_toggle)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_9_step_6),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_confirm)
        ));
        // Tip 9
        tutorialItems.add(new WallTip(
                9,
                context.getResources().getString(R.string.tip_9_text),
                context.getResources().getString(R.string.tip_9_title),
                wallSteps,
                context.getResources().getString(R.string.tip_9_description),
                context.getResources().getString(R.string.tip_9_end),
                5));
     /* TIP 10: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_10_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_news)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_10_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_newsmenu)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_10_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_share)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_10_step_4),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_confirm)
        ));
        // Tip 10
        tutorialItems.add(new WallTip(
                10,
                context.getResources().getString(R.string.tip_10_text),
                context.getResources().getString(R.string.tip_10_title),
                wallSteps,
                context.getResources().getString(R.string.tip_10_description),
                context.getResources().getString(R.string.tip_10_end),
                5));
         /* TIP 11: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_11_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_videochat)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_11_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_friendexample)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_11_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_call)
        ));
        // Tip 11
        tutorialItems.add(new WallTip(
                11,
                context.getResources().getString(R.string.tip_11_text),
                context.getResources().getString(R.string.tip_11_title),
                wallSteps,
                context.getResources().getString(R.string.tip_11_description),
                context.getResources().getString(R.string.tip_11_end),
                5));
      /* TIP 12: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_12_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_pin)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_12_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_wall)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_12_step_3),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_newsmenu)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_12_step_4),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_pin)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_12_step_5),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_pincost)
        ));
        // Tip 12
        tutorialItems.add(new WallTip(
                12,
                context.getResources().getString(R.string.tip_12_text),
                context.getResources().getString(R.string.tip_12_title),
                wallSteps,
                context.getResources().getString(R.string.tip_12_description),
                context.getResources().getString(R.string.tip_12_end),
                5));
      /* TIP 13: wall steps */
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_13_step_1),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_store)
        ));
        wallSteps.add(new WallStep(
                context.getResources().getString(R.string.tip_13_step_2),
                ContextCompat.getDrawable(context, R.drawable.tutorialicon_nav)
        ));
        // Tip 13
        tutorialItems.add(new WallTip(
                13,
                context.getResources().getString(R.string.tip_13_text),
                context.getResources().getString(R.string.tip_13_title),
                wallSteps,
                context.getResources().getString(R.string.tip_13_description),
                context.getResources().getString(R.string.tip_13_end),
                5));

    }
}
