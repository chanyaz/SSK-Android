package tv.sportssidekick.sportssidekick.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Nemanja Jovanovic on 29/03/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private int columnCount;

    private boolean mNeedLeftSpacing = false;

    public GridItemDecoration(int spacing, int columnCount) {
        this.spacing = spacing;
        this.columnCount = columnCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int frameWidth = (int) ((parent.getWidth() - (float) spacing * (columnCount - 1)) / columnCount);
        int padding = parent.getWidth() / columnCount - frameWidth;
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        if (itemPosition < columnCount) {
            outRect.top = 0;
        } else {
            outRect.top = spacing;
        }
        if (itemPosition % columnCount == 0) {
            outRect.left = 0;
            outRect.right = padding;
            mNeedLeftSpacing = true;
        } else if ((itemPosition + 1) % columnCount == 0) {
            mNeedLeftSpacing = false;
            outRect.right = 0;
            outRect.left = padding;
        } else if (mNeedLeftSpacing) {
            mNeedLeftSpacing = false;
            outRect.left = spacing - padding;
            if ((itemPosition + 2) % columnCount == 0) {
                outRect.right = spacing - padding;
            } else {
                outRect.right = spacing / 2;
            }
        } else if ((itemPosition + 2) % columnCount == 0) {
            mNeedLeftSpacing = false;
            outRect.left = spacing / 2;
            outRect.right = spacing - padding;
        } else {
            mNeedLeftSpacing = false;
            outRect.left = spacing / 2;
            outRect.right = spacing / 2;
        }
        outRect.bottom = 0;
    }
}