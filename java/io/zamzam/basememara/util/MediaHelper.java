package io.zamzam.basememara.util;

import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.zamzam.basememara.App;
import io.zamzam.basememara.R;
import io.zamzam.basememara.model.PostModel;

/**
 * Created by basem on 6/30/15.
 */
public class MediaHelper {

    public static void displayImage(String uri, ImageView imageView, boolean centerCrop) {
        DrawableRequestBuilder builder = Glide.with(App.getStaticContext())
                .load(uri)
                .placeholder(R.drawable.ic_empty)
                .error(R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if (centerCrop) {
            builder = builder.centerCrop();
        }

        builder.into(imageView);
    }

    public static void displayImage(String uri, ImageView imageView) {
        displayImage(uri, imageView, false);
    }

    public static void displayThumbnail(PostModel item, ImageView imageView, boolean centerCrop) {
        if (item.getFeatured_image() != null
                && item.getFeatured_image().getAttachment_meta() != null
                && item.getFeatured_image().getAttachment_meta().getSizes() != null
                && item.getFeatured_image().getAttachment_meta().getSizes().getThumbnail() != null
                && item.getFeatured_image().getAttachment_meta().getSizes().getThumbnail().getUrl() != null) {
            displayImage(
                    item.getFeatured_image().getAttachment_meta().getSizes().getThumbnail().getUrl(),
                    imageView,
                    centerCrop);
        } else {
            imageView.setImageResource(R.drawable.ic_empty);
        }
    }

    public static void displayThumbnail(PostModel item, ImageView imageView) {
        displayThumbnail(item, imageView, false);
    }

    public static void displayImage(PostModel item, ImageView imageView, boolean centerCrop) {
        if (item.getFeatured_image() != null
                && item.getFeatured_image().getSource() != null) {
            displayImage(
                    item.getFeatured_image().getSource(),
                    imageView,
                    centerCrop);
        } else {
            imageView.setImageResource(R.drawable.ic_empty);
        }
    }

    public static void displayImage(PostModel item, ImageView imageView) {
        displayImage(item, imageView, false);
    }
}
