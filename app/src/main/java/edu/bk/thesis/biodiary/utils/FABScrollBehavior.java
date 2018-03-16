package edu.bk.thesis.biodiary.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


public class FABScrollBehavior extends FloatingActionButton.Behavior
{
    public FABScrollBehavior(Context context, AttributeSet attributeSet)
    {
        super();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target,
                                       int axes,
                                       int type)
    {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                               @NonNull FloatingActionButton child,
                               @NonNull View target,
                               int dxConsumed,
                               int dyConsumed,
                               int dxUnconsumed,
                               int dyUnconsumed,
                               int type)
    {
        super.onNestedScroll(coordinatorLayout,
                             child,
                             target,
                             dxConsumed,
                             dyConsumed,
                             dxUnconsumed,
                             dyUnconsumed,
                             type);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide(new FloatingActionButton.OnVisibilityChangedListener()
            {
                @Override
                public void onHidden(FloatingActionButton fab)
                {
                    super.onHidden(fab);
                    fab.setVisibility(View.INVISIBLE);
                }
            });
        }
        else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }

        /*
        if (dyConsumed > 0) {
            CoordinatorLayout.LayoutParams layoutParams
                    = (CoordinatorLayout.LayoutParams) child
                    .getLayoutParams();
            int fab_bottomMargin = layoutParams.bottomMargin;
            child.animate()
                 .translationY(child.getHeight() + fab_bottomMargin)
                 .setInterpolator(new LinearInterpolator())
                 .start();
        }
        else if (dyConsumed < 0) {
            child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        }
        */
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,
                                   FloatingActionButton child,
                                   View dependency)
    {
        return dependency instanceof RecyclerView;
    }
}
