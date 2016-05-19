package io.zamzam.basememara.model;

import io.realm.RealmObject;

/**
 * Created by basem on 6/30/15.
 */
public class AttachmentMetaModel extends RealmObject {
    private int width;
    private int height;
    private SizesModel sizes;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public SizesModel getSizes() {
        return sizes;
    }

    public void setSizes(SizesModel sizes) {
        this.sizes = sizes;
    }
}