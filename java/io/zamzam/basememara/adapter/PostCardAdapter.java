package io.zamzam.basememara.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.zamzam.basememara.R;
import io.zamzam.basememara.fragment.PostMoreFragment;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.util.ConvertHelper;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.MediaHelper;

/**
 * Created by basem on 6/30/15.
 */
public class PostCardAdapter extends RecyclerView.Adapter<PostCardAdapter.PostCardViewHolder> {
    public static final int MINIMUM_CELL_HEIGHT = 250;
    public static final int MINIMUM_CONTENT_HEIGHT = 60;
    public static final int CELL_PADDING = 10;
    public static final int CONTENT_PADDING = 10;

    private Context mContext = null;
    private List<PostModel> mData = null;
    private int mCellWidth = 0;
    private int mContentHeight = 0;

    public PostCardAdapter(Context context) {
        mContext = context;

        // Determine card element dimensions later render use
        mCellWidth = mContext.getResources().getDisplayMetrics().widthPixels / 2
                - ConvertHelper.convertDpToPixel(mContext, CELL_PADDING);
        mContentHeight = ConvertHelper.convertDpToPixel(mContext,
                MINIMUM_CONTENT_HEIGHT - CONTENT_PADDING);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.post_list_item, parent, false);

        return new PostCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostCardViewHolder holder, final int position) {
        final PostModel item = mData.get(position);

        // Populate view holder
        holder.titleView.setText(Html.fromHtml(item.getTitle()));
        MediaHelper.displayImage(item, holder.thumbnailImage);

        // Toggle favorite icon
        holder.moreImage.setImageResource(item.isFavorite()
                ? R.drawable.ic_more_vert_red_24dp
                : R.drawable.ic_more_vert_black_24dp);

        // Handle click event for favorite
        holder.moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                PostMoreFragment dialog = new PostMoreFragment();
                dialog.setItemId(item.getID());
                dialog.setMoreIcon(holder.moreImage);
                dialog.show(fm, "fragment_post_more_action");
            }
        });

        // Handle click event for row item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.openPostDetail(v.getContext(), item.getID());
            }
        });

        setViewDynamicHeight(holder.cardView, item);
    }

    public void setViewDynamicHeight(View view, PostModel item) {
        int viewHeight = MINIMUM_CELL_HEIGHT;

        // Determine height using image
        if (item.getFeatured_image() != null
                && item.getFeatured_image().getAttachment_meta() != null) {
            int imageWidth = item.getFeatured_image().getAttachment_meta().getWidth();
            int imageHeight = item.getFeatured_image().getAttachment_meta().getHeight();

            viewHeight = imageHeight * mCellWidth / imageWidth;

            if (viewHeight < MINIMUM_CELL_HEIGHT) {
                viewHeight = MINIMUM_CELL_HEIGHT;
            }
        }

        // Adjust cell dimensions
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = viewHeight + mContentHeight;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public List<PostModel> getData() {
        return mData;
    }

    public void setData(List<PostModel> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(PostCardViewHolder holder) {
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnLongClickListener(null);
        holder.moreImage.setOnClickListener(null);
        super.onViewRecycled(holder);
    }

    public class PostCardViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        @Bind(R.id.row_blog_post_card_view) CardView cardView;
        @Bind(R.id.row_blog_post_thumbnail_image) ImageView thumbnailImage;
        @Bind(R.id.row_blog_post_title_text) TextView titleView;
        @Bind(R.id.row_blog_post_more) ImageView moreImage;

        public PostCardViewHolder(View view) {
            super(view);

            // Bind views to fields
            ButterKnife.bind(this, view);

            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Mark all as read?");
            menu.add(Menu.NONE, 0, 0, "OK");
            menu.add(Menu.NONE, 1, 1, "Cancel");
        }
    }
}