package io.zamzam.basememara.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.common.base.Strings;

import io.zamzam.basememara.R;
import io.zamzam.basememara.activity.MainActivity;
import io.zamzam.basememara.activity.PostDetailActivity;
import io.zamzam.basememara.activity.TutorialActivity;
import io.zamzam.basememara.activity.WebActivity;

/**
 * Created by basem on 7/4/15.
 */
public class IntentHelper {

    public static void share(Context context, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent,
                    context.getResources().getText(R.string.share)));
        }
    }

    public static void openExternalApp(Context context, String appLink) {
        openExternalApp(context, appLink, null);
    }

    public static void openExternalApp(Context context, String appLink, String urlLink) {
        if (!Strings.isNullOrEmpty(appLink)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appLink));

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return;
            }
        }

        if (!Strings.isNullOrEmpty(urlLink)) {
            WebHelper.openBrowser(context, urlLink);
        }
    }

    public static void openPostDetail(Activity context, int id, boolean clearTop) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_ITEM_ID, id);

        if (clearTop) {
            context.finish();
            context.overridePendingTransition(0, 0);
        }

        context.startActivity(intent);
    }

    public static void openPostDetail(Activity context, int id) {
        openPostDetail(context, id, false);
    }

    public static void openPostDetail(Context context, int id) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_ITEM_ID, id);
        context.startActivity(intent);
    }

    public static void openHomeScreen(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openHomeCategory(Context context, int id) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_CATEGORY_ID, id);
        context.startActivity(intent);
    }

    public static void openHomeSearch(Context context, String query) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_SEARCH_QUERY, query);
        context.startActivity(intent);
    }

    public static void openDeepLink(Context context, String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_URL_REQUEST, url);
        context.startActivity(intent);
    }

    public static void openTutorial(Context context) {
        Intent intent = new Intent(context, TutorialActivity.class);
        context.startActivity(intent);
    }

    public static void openWebScreen(Activity context, String url, boolean clearTop) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_URL_PATH, url);

        if (clearTop) {
            context.finish();
            context.overridePendingTransition(0, 0);
        }

        context.startActivity(intent);
    }

    public static void openWebScreen(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_URL_PATH, url);
        context.startActivity(intent);
    }

}
