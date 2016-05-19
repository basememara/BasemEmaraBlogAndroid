package io.zamzam.basememara.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.zamzam.basememara.R;
import io.zamzam.basememara.model.TutorialPage;

/**
 * Created by basem on 7/29/15.
 */
public class TutorialFragment extends Fragment {

    private static final String ARG_TUTORIAL_RES_ID = "tutorialResId";
    private TutorialPage mPage;

    @Bind(R.id.tutorial_main) LinearLayout mainLayout;
    @Bind(R.id.tutorial_title) TextView titleLabel;
    @Bind(R.id.tutorial_desc) TextView descLabel;
    @Bind(R.id.tutorial_icon) ImageView iconImage;

    public TutorialFragment() {}

    public static TutorialFragment newInstance(int tutorialResId) {
        TutorialFragment frag = new TutorialFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_TUTORIAL_RES_ID, tutorialResId);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_TUTORIAL_RES_ID)) {
            // Create tutorial page object out of resource array
            TypedArray tutorialResource = this.getResources().obtainTypedArray(getArguments().getInt(ARG_TUTORIAL_RES_ID));
            mPage = new TutorialPage();
            mPage.title = tutorialResource.getString(0);
            mPage.desc = tutorialResource.getString(1);
            mPage.icon = tutorialResource.getDrawable(2);
            mPage.bgColor = tutorialResource.getInt(3, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        // Bind views to fields
        ButterKnife.bind(this, view);

        // Dynamically update interface
        titleLabel.setText(mPage.title);
        descLabel.setText(mPage.desc);
        iconImage.setImageDrawable(mPage.icon);
        mainLayout.setBackgroundColor(mPage.bgColor);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}