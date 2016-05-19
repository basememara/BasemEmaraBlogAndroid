package io.zamzam.basememara.util;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;

/**
 * Created by basem on 7/5/15.
 */
public class LayoutHelper {

    public static void ExpandToolbar(AppBarLayout appBarLayout, CoordinatorLayout rootLayout) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if(behavior != null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(rootLayout, appBarLayout, null, 0, 1, new int[2]);
        }
    }

}
