package edu.bk.thesis.biodiary.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import edu.bk.thesis.biodiary.R;


public class DividerItemDecoration extends RecyclerView.ItemDecoration
{
    private Drawable mDivider;

    public DividerItemDecoration(Context context)
    {
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider_item_line);
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        outRect.top = mDivider.getIntrinsicHeight();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        int left  = parent.getPaddingLeft();
        int right = parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams layoutParams
                    = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top    = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }

    }
}
