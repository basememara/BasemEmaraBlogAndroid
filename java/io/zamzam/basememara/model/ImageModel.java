package io.zamzam.basememara.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.zamzam.basememara.contract.IndentifierModel;

/**
 * Created by basem on 6/30/15.
 */
public class ImageModel extends RealmObject implements IndentifierModel {

    @PrimaryKey
    private int ID;

    private String title;
    private String content;
    private String source;
    private Date date;
    private Date modified;
    private String slug;
    private AttachmentMetaModel attachment_meta;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public AttachmentMetaModel getAttachment_meta() {
        return attachment_meta;
    }

    public void setAttachment_meta(AttachmentMetaModel attachment_meta) {
        this.attachment_meta = attachment_meta;
    }
}
