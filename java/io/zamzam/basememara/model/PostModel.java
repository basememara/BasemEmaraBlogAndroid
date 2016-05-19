package io.zamzam.basememara.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.zamzam.basememara.contract.IndentifierModel;
import io.zamzam.basememara.contract.ModifiableModel;

/**
 * Created by basem on 6/30/15.
 */
public class PostModel extends RealmObject implements IndentifierModel, ModifiableModel {

    @PrimaryKey
    private int ID;

    private String title;
    private String excerpt;
    private String content;
    private String slug;
    private String status;
    private String type;
    private String link;
    private Date date;
    private Date modified;
    private boolean favorite;
    private boolean read;
    private int viewsCount;
    private int commentsCount;
    private AuthorModel author;
    private ImageModel featured_image;
    private TermsModel terms;
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

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
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

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public AuthorModel getAuthor() {
        return author;
    }

    public void setAuthor(AuthorModel author) {
        this.author = author;
    }

    public ImageModel getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(ImageModel featured_image) {
        this.featured_image = featured_image;
    }

    public TermsModel getTerms() {
        return terms;
    }

    public void setTerms(TermsModel terms) {
        this.terms = terms;
    }
}