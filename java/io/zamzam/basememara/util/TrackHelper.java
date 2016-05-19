package io.zamzam.basememara.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.base.Strings;

import io.zamzam.basememara.App;

/**
 * Created by basem on 7/30/15.
 */
public class TrackHelper {

    public static void screenView(String name) {
        Tracker tracker = App.getTracker();
        tracker.setScreenName(name);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void event(String category, String action, String label, int value) {
        Tracker tracker = App.getTracker();
        HitBuilders.EventBuilder hitBuilder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action);

        if (!Strings.isNullOrEmpty(label)) {
            hitBuilder.setLabel(label);
        }

        if (value > 0) {
            hitBuilder.setValue(value);
        }

        tracker.send(hitBuilder.build());
    }

    public static void event(String category, String action, String label) {
        event(category, action, label, 0);
    }

    public static void event(String category, String action, int value) {
        event(category, action, null, value);
    }

    public static void event(String category, String action) {
        event(category, action, null, 0);
    }

}
