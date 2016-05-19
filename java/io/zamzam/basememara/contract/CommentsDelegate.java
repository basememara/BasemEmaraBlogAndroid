package io.zamzam.basememara.contract;

import io.realm.Realm;
import io.zamzam.basememara.model.PostModel;

/**
 * Created by basem on 7/7/15.
 */
public interface CommentsDelegate {

    public Realm getRealm();
    public PostModel getItem();
    public void finishedLoadingCommentsCount(int commentsCount);

}
