package io.zamzam.basememara.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.R;
import io.zamzam.basememara.contract.AlertDialogDelegate;
import io.zamzam.basememara.contract.WebViewDelegate;
import io.zamzam.basememara.model.NotificationModel;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.util.DataHelper;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.TrackHelper;
import io.zamzam.basememara.util.WebHelper;

/**
 * Created by basem on 7/6/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>
    implements WebViewDelegate, AlertDialogDelegate {
    private Context mContext = null;
    private List<NotificationModel> mData = null;

    public NotificationAdapter(Context context) {
        mContext = context;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_item, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, final int position) {
        final NotificationModel item = mData.get(position);
        final String title = Html.fromHtml(item.getTitle()).toString();

        holder.titleView.setText(title);
        holder.subTitleView.setText(item.getDate().toString());

        // Toggle favorite icon
        holder.iconImage.setImageResource(item.isRead()
                ? R.drawable.ic_drafts_black_24dp
                : R.drawable.ic_email_black_24dp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mark as read
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        item.setRead(true);
                    }
                });
                realm.close();

                NotificationAdapter.this.notifyDataSetChanged();

                // Open notification
                if (!Strings.isNullOrEmpty(item.getPost_meta().getLink())) {
                    WebHelper.openUrl(mContext,
                            item.getPost_meta().getLink(),
                            title,
                            NotificationAdapter.this,
                            NotificationAdapter.this,
                            true,
                            false,
                            position);
                } else {
                    WebHelper.openHtml(mContext,
                            item.getContent(),
                            title,
                            NotificationAdapter.this,
                            NotificationAdapter.this,
                            true,
                            position);
                }

                // Google Analytics
                TrackHelper.screenView("Notification detail - " + title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public List<NotificationModel> getData() {
        return mData;
    }

    public void setData(List<NotificationModel> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public String getNeutralText() {
        return mContext.getResources().getText(R.string.comments).toString();
    }

    @Override
    public boolean onNeutralClick(DialogInterface dialog, int position) {
        final NotificationModel item = mData.get(position);

        WebHelper.openUrl(mContext,
                Config.getBaseUrl()
                        + "/mobile-comments/?postid="
                        + item.getID(),
                mContext.getString(R.string.comments),
                false,
                false);

        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Load blog posts into activity instead of web view if applicable
        if (Uri.parse(url).getHost().equalsIgnoreCase(Uri.parse(Config.getBaseUrl()).getHost())) {
            PostModel item = DataHelper.getPostByUrl(url);

            // Load item into activity if found
            if (item != null) {
                IntentHelper.openPostDetail(mContext, item.getID());
            } else {
                // Let it open item into browser page
                WebHelper.openUrl(mContext, url);
            }
        } else {
            // Load all other URL's into web view popup
            WebHelper.openUrl(mContext, url);
        }

        return true;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icon) ImageView iconImage;
        @Bind(R.id.firstLine) TextView titleView;
        @Bind(R.id.secondLine) TextView subTitleView;

        public NotificationViewHolder(View view) {
            super(view);

            // Bind views to fields
            ButterKnife.bind(this, view);
        }
    }
}
