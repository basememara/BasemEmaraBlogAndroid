package io.zamzam.basememara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.base.Strings;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.zamzam.basememara.Config;
import io.zamzam.basememara.R;
import io.zamzam.basememara.adapter.NotificationAdapter;
import io.zamzam.basememara.contract.StoreNewDataDelegate;
import io.zamzam.basememara.model.NotificationModel;
import io.zamzam.basememara.util.DataHelper;
import io.zamzam.basememara.util.TrackHelper;
import io.zamzam.basememara.widget.RecyclerViewExtended;

/**
 * Created by basem on 7/6/15.
 */
public class NotificationActivity extends AppCompatActivity
    implements StoreNewDataDelegate {

    private NotificationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MenuItem mMoreItem;
    private Realm realm;

    @Bind(R.id.main_content) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.appbar) AppBarLayout appBarLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerview) RecyclerViewExtended recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Bind views to fields
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        setTitle(getString(R.string.notifications));
        setupToolbar();
        setupRecyclerView();

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);

        mMoreItem = menu.findItem(R.id.action_more);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }

        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_all:
                loadData(true);
                return true;
            case R.id.action_newsletters:
                loadData("newsletter", true);

                // Google Analytics
                TrackHelper.screenView("Newsletters");
                return true;
            case R.id.action_messages:
                loadData("message", true);

                // Google Analytics
                TrackHelper.screenView("action_messages");
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load data or update interface only
        if (mAdapter == null) {
            // Bind adapter
            mAdapter = new NotificationAdapter(this);
            recyclerView.setAdapter(mAdapter);

            // Populate data
            setupDataSource();
        } else {
            // Reattach adapter if needed
            if (recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(mAdapter);
            }

            // Update interface in case data change on detail activity
            mAdapter.notifyDataSetChanged();
        }

        // Google Analytics
        TrackHelper.screenView("Notifications");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(findViewById(R.id.empty_text_view));
    }

    private void setupDataSource() {
        loadData();

        DataHelper.storeNewOrUpdatedData(this, NotificationModel.class, NotificationModel[].class,
                Config.getBaseUrl() + "/wp-json/posts?type=z-notification&filter[posts_per_page]=50&filter[orderby]=modified&filter[order]=desc&page=1");
    }

    public void loadData() {
        loadData(false);
    }

    public void loadData(boolean invalidateRecyclerView) {
        loadData(null, invalidateRecyclerView);
    }

    public void loadData(String type, boolean invalidateRecyclerView) {
        if (realm != null) {
            RealmQuery dbQuery = realm.where(NotificationModel.class);

            if (!Strings.isNullOrEmpty(type)) {
                dbQuery.equalTo("post_meta.type", type);
            }

            RealmResults<NotificationModel> result = dbQuery.findAll();
            result.sort("date", RealmResults.SORT_ORDER_DESCENDING);
            mAdapter.setData(result);

            // Refresh recycler if applicable
            if (invalidateRecyclerView) {
                recyclerView.invalidate();
            }
        }

        if (mMoreItem != null) {
            mMoreItem.setIcon(Strings.isNullOrEmpty(type)
                    ? R.drawable.ic_more_vert_white_24dp
                    : R.drawable.ic_more_vert_black_24dp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    @Override
    public void finishedLoadingData(int addedCount, int updatedCount, int popularCount) {
        if (addedCount > 0 || updatedCount > 0 || popularCount > 0) {
            loadData(true);

            if (addedCount > 0) {
                Toast.makeText(this, addedCount + " "
                        + getResources().getText(R.string.new_messages),
                        Toast.LENGTH_LONG).show();
            }

            if (updatedCount > 0) {
                Toast.makeText(this, updatedCount + " "
                                + getResources().getText(R.string.new_messages),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}