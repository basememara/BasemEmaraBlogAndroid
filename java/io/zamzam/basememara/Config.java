package io.zamzam.basememara;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

import io.zamzam.basememara.model.Category;
import io.zamzam.basememara.model.Social;
import io.zamzam.basememara.util.ResourceHelper;

/**
 * Created by basem on 7/29/15.
 */
public class Config {

    public static String getAppName() {
        return App.getStaticContext().getString(R.string.app_name);
    }

    public static String getSeedFileName() {
        return App.getStaticContext().getString(R.string.seed_file_name);
    }

    public static int getSeedFilesCount() {
        return ResourceHelper.getInteger(R.integer.seed_files_count);
    }

    public static String getBaseUrl() {
        return App.getStaticContext().getString(R.string.base_url);
    }

    public static String getNewsletterUrl() {
        return App.getStaticContext().getString(R.string.newsletter_url);
    }

    public static String getBloggerFirstName() {
        return App.getStaticContext().getString(R.string.blogger_first_name);
    }

    public static String getFeedbackEmail() {
        return App.getStaticContext().getString(R.string.feedback_email);
    }

    public static String getDesignedBy() {
        return App.getStaticContext().getString(R.string.designed_by_name);
    }

    public static String getDesignedByUrl() {
        return App.getStaticContext().getString(R.string.designed_by_url);
    }

    public static List<Category> getCategories() {
        List<Category> list = new ArrayList<>();

        for (TypedArray item : ResourceHelper.getMultiTypedArray("categories")) {
            Category model = new Category();
            model.ID = item.getInt(0, 0);
            model.title = item.getString(1);
            list.add(model);
        }

        return list;
    }

    public static List<Social> getSocial() {
        List<Social> list = new ArrayList<>();

        for (TypedArray item : ResourceHelper.getMultiTypedArray("social")) {
            Social model = new Social();
            model.ID = item.getResourceId(0, 0);
            model.name = item.getString(1);
            model.link = item.getString(2);
            model.app = item.getString(3);
            list.add(model);
        }

        return list;
    }

    public static SharedPreferences getPreferences() {
        Context context = App.getStaticContext();

        return context.getSharedPreferences(
                context.getPackageName() + ".main_preference",
                context.MODE_PRIVATE);
    }

    public static boolean isTutorialFinished() {
        return getPreferences().getBoolean("isTutorialFinished", false);
    }

    public static void isTutorialFinished(boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean("isTutorialFinished", value);
        editor.apply();
    }
}
