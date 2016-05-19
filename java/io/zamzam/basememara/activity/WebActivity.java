package io.zamzam.basememara.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.base.Strings;

import butterknife.Bind;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.R;
import io.zamzam.basememara.activity.base.BaseActivity;
import io.zamzam.basememara.model.Category;
import io.zamzam.basememara.util.AlertHelper;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.TrackHelper;
import io.zamzam.basememara.util.WebHelper;


/**
 * Created by basem on 7/2/15.
 */
public class WebActivity extends BaseActivity {

    public static final String EXTRA_URL_PATH = "url_path";

    private ProgressDialog spinnerDialog;
    private String mUrl;

    @Bind(R.id.web_view) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID = R.layout.activity_web;
        menuResID = R.menu.menu_web;
        spinnerDialog = AlertHelper.createSpinnerDialog(this);

        super.onCreate(savedInstanceState);

        // Initialize areas
        setupWebView();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            mUrl = data;
            webView.loadUrl(getUrl());

            // Google Analytics
            TrackHelper.screenView("Web page - " + getUrl());
        } else if (intent.hasExtra(EXTRA_URL_PATH)) {
            mUrl = intent.getStringExtra(EXTRA_URL_PATH);
            webView.loadUrl(getUrl());

            // Google Analytics
            TrackHelper.screenView("Web page - " + getUrl());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }

        switch (menuItem.getItemId()) {
            case R.id.action_browser:
                WebHelper.openBrowser(this, mUrl);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void setupCategories() {
        super.setupCategories();

        for (Category item : categories) {
            tabLayout.addTab(tabLayout.newTab()
                    .setText(item.title)
                    .setTag(item.ID));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                IntentHelper.openHomeCategory(WebActivity.this, (int) tab.getTag());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void setupSearch(Menu menu) {
        super.setupSearch(menu);

        // Add extra search context if applicable
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(MainActivity.EXTRA_SEARCH_FILTER, searchFilter);
                searchView.setAppSearchData(bundle);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Load internal links through intents
                if (Uri.parse(url).getHost().equalsIgnoreCase(Uri.parse(Config.getBaseUrl()).getHost())) {
                    IntentHelper.openDeepLink(WebActivity.this, url);
                } else {
                    // Load all other URL's into web view popup
                    WebHelper.openUrl(WebActivity.this, url);
                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                spinnerDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                spinnerDialog.dismiss();
            }
        });

        if (!Strings.isNullOrEmpty(mUrl)) {
            webView.loadUrl(getUrl());

            // Google Analytics
            TrackHelper.screenView("Web page - " + getUrl());
        }
    }

    public String getUrl() {
        return !Strings.isNullOrEmpty(mUrl) ?
                Uri.parse(mUrl)
                    .buildUpon()
                    .appendQueryParameter("mobileembed", "1")
                    .build()
                    .toString() : mUrl;
    }
}