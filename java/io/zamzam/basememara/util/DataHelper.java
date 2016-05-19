package io.zamzam.basememara.util;

import android.util.Log;

import com.google.common.base.Strings;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.zamzam.basememara.App;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.contract.CommentsDelegate;
import io.zamzam.basememara.contract.IndentifierModel;
import io.zamzam.basememara.contract.ModifiableModel;
import io.zamzam.basememara.contract.StoreNewDataDelegate;
import io.zamzam.basememara.model.NotificationModel;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.model.TermModel;

/**
 * Created by basem on 7/2/15.
 */
public class DataHelper {

    public static void initDatabase() {
        // Configure realm database
        RealmConfiguration config = new RealmConfiguration.Builder(App.getStaticContext()).build();
        Realm.setDefaultConfiguration(config);
    }

    public static void seedDatabaseIfNeeded() {
        Realm realm = Realm.getDefaultInstance();

        // Populate database first time if applicable
        if (realm.where(PostModel.class).count() == 0) {
            for (int i = 1; i <= Config.getSeedFilesCount(); i++) {
                InputStream stream = null;
                final PostModel[] data;

                // Construct file name
                String fileName = Config.getSeedFileName();
                if (i > 1) {
                    fileName += i;
                }
                fileName += ".json";

                try {
                    stream = App.getStaticContext().getAssets().open(fileName);

                    if (stream != null) {
                        Gson gson = getGsonInstance();
                        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
                        data = gson.fromJson(json, PostModel[].class);

                        // Open a transaction to store items into the realm
                        // Converts objects into proper RealmObjects managed by Realm.
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (data.length > 0) {
                                    realm.copyToRealmOrUpdate(Arrays.asList(data));
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    // Delete and recreate database
                    realm.close();
                    Realm.deleteRealm(realm.getConfiguration());
                    initDatabase();
                    return;
                } finally {
                    // Close stream when finished
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        realm.close();
    }

    public static <T extends RealmObject & IndentifierModel & ModifiableModel> void storeNewOrUpdatedData(final StoreNewDataDelegate delegate,
                                                                                                          final Class<T> clazz,
                                                                                                          final Class<T[]> clazzCollection,
                                                                                                          String url) {
        storeNewOrUpdatedData(delegate, clazz, clazzCollection, url, null);
    }

    public static <T extends RealmObject & IndentifierModel & ModifiableModel> void storeNewOrUpdatedData(final StoreNewDataDelegate delegate,
                                                                                                          final Class<T> clazz,
                                                                                                          final Class<T[]> clazzCollection,
                                                                                                          String url,
                                                                                                          final String popularUrl) {
        new AsyncHttpClient().get(url,
                new TextHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        Realm realm = Realm.getDefaultInstance();
                        final List<T> newData = new ArrayList<>();
                        final List<T> updatedData = new ArrayList<>();
                        Exception error = null;

                        try {
                            T[] data;
                            Gson gson = getGsonInstance();
                            data = gson.fromJson(response, clazzCollection);

                            // Extract new data not stored locally
                            for (int i = 0; i < data.length; i++) {
                                T item = realm.where(clazz)
                                        .equalTo("ID", data[i].getID())
                                        .findFirst();

                                if (item != null) {
                                    if (item.getModified().before(data[i].getModified())) {
                                        updatedData.add(data[i]);
                                    }
                                } else {
                                    newData.add(data[i]);
                                }
                            }

                            if (newData.size() > 0 || updatedData.size() > 0) {
                                // Open a transaction to store items into the realm
                                // Converts objects into proper RealmObjects managed by Realm.
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (newData.size() > 0) {
                                            realm.copyToRealmOrUpdate(newData);
                                        }

                                        // Merge updated data if applicable
                                        if (updatedData.size() > 0) {
                                            if (clazz == PostModel.class) {
                                                for (T item : updatedData) {
                                                    PostModel source = (PostModel) item;
                                                    PostModel destination = realm.where(PostModel.class)
                                                            .equalTo("ID", source.getID())
                                                            .findFirst();

                                                    destination.setTitle(source.getTitle());
                                                    destination.setExcerpt(source.getExcerpt());
                                                    destination.setContent(source.getContent());
                                                    destination.setModified(source.getModified());
                                                    destination.setStatus(source.getStatus());

                                                    if (destination.getViewsCount() < source.getViewsCount()) {
                                                        destination.setViewsCount(source.getViewsCount());
                                                    }

                                                    if (destination.getCommentsCount() < source.getCommentsCount()) {
                                                        destination.setCommentsCount(source.getCommentsCount());
                                                    }

                                                    if (source.getFeatured_image() != null) {
                                                        destination.setFeatured_image(realm.copyToRealmOrUpdate(source.getFeatured_image()));
                                                    }

                                                    destination.setPost_meta(realm.copyToRealm(source.getPost_meta()));

                                                    // Handle category avoiding primary key limitations of parent
                                                    destination.getTerms().getCategory().clear();
                                                    for (TermModel term : source.getTerms().getCategory()) {
                                                        TermModel entity = realm.copyToRealmOrUpdate(term);
                                                        destination.getTerms().getCategory().add(entity);
                                                    }

                                                    // Handle tag avoiding primary key limitations of parent
                                                    destination.getTerms().getPost_tag().clear();
                                                    for (TermModel term : source.getTerms().getPost_tag()) {
                                                        TermModel entity = realm.copyToRealmOrUpdate(term);
                                                        destination.getTerms().getPost_tag().add(entity);
                                                    }
                                                }
                                            } else if (clazz == NotificationModel.class) {
                                                for (T item : updatedData) {
                                                    NotificationModel source = (NotificationModel) item;
                                                    NotificationModel destination = realm.where(NotificationModel.class)
                                                            .equalTo("ID", source.getID())
                                                            .findFirst();

                                                    destination.setTitle(source.getTitle());
                                                    destination.setContent(source.getContent());
                                                    destination.setModified(source.getModified());
                                                    destination.setStatus(source.getStatus());
                                                    destination.setPost_meta(realm.copyToRealm(source.getPost_meta()));
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            error = e;
                            Log.e("Sync new data", e.getMessage());
                        } finally {
                            realm.close();

                            if (error == null) {
                                // Handle popular data if applicable
                                if (!Strings.isNullOrEmpty(popularUrl) && clazz == PostModel.class) {
                                    populatePopularData(delegate, popularUrl, newData.size(), updatedData.size());
                                } else {
                                    delegate.finishedLoadingData(newData.size(), updatedData.size(), 0);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                        // Log error message to help solve any problems
                        Log.e("Sync new data", statusCode + " " + e.getMessage());
                    }
                });
    }

    public static void populatePopularData(final StoreNewDataDelegate delegate, String url, final int addedCount, final int updatedCount) {
        new AsyncHttpClient().get(url,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Realm realm = Realm.getDefaultInstance();
                        int count = 0;

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);
                                final int id = row.getInt("ID");
                                final int viewsCount = !row.isNull("views_count")
                                        ? row.getInt("views_count") : 0;
                                final int commentsCount = !row.isNull("comments_count")
                                        ? row.getInt("comments_count") : 0;
                                final PostModel item = realm.where(PostModel.class)
                                        .equalTo("ID", id)
                                        .findFirst();

                                if (item != null
                                        && (item.getViewsCount() < viewsCount
                                            || item.getCommentsCount() < commentsCount)) {
                                    // Open a transaction to store items into the realm
                                    // Converts objects into proper RealmObjects managed by Realm.
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            item.setViewsCount(viewsCount);
                                            item.setCommentsCount(commentsCount);
                                        }
                                    });

                                    count++;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Popular data", e.getMessage());
                        } finally {
                            realm.close();
                            delegate.finishedLoadingData(addedCount, updatedCount, count);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // Log error message to help solve any problems
                        Log.e("Popular data", statusCode + " " + throwable.getMessage());
                    }
                });
    }

    public static void updateCommentsCount(final CommentsDelegate delegate) {
        if (delegate.getRealm() != null && delegate.getItem() != null) {
            new AsyncHttpClient().get(String.format("%s/wp-json/comments_count/%d", Config.getBaseUrl(), delegate.getItem().getID()),
                    new TextHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            final int commentsCount = Integer.getInteger(responseString, 0);

                            try {
                                if (delegate.getItem().getCommentsCount() < commentsCount) {
                                    delegate.getRealm().executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            delegate.getItem().setCommentsCount(commentsCount);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Log.e("Comments count", e.getMessage());
                            } finally {
                                delegate.finishedLoadingCommentsCount(commentsCount);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            // Log error message to help solve any problems
                            Log.e("Comments count", statusCode + " " + throwable.getMessage());
                        }
                    });
        }
    }

    public static Gson getGsonInstance() {
        // GSON can parse the data.
        // Note there is a bug in GSON 2.3.1 that can cause it to StackOverflow when working with RealmObjects.
        // To work around this, use the ExclusionStrategy below
        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    public static <T extends RealmObject & IndentifierModel> int getIdBySlug(Class<T> clazz, String slug) {
        slug = StringUtils.stripStart(StringUtils.stripEnd(slug, "/"), "/");

        Realm realm = Realm.getDefaultInstance();
        T item = realm.where(clazz).equalTo("slug", slug).findFirst();
        int id = 0;

        if (item != null) {
            id = item.getID();
        }

        realm.close();

        return id;
    }

    public static PostModel getPostByUrl(String url) {
        Realm realm = Realm.getDefaultInstance();

        PostModel item = realm.where(PostModel.class).equalTo("link",
                // Handle legacy permalinks
                url.replaceFirst("\\d{4}/\\d{2}/\\d{2}/", ""))
                .findFirst();

        realm.close();

        return item;
    }
}
