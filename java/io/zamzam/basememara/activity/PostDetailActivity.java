package io.zamzam.basememara.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.R;
import io.zamzam.basememara.contract.CommentsDelegate;
import io.zamzam.basememara.contract.WebViewDelegate;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.model.TermModel;
import io.zamzam.basememara.util.DataHelper;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.LayoutHelper;
import io.zamzam.basememara.util.MediaHelper;
import io.zamzam.basememara.util.ResourceHelper;
import io.zamzam.basememara.util.TrackHelper;
import io.zamzam.basememara.util.WebHelper;

/**
 * Created by basem on 6/30/15.
 */
public class PostDetailActivity extends AppCompatActivity implements WebViewDelegate, CommentsDelegate {

    public static final String EXTRA_ITEM_ID = "item_id";

    private Realm realm = null;
    private PostModel mItem = null;
    private Menu mMenu;
    private MenuItem mFavoriteMenuItem;
    private MenuItem mCommentsMenuItem;
    private AlertDialog mRelatedItemsDialog;
    private GoogleApiClient mClient;
    private Action mViewAction;

    @Bind(R.id.main_content) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.appbar) AppBarLayout appBarLayout;
    @Bind(R.id.scroll_view) NestedScrollView nestedScrollView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.post_web_view) WebView contentWebView;
    @Bind(R.id.backdrop) ImageView mainImageView;
    @Bind(R.id.floating_favorite) FloatingActionButton favoriteFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        realm = Realm.getDefaultInstance();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // Bind views to fields
        ButterKnife.bind(this);

        setupToolbar();
        setupWebView();

        handleIntent(getIntent());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Connect Google app indexing client
        mClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        mMenu = menu;
        mFavoriteMenuItem = menu.findItem(R.id.action_favorite);
        mCommentsMenuItem = menu.findItem(R.id.action_comment);

        updateFavoriteIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share: {
                IntentHelper.share(this,
                        mItem.getTitle(),
                        mItem.getTitle() + ": " + mItem.getLink());

                // Google Analytics
                TrackHelper.event("Share", "Post", mItem.getTitle(), mItem.getID());
                return true;
            }
            case R.id.action_browser: {
                WebHelper.openBrowser(this, mItem.getLink());
                return true;
            }
            case R.id.action_favorite: {
                toggleFavorite();

                // Google Analytics
                if (mItem.isFavorite()) {
                    TrackHelper.event("Favorite", "Post", mItem.getTitle(), mItem.getID());
                }
                return true;
            }
            case R.id.action_related: {
                mRelatedItemsDialog = WebHelper.openUrl(this,
                        Config.getBaseUrl()
                                + "/mobile-related/?theme=light&postid="
                                + mItem.getID(),
                        getString(R.string.related_items),
                        PostDetailActivity.this,
                        false,
                        false);

                // Google Analytics
                TrackHelper.event("Related", "Post", mItem.getTitle(), mItem.getID());
                return true;
            }
            case R.id.action_comment: {
                WebHelper.openUrl(this,
                        Config.getBaseUrl()
                                + "/mobile-comments/?postid="
                                + mItem.getID(),
                        getString(R.string.comments),
                        false,
                        false);

                // Google Analytics
                TrackHelper.screenView("Comment detail - " + mItem.getTitle());
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Load blog posts into activity instead of web view if applicable
        if (Uri.parse(url).getHost().equalsIgnoreCase(Uri.parse(Config.getBaseUrl()).getHost())) {
            // Load item into activity if found
            if (!loadData(url, true)) {
                // Let it open item within the app still
                IntentHelper.openDeepLink(this, url);
            } else if (mRelatedItemsDialog != null) {
                // Click came from related items popup so reset
                // to show data behind it
                mRelatedItemsDialog.dismiss();
                mRelatedItemsDialog = null;
            }
        } else {
            // Load all other URL's into web view popup
            WebHelper.openUrl(this, url);
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_ITEM_ID)) {
            loadData(intent.getIntExtra(EXTRA_ITEM_ID, 0));
        }
    }

    @OnClick(R.id.floating_favorite)
    public void onFavoriteFloatingButtonClick() {
        toggleFavorite();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupWebView() {
        contentWebView.getSettings().setJavaScriptEnabled(true);
        contentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return PostDetailActivity.this.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    public boolean loadData(int id) {
        return loadData(id, false);
    }

    public boolean loadData(int id, boolean performScrollTop) {
        PostModel item = id > 0 ? realm.where(PostModel.class).equalTo("ID", id).findFirst() : null;
        return loadData(item, performScrollTop);
    }

    public boolean loadData(String url) {
        return loadData(url, false);
    }

    public boolean loadData(String url, boolean performScrollTop) {
        return loadData(DataHelper.getPostByUrl(url), performScrollTop);
    }

    public boolean loadDataBySlug(String slug) {
        return loadDataBySlug(slug, false);
    }

    public boolean loadDataBySlug(String slug, boolean performScrollTop) {
        slug = StringUtils.stripStart(StringUtils.stripEnd(slug, "/"), "/");
        PostModel item = realm.where(PostModel.class).equalTo("slug", slug, false).findFirst();
        return loadData(item, performScrollTop);
    }

    public boolean loadData(PostModel item) {
        return loadData(item, false);
    }

    public boolean loadData(PostModel item, boolean performScrollTop) {
        // Stop previous app index if any
        stopAppIndexing();

        if (item != null) {
            // Store item to memory for later use if needed
            mItem = item;

            MediaHelper.displayImage(mItem, mainImageView, true);

            contentWebView.loadDataWithBaseURL(Config.getBaseUrl(),
                    GetContentHtml(), "text/html", "utf-8", null);

            // Mark as read
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mItem.setRead(true);
                }
            });

            updateFavoriteIcon();

            // Update comments interface while retrieving updates in the background
            updateCommentsIcon();
            DataHelper.updateCommentsCount(this);

            // Call the Google App Indexing start after view has completely rendered
            startAppIndexing();

            // Google Analytics
            TrackHelper.screenView("Post detail - " + mItem.getTitle());

            if (performScrollTop) {
                scrollTop();
            }

            // Loaded successfully
            return true;
        }

        // Failed to load item
        return false;
    }

    public String GetContentHtml() {
        String html = "";

        if (mItem != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy");

            // Add styles
            html += "<style type='text/css'>";
            html += ResourceHelper.getAssetContent("bootstrap.min.css");
            html += ResourceHelper.getAssetContent("detail.css");
            html += ResourceHelper.getAssetContent("custom.css");
            html += "</style>";

            // Add terms
            String terms = "";
            if (mItem.getTerms() != null) {
                if (mItem.getTerms().getCategory() != null && mItem.getTerms().getCategory().size() > 0) {
                    String output = "";

                    for (TermModel item : mItem.getTerms().getCategory()) {
                        if (output.length() > 0) {
                            output += ", ";
                        }
                        output += String.format("<a href='%s/category/%s'>%s</a>",
                                Config.getBaseUrl(), item.getSlug(), item.getName());
                    }

                    if (output.length() > 0) {
                        terms += String.format("<li><strong>Filed Under:</strong> <em>%s</em></li>", output);
                    }
                }

                if (mItem.getTerms().getPost_tag() != null && mItem.getTerms().getPost_tag().size() > 0) {
                    String output = "";

                    for (TermModel item : mItem.getTerms().getPost_tag()) {
                        if (output.length() > 0) {
                            output += ", ";
                        }
                        output += item.getName();
                    }

                    if (output.length() > 0) {
                        terms += String.format("<li><strong>Tagged With:</strong> <em>%s</em></li>", output);
                    }
                }

                if (terms.length() > 0) {
                    terms = String.format("<div class='panel panel-default'><div class='panel-body'><ul>%s</ul></div></div>", terms);
                }
            }

            // Add detail header and content
            html += String.format("<h1 class='top-title'>%s</h1>"
                    + "<small class='top-meta'>By %s"
                    + " on %s</small>"
                    + "<div style='clear: both;'></div>"
                    + "<div class='wrapper'>%s%s",
                    mItem.getTitle(),
                    mItem.getAuthor().getName(),
                    dateFormatter.format(mItem.getDate()),
                    mItem.getContent(),
                    terms);

            // Add author bio if applicable
            if (mItem.getAuthor().getDescription() != null && mItem.getAuthor().getDescription().length() > 0) {
                html += String.format("<div class='author-bio well well-sm'>"
                        + "<div class='page-header'><h3>About %s</h3></div>"
                        + "<p><img src='%s' class='profile-pic' />%s</p></div>",
                        mItem.getAuthor().getName(),
                        mItem.getAuthor().getAvatar(),
                        mItem.getAuthor().getDescription());
            }

            // Add footer help
            html += "<div class='alert alert-warning' role='alert'><strong><em>" +
                    "Use the toolbar at the top to view related content, post a comment, favorite, or share." +
                    "<em><strong></div><br />";
            html += "</div>";
        }

        return html;
    }

    public void toggleFavorite() {
        if (mItem != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mItem.setFavorite(!mItem.isFavorite());
                }
            });

            updateFavoriteIcon();
        }
    }

    private void updateFavoriteIcon() {
        if (mItem != null) {
            int icon = mItem.isFavorite()
                    ? R.drawable.ic_favorite_white_24dp
                    : R.drawable.ic_favorite_border_white_24dp;

            favoriteFloatingButton.setImageResource(icon);

            if (mFavoriteMenuItem != null) {
                mFavoriteMenuItem.setIcon(icon);
            }
        }
    }

    private void updateCommentsIcon() {
        if (mItem != null) {
            int icon = mItem.getCommentsCount() > 0
                    ? R.drawable.ic_chat_bubble_white_24dp
                    : R.drawable.ic_chat_bubble_outline_white_24dp;

            if (mCommentsMenuItem != null) {
                mCommentsMenuItem.setIcon(icon);
            }
        }
    }

    @Override
    public void finishedLoadingCommentsCount(int commentsCount) {
        updateCommentsIcon();
    }

    public void startAppIndexing() {
        if (mItem != null) {
            Uri appUrl = Uri.parse(String.format("android-app://%s/http/%s/%s", this.getPackageName(), Config.getBaseUrl(), mItem.getSlug()));
            Uri webUrl = Uri.parse(String.format("%s/%s", Config.getBaseUrl(), mItem.getSlug()));

            // Construct the Action performed by the user
            mViewAction = Action.newAction(Action.TYPE_VIEW, mItem.getTitle(), webUrl, appUrl);

            // Send to Google app indexing
            AppIndex.AppIndexApi.start(mClient, mViewAction);
        }
    }

    public void stopAppIndexing() {
        if (mViewAction != null) {
            AppIndex.AppIndexApi.end(mClient, mViewAction);
            mViewAction = null;
        }
    }

    private void scrollTop() {
        nestedScrollView.fullScroll(ScrollView.FOCUS_UP);
        LayoutHelper.ExpandToolbar(appBarLayout, coordinatorLayout);
    }

    public Realm getRealm() {
        return realm;
    }

    public PostModel getItem() {
        return mItem;
    }

    @Override
    public void onStop() {
        // Stop and disconnect app index
        stopAppIndexing();
        mClient.disconnect();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.close();
            realm = null;
        }
    }
}