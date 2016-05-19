package io.zamzam.basememara.model;

import io.realm.RealmObject;

/**
 * Created by basem on 6/30/15.
 */
public class SizesModel extends RealmObject {
    private ThumbnailModel thumbnail;

    public ThumbnailModel getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailModel thumbnail) {
        this.thumbnail = thumbnail;
    }
}