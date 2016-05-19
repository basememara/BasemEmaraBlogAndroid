package io.zamzam.basememara.activity.base;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Strings;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.R;
import io.zamzam.basememara.model.Category;
import io.zamzam.basememara.model.SearchFilter;
import io.zamzam.basememara.model.Social;
import io.zamzam.basememara.util.ConvertHelper;
import io.zamzam.basememara.util.IntentHelper;
import io.zamzam.basememara.util.TrackHelper;
import io.zamzam.basememara.util.WebHelper;

/**
 * Created by basem on 8/1/15.
 */
public class BaseActivity extends AppCompatActivity {

    protected int layoutResID;
    protected int menuResID;
    protected SearchFilter searchFilter = SearchFilter.ANY;
    protected SearchView searchView;
    protected MenuItem searchMenuItem;
    protected Menu mainMenu;
    protected ActionBar actionBar;
    protected List<Category> categories = Config.getCategories();
    protected List<Social> socialNetworks = Config.getSocial();

    @Bind(R.id.main_content) protected CoordinatorLayout coordinatorLayout;
    @Bind(R.id.appbar) protected AppBarLayout appBarLayout;
    @Bind(R.id.drawer_layout) protected DrawerLayout drawerLayout;
    @Bind(R.id.nav_view) protected NavigationView navigationView;
    @Bind(R.id.tabs) protected TabLayout tabLayout;
    @Bind(R.id.toolbar) protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);

        // Bind views to fields
        ButterKnife.bind(this);

        // Initialize areas
        setupCategories();
        setupSocial();
        setupToolbar();
        setupDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuResID, menu);

        mainMenu = menu;
        setupSearch(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Update interface since it doesn't automatically
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }

        switch (menuItem.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_filter_any:
                setSearchFilter(SearchFilter.ANY);
                return true;
            case R.id.action_filter_title:
                setSearchFilter(SearchFilter.TITLE);
                return true;
            case R.id.action_filter_content:
                setSearchFilter(SearchFilter.CONTENT);
                return true;
            case R.id.action_filter_keywords:
                setSearchFilter(SearchFilter.KEYWORDS);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @OnClick({R.id.facebook_icon, R.id.twitter_icon, R.id.pinterest_icon,
            R.id.googleplus_icon, R.id.instagram_icon, R.id.youtube_icon,
            R.id.linkedin_icon, R.id.github_icon})
    public void onSocialClick(ImageView sender) {
        for (Social item : socialNetworks) {
            if (item.ID == sender.getId()) {
                IntentHelper.openExternalApp(BaseActivity.this, item.app, item.link);

                // Google Analytics
                TrackHelper.event("Social", item.name);
                break;
            }
        }
    }

    protected void setupToolbar() {
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.ic_drawer_nostretch));
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void setupCategories() {
        // Add "all" category first
        Category category = new Category();
        category.ID = 0;
        category.title = "All";
        categories.add(0, category);
    }

    protected void setupSocial() {
        int count = 0;

        // Retrieve social networks from XML config and configure icons
        for (Social item : socialNetworks) {
            if (Strings.isNullOrEmpty(item.link)) {
                this.findViewById(item.ID).setVisibility(View.GONE);
                count++;
            }
        }

        // Calculate max size of icons
        int width = ConvertHelper.convertDpToPixel(this, 200) / (socialNetworks.size() - count);
        for (Social item : socialNetworks) {
            this.findViewById(item.ID).getLayoutParams().width = width;
        }
    }

    protected void setupDrawer() {
        final Context context = this;

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home: {
                                IntentHelper.openHomeScreen(context);
                                return true;
                            }
                            case R.id.nav_about: {
                                IntentHelper.openWebScreen(context, Config.getBaseUrl() + "/about/");
                                return true;
                            }
                            case R.id.nav_workwithme: {
                                IntentHelper.openWebScreen(context, Config.getBaseUrl() + "/resume/");
                                return true;
                            }
                            case R.id.nav_disclaimer: {
                                IntentHelper.openWebScreen(context, Config.getBaseUrl() + "/disclaimer/");
                                return true;
                            }
                            case R.id.nav_subscribe: {
                                WebHelper.openUrl(context, Config.getNewsletterUrl(), true, false);
                                return true;
                            }
                            case R.id.nav_read_tutorial: {
                                IntentHelper.openTutorial(context);

                                // Google Analytics
                                TrackHelper.event("Tutorial", "Manual");
                                return true;
                            }
                            case R.id.nav_feedback: {
                                WebHelper.email(context, "Feedback: " + Config.getAppName());
                                return true;
                            }
                            case R.id.nav_rate: {
                                WebHelper.openBrowser(context, "https://play.google.com/store/apps/details?id="
                                        + context.getPackageName());
                                return true;
                            }
                            case R.id.nav_tell_friend: {
                                WebHelper.email(context, Config.getAppName() + " is awesome! Check out the app!",
                                        "I found this awesome app that may interest you!"
                                                + "https://play.google.com/store/apps/details?id="
                                                + context.getPackageName());

                                // Google Analytics
                                TrackHelper.event("Share", "App");
                                return true;
                            }
                            case R.id.nav_designed_by: {
                                WebHelper.openBrowser(context, Config.getDesignedByUrl());

                                // Google Analytics
                                TrackHelper.screenView("Designed by");
                                return true;
                            }
                        }

                        return true;
                    }
                });
    }

    protected void setupSearch(Menu menu) {
        final MenuItem filterMenuItem = menu.findItem(R.id.action_filter);
        final MenuItem moreMenuItem = menu.findItem(R.id.action_more);

        // Associate searchable configuration with the SearchView
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Search collapsed
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Clear search
                searchFilter = SearchFilter.ANY;
                setSearchQuery(null);
                filterMenuItem.setVisible(false);
                moreMenuItem.setVisible(true);
                actionBar.setBackgroundDrawable(ContextCompat.getDrawable(BaseActivity.this, R.drawable.ic_drawer_nostretch));
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                filterMenuItem.setVisible(true);
                moreMenuItem.setVisible(false);
                actionBar.setBackgroundDrawable(null);
                return true;
            }
        });
    }

    public void setSearchQuery(String searchQuery) {

    }

    public void setSearchFilter(SearchFilter value) {
        searchFilter = value;

        mainMenu.findItem(R.id.action_filter).setIcon(
                searchFilter == SearchFilter.ANY
                        ? R.drawable.ic_filter_list_white_24dp
                        : R.drawable.ic_filter_list_black_24dp);
    }
}