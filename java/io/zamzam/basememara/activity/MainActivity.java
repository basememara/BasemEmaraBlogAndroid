package io.zamzam.basememara.activity;


import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.base.Strings;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.R;
import io.zamzam.basememara.activity.base.BaseActivity;
import io.zamzam.basememara.adapter.PostPagerAdapter;
import io.zamzam.basememara.contract.StoreNewDataDelegate;
import io.zamzam.basememara.fragment.PostListFragment;
import io.zamzam.basememara.model.Category;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.model.SearchFilter;
import io.zamzam.basememara.model.TermModel;
import io.zamzam.basememara.util.DataHelper;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.LayoutHelper;
import io.zamzam.basememara.util.TrackHelper;


/**
 * Created by basem on 7/2/15.
 */
public class MainActivity extends BaseActivity implements StoreNewDataDelegate {

    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_SEARCH_QUERY = "search_query";
    public static final String EXTRA_SEARCH_FILTER = "search_filter";
    public static final String EXTRA_URL_REQUEST = "url_request";

    private PostPagerAdapter mPagerAdapter;
    String mSearchQuery = null;
    boolean mShowUnread = false;
    boolean mShowFavorites = false;
    boolean mShowPopular = false;
    boolean mSearchExpandedOnStart = false;

    @Bind(R.id.viewpager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID = R.layout.activity_main;
        menuResID = R.menu.menu_main;

        super.onCreate(savedInstanceState);

        // Fill database with seed data if needed
        DataHelper.seedDatabaseIfNeeded();

        // Initialize areas
        setupViewPager();
        setupDataSource();
        setTutorial();

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Google Analytics
        TrackHelper.screenView("Home - Posts");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            handleUrl(data);
        } else if (Intent.ACTION_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = intent.getBundleExtra(SearchManager.APP_DATA);

            if (bundle != null) {
                searchFilter = (SearchFilter) bundle.get(EXTRA_SEARCH_FILTER);
            }

            setSearchQuery(query);
            mSearchExpandedOnStart = true;
        } else if (intent.hasExtra(EXTRA_CATEGORY_ID)) {
            int id = intent.getIntExtra(EXTRA_CATEGORY_ID, 0);
            if (!setCategory(id)) {
                IntentHelper.openWebScreen(this, String.format("%s/?cat=%d", Config.getBaseUrl(), id), true);
            }
        } else if (intent.hasExtra(EXTRA_SEARCH_QUERY)) {
            setSearchQuery(intent.getStringExtra(EXTRA_SEARCH_QUERY));
        } else if (intent.hasExtra(EXTRA_URL_REQUEST)) {
            handleUrl(intent.getStringExtra(EXTRA_URL_REQUEST));
        }
    }

    public void handleUrl(String url) {
        Uri uri = Uri.parse(url);
        boolean success = false;

        // Handle home page
        if (uri.getPath().equals("/") && Strings.isNullOrEmpty(uri.getQuery())) {
            success = true;
        } else if (uri.getPath().equals("/") && !Strings.isNullOrEmpty(uri.getQueryParameter("s"))) { // Handle search
            setSearchQuery(uri.getQueryParameter("s"));
            mSearchExpandedOnStart = true;
            success = true;
        } else if (uri.getPathSegments().size() == 2) {
            String query = uri.getPathSegments().get(1).toLowerCase();

            // Handle path prefix based URL
            switch (uri.getPathSegments().get(0).toLowerCase()) {
                case "category": {
                    int id = DataHelper.getIdBySlug(TermModel.class, query);
                    if (id > 0 && setCategory(id)) {
                        success = true;
                    }
                    break;
                }
            }
        } else if (DataHelper.getIdBySlug(PostModel.class, uri.getPath()) > 0) { // Handle post
            IntentHelper.openPostDetail(this,
                    DataHelper.getIdBySlug(PostModel.class, uri.getPath()), true);
            success = true;
        }

        // Fallback to internal browser if applicable
        if (!success) {
            IntentHelper.openWebScreen(this, url, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }

        switch (menuItem.getItemId()) {
            case R.id.action_all:
                setFilter(false, false, false);
                return true;
            case R.id.action_unread:
                setFilter(true, false, false);
                return true;
            case R.id.action_favorites:
                setFilter(false, true, false);

                // Google Analytics
                TrackHelper.screenView("Favorites");
                return true;
            case R.id.action_popular:
                setFilter(false, false, true);

                // Google Analytics
                TrackHelper.screenView("Popular posts");
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @OnClick(R.id.main_scroll_top)
    public void onScrollTopFloatingButtonClick() {
        LayoutHelper.ExpandToolbar(appBarLayout, coordinatorLayout);
        scrollTop();
    }

    private void setupViewPager() {
        mPagerAdapter = new PostPagerAdapter(getSupportFragmentManager());

        for (Category item : categories) {
            mPagerAdapter.addFragment(PostListFragment.newInstance(item.ID), item.title);
        }

        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupDataSource() {
        DataHelper.storeNewOrUpdatedData(this, PostModel.class, PostModel[].class,
                Config.getBaseUrl() + "/wp-json/posts?filter[posts_per_page]=50&filter[orderby]=modified&filter[order]=desc&page=1",
                Config.getBaseUrl() + "/wp-json/popular_count");
    }

    @Override
    protected void setupSearch(Menu menu) {
        super.setupSearch(menu);

        if (mSearchExpandedOnStart) {
            MenuItemCompat.expandActionView(searchMenuItem);
            setSearchFilter(searchFilter);
            searchView.setQuery(mSearchQuery, false);
            searchView.clearFocus();
            mSearchExpandedOnStart = false;
        }
    }

    private void setTutorial() {
        if (!Config.isTutorialFinished()) {
            IntentHelper.openTutorial(this);
        }
    }

    @Override
    public void setSearchQuery(String searchQuery) {
        mSearchQuery = searchQuery;

        // Update search query parameter for all fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            PostListFragment frag = (PostListFragment) mPagerAdapter.getItem(i);
            if (frag != null) {
                frag.setSearchQuery(mSearchQuery, searchFilter);
                frag.loadData();
            }
        }

        // Google Analytics
        if (!Strings.isNullOrEmpty(mSearchQuery)) {
            TrackHelper.event("Search", "Post", mSearchQuery);
        }

        scrollTop();
    }

    public boolean setCategory(int id) {
        int counter = 0;

        // Find category
        for (Category item : categories) {
            if (item.ID == id) {
                viewPager.setCurrentItem(counter);

                // Google Analytics
                if (id > 0) {
                    TrackHelper.event("Category", "Post", item.title, item.ID);
                }

                return true;
            }
            counter++;
        }

        return false;
    }

    public void setFilter(boolean showUnread, boolean showFavorites, boolean showPopular) {
        mShowFavorites = showFavorites;
        mShowUnread = showUnread;
        mShowPopular = showPopular;

        Realm realm = Realm.getDefaultInstance();
        setTitle(mShowFavorites ? String.format("Favorites (%d)", realm.where(PostModel.class).equalTo("favorite", true).count())
                : mShowUnread ? String.format("Unread (%d)", realm.where(PostModel.class).equalTo("read", false).count())
                : mShowPopular ? "Popular"
                : Config.getAppName());
        realm.close();

        mainMenu.findItem(R.id.action_more).setIcon(
                mShowFavorites || mShowUnread || mShowPopular
                        ? R.drawable.ic_more_vert_black_24dp
                        : R.drawable.ic_more_vert_white_24dp);

        // Update search query parameter for all fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            PostListFragment frag = (PostListFragment) mPagerAdapter.getItem(i);
            if (frag != null) {
                frag.setShowUnread(mShowUnread);
                frag.setShowFavorites(mShowFavorites);
                frag.setShowPopular(mShowPopular);
                frag.loadData();
            }
        }

        scrollTop();
    }

    @Override
    public void finishedLoadingData(int addedCount, int updatedCount, int popularCount) {
        if (addedCount > 0 || updatedCount > 0 || popularCount > 0) {
            // Update data for all fragments
            for (int i = 0; i < mPagerAdapter.getCount(); i++) {
                PostListFragment frag = (PostListFragment) mPagerAdapter.getItem(i);
                if (frag != null) {
                    frag.loadData(true);
                }
            }

            if (addedCount > 0) {
                Toast.makeText(this,
                        addedCount + " " + getResources().getText(R.string.new_posts),
                        Toast.LENGTH_LONG).show();
            }

            if (updatedCount > 0) {
                Toast.makeText(this,
                        updatedCount + " " + getResources().getText(R.string.updated_posts),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void scrollTop() {
        // Scroll top to active fragment
        PostListFragment frag = (PostListFragment) mPagerAdapter.getItem(viewPager.getCurrentItem());
        if (frag != null) {
            frag.scrollTop();
        }
    }
}