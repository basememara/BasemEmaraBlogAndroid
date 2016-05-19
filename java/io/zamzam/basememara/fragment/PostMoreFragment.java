package io.zamzam.basememara.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.zamzam.basememara.R;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.TrackHelper;
import io.zamzam.basememara.util.WebHelper;

/**
 * Created by basem on 7/29/15.
 */
public class PostMoreFragment extends DialogFragment {
    private Realm realm;
    private int itemId = 0;
    private PostModel mItem;
    private AlertDialog mDialog;
    private ImageView moreIcon;

    @Bind(R.id.more_favorite) ImageView favoriteImage;
    @Bind(R.id.more_mark_as_read) ImageView readImage;
    @Bind(R.id.more_favorite_label) TextView favoriteLabel;
    @Bind(R.id.more_mark_as_read_label) TextView readLabel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_post_more, null);

        // Bind views to fields
        ButterKnife.bind(this, view);

        // Build and cache dialog
        builder.setView(view);
        mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Get item entity from storage
        realm = Realm.getDefaultInstance();
        mItem = realm.where(PostModel.class).equalTo("ID", itemId).findFirst();

        // Dynamically update interface
        if (mItem.isFavorite()) {
            favoriteImage.setImageResource(R.drawable.more_unfavorite);
            favoriteLabel.setText(R.string.unfavorite);
        }

        if (mItem.isRead()) {
            readImage.setImageResource(R.drawable.more_mark_as_unread);
            readLabel.setText(R.string.mark_as_unread);
        }

        return mDialog;
    }

    @OnClick(R.id.more_favorite)
    public void onFavoriteClick() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mItem.setFavorite(!mItem.isFavorite());

                // Update interface
                if (moreIcon != null) {
                    moreIcon.setImageResource(mItem.isFavorite()
                            ? R.drawable.ic_more_vert_red_24dp
                            : R.drawable.ic_more_vert_black_24dp);
                }

                // Google Analytics
                if (mItem.isFavorite()) {
                    TrackHelper.event("Favorite", "Post", mItem.getTitle(), mItem.getID());
                }

                mDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.more_mark_as_read)
    public void onReadClick() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mItem.setRead(!mItem.isRead());
                mDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.more_browser)
    public void onBrowserClick() {
        WebHelper.openBrowser(getActivity(), mItem.getLink());
        mDialog.dismiss();
    }

    @OnClick(R.id.more_share)
    public void onShareClick() {
        IntentHelper.share(getActivity(),
                mItem.getTitle(),
                mItem.getTitle() + ": " + mItem.getLink());

        // Google Analytics
        TrackHelper.event("Share", "Post", mItem.getTitle(), mItem.getID());

        mDialog.dismiss();
    }

    public void setItemId(int value) {
        itemId = value;
    }

    public void setMoreIcon(ImageView value) {
        moreIcon = value;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.close();
        }
    }
}