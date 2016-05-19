package io.zamzam.basememara.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.zamzam.basememara.R;
import io.zamzam.basememara.adapter.PostCardAdapter;
import io.zamzam.basememara.model.PostModel;
import io.zamzam.basememara.model.SearchFilter;
import io.zamzam.basememara.widget.RecyclerViewExtended;

/**
 * Created by basem on 7/2/15.
 */
public class PostListFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "categoryID";

    private PostCardAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mCategoryID = 0;
    private String mSearchQuery = null;
    private SearchFilter mSearchFilter = SearchFilter.ANY;
    private boolean mShowUnread = false;
    private boolean mShowFavorites = false;
    private boolean mShowPopular = false;
    private Realm realm;

    @Bind(R.id.recyclerview) RecyclerViewExtended recyclerView;

    public PostListFragment() {}

    public static PostListFragment newInstance(int categoryID) {
        PostListFragment frag = new PostListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryID);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Bind views to fields
        ButterKnife.bind(this, view);

        // Get bundle data if applicable
        final Bundle bundle = getArguments();
        if (bundle != null) {
            setCategoryID(bundle.getInt("categoryID"));
        }

        // Configure recycler view
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_text_view));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load data or update interface only
        if (mAdapter == null) {
            // Bind adapter
            mAdapter = new PostCardAdapter(getActivity());
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
    }

    public void loadData() {
        loadData(false);
    }

    public void loadData(boolean invalidateRecyclerView) {
        if (realm != null && mAdapter != null) {
            RealmQuery dbQuery = realm.where(PostModel.class);
            dbQuery.notEqualTo("terms.category.name", "Uncategorized");

            if (mCategoryID > 0) {
                dbQuery.beginGroup();
                dbQuery.equalTo("terms.category.ID", mCategoryID);
                dbQuery.or().equalTo("terms.category.parent.ID", mCategoryID);
                dbQuery.endGroup();
            }

            if (mShowUnread) {
                dbQuery.equalTo("read", !mShowUnread);
            }

            if (mShowFavorites) {
                dbQuery.equalTo("favorite", mShowFavorites);
            }

            if (mShowPopular) {
                dbQuery.equalTo("terms.category.ID", 64);
            }

            if (!Strings.isNullOrEmpty(mSearchQuery)) {
                switch (mSearchFilter) {
                    case ANY:
                        dbQuery.beginGroup();
                        dbQuery.contains("title", mSearchQuery, false);
                        dbQuery.or().contains("content", mSearchQuery, false);
                        dbQuery.or().equalTo("terms.category.name", mSearchQuery, false);
                        dbQuery.or().equalTo("terms.post_tag.name", mSearchQuery, false);
                        dbQuery.endGroup();
                        break;
                    case TITLE:
                        dbQuery.contains("title", mSearchQuery, false);
                        break;
                    case CONTENT:
                        dbQuery.contains("content", mSearchQuery, false);
                        break;
                    case KEYWORDS:
                        dbQuery.beginGroup();
                        dbQuery.equalTo("terms.category.name", mSearchQuery, false);
                        dbQuery.or().equalTo("terms.post_tag.name", mSearchQuery, false);
                        dbQuery.endGroup();
                        break;
                }
            }

            RealmResults<PostModel> result = dbQuery.findAll();
            result.sort("date", RealmResults.SORT_ORDER_DESCENDING);
            mAdapter.setData(result);

            // Refresh recycler if applicable
            if (invalidateRecyclerView) {
                recyclerView.invalidate();
            }
        }
    }

    private void setupDataSource() {
        loadData();
    }

    public void setCategoryID(int value) {
        mCategoryID = value;
    }

    public void setSearchQuery(String value) {
        setSearchQuery(value, SearchFilter.ANY);
    }

    public void setSearchQuery(String value, SearchFilter filter) {
        mSearchQuery = value;
        mSearchFilter = filter;
    }

    public void setShowUnread(boolean showUnread) {
        this.mShowUnread = showUnread;
    }

    public void setShowFavorites(boolean showFavorites) {
        this.mShowFavorites = showFavorites;
    }

    public void setShowPopular(boolean showPopular) {
        this.mShowPopular = showPopular;
    }

    public void scrollTop() {
        if (mLayoutManager != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Mark all as read
            case 0:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<PostModel> results = realm.where(PostModel.class).findAll();
                        for (int i = 0; i < results.size(); i++) {
                            results.get(i).setRead(true);
                        }
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
