package base.app.util.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Djordje Krutil on 09/01/2017.
 */

public class StaggeredLayoutManagerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int SPAN_COUNT = 2;

    private int halfSpace;
    private boolean includeEdge;
    private int space;
    private boolean isTablet;

    public StaggeredLayoutManagerItemDecoration(int space, boolean includeEdge, boolean isTablet) {
        this.halfSpace = space / 2;
        this.includeEdge = includeEdge;
        this.space = space;
        this.isTablet = isTablet;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isTablet) {
            if (parent.getPaddingLeft() != halfSpace) {
                parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
                parent.setClipToPadding(false);
            }
            outRect.top = halfSpace;
            outRect.bottom = halfSpace;
            outRect.left = halfSpace;
            outRect.right = halfSpace;
        } else {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % SPAN_COUNT; // item column
            if (includeEdge) {
                outRect.left = space - column * space / SPAN_COUNT; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * space / SPAN_COUNT; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < SPAN_COUNT) { // top edge
                    outRect.top = space;
                }
                outRect.bottom = space; // item bottom
            } else {
                outRect.left = column * space / SPAN_COUNT; // column * ((1f / spanCount) * spacing)
                outRect.right = space - (column + 1) * space / SPAN_COUNT; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= SPAN_COUNT) {
                    outRect.top = space; // item top
                }
            }
        }
    }
}