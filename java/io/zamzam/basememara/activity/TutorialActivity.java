package io.zamzam.basememara.activity;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

import io.zamzam.basememara.Config;
import io.zamzam.basememara.fragment.TutorialFragment;
import io.zamzam.basememara.util.ResourceHelper;

/**
 * Created by basem on 7/29/15.
 */
public class TutorialActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        // Collect all tutorials and create slide dynamically
        for (int item : ResourceHelper.getMultiTypedArrayIds("tutorial")) {
            addSlide(TutorialFragment.newInstance(item));
        }
    }

    @Override
    public void onDonePressed() {
        Config.isTutorialFinished(true);
        this.finish();
    }
}
