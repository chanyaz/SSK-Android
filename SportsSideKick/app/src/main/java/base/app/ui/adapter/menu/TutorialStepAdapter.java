package base.app.ui.adapter.menu;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import base.app.R;
import base.app.data.user.tutorial.WallStep;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Djordje on 04/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TutorialStepAdapter extends RecyclerView.Adapter<TutorialStepAdapter.ViewHolder> {
    private static final String TAG = "TutorialAdapter";

    private ArrayList<WallStep> wallSteps;

    public ArrayList<WallStep> getWallSteps() {
        return wallSteps;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @Nullable
        @BindView(R.id.step_text)
        TextView rowText;
        @Nullable
        @BindView(R.id.step_number)
        TextView rowNumber;
        @Nullable
        @BindView(R.id.step_image)
        ImageView rowImage;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public TutorialStepAdapter() {
        wallSteps = new ArrayList<>();
    }

    @Override
    public TutorialStepAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        TutorialStepAdapter.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tutorial_step, parent, false);
        viewHolder = new TutorialStepAdapter.ViewHolder(view);
        // view.setLayoutParams(new RecyclerView.LayoutParams((recyclerViewWidth - R.dimen.padding_16)/2, RecyclerView.LayoutParams.WRAP_CONTENT));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!wallSteps.isEmpty()) {
            WallStep step = wallSteps.get(position);
            holder.rowText.setText(step.getStepText());
            holder.rowNumber.setText("STEP "+ String.valueOf(position+1));
            holder.rowImage.setImageDrawable(step.getStepIcon());
        }
    }


    @Override
    public int getItemCount() {
        return wallSteps.size() ;
    }
}