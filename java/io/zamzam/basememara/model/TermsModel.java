package io.zamzam.basememara.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by basem on 6/30/15.
 */
public class TermsModel extends RealmObject {
    private RealmList<TermModel> category;
    private RealmList<TermModel> post_tag;

    public RealmList<TermModel> getCategory() {
        return category;
    }

    public void setCategory(RealmList<TermModel> category) {
        this.category = category;
    }

    public RealmList<TermModel> getPost_tag() {
        return post_tag;
    }

    public void setPost_tag(RealmList<TermModel> post_tag) {
        this.post_tag = post_tag;
    }
}