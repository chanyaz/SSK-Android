package base.app.util.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import base.app.R;

/**
 * Created by Nemanja Jovanovic on 30/03/2017.
 */
public class AutofitDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public AutofitDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.margin_10);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}