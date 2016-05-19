package io.zamzam.basememara.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.zamzam.basememara.contract.IndentifierModel;

/**
 * Created by basem on 6/30/15.
 */
public class TermModel extends RealmObject implements IndentifierModel {

    @PrimaryKey
    private int ID;

    private String name;
    private String description;
    private String link;
    private String slug;
    private String taxonomy;
    private TermModel parent;
    private int count;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public TermModel getParent() {
        return parent;
    }

    public void setParent(TermModel parent) {
        this.parent = parent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
