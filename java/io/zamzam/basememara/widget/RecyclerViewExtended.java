package io.zamzam.basememara.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by basem on 7/5/15.
 * No empty handling built into recycler view
 * http://stackoverflow.com/questions/28217436/how-to-show-an-empty-view-with-a-recyclerview
 */
public class RecyclerViewExtended extends RecyclerView {
    private View emptyView;

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter =  getAdapter();
            if(adapter != null && emptyView != null) {
                if(adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    RecyclerViewExtended.this.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    RecyclerViewExtended.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    public RecyclerViewExtended(Context context) {
        super(context);
    }

    public RecyclerViewExtended(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewExtended(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
