package io.zamzam.basememara.model;

import io.realm.RealmObject;

/**
 * Created by basem on 6/30/15.
 */
public class PostMetaModel extends RealmObject {
    private String type;
    private String file;
    private String link;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
