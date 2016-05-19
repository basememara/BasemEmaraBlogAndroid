package io.zamzam.basememara.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.zamzam.basememara.contract.IndentifierModel;
import io.zamzam.basememara.contract.ModifiableModel;

/**
 * Created by basem on 6/30/15.
 */
public class NotificationModel extends RealmObject implements IndentifierModel, ModifiableModel {

    @PrimaryKey
    private int ID;

    private String title;
    private String content;
    private String slug;
    private String status;
    private String type;
    private String link;
    private Date date;
    private Date modified;
    private boolean read;
    private PostMetaModel post_meta;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public PostMetaModel getPost_meta() {
        return post_meta;
    }

    public void setPost_meta(PostMetaModel post_meta) {
        this.post_meta = post_meta;
    }
}
