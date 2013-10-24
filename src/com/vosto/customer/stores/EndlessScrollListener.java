package com.vosto.customer.stores;

import android.util.Log;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.activities.StoresActivity;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/09/24
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class EndlessScrollListener implements OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    VostoBaseActivity context;
    StoresActivity storesActivity;

    public EndlessScrollListener(VostoBaseActivity context, StoresActivity storesActivity) {
        this.context = context;
        this.storesActivity = storesActivity;
    }

    public EndlessScrollListener(VostoBaseActivity context, StoresActivity storesActivity, int visibleThreshold) {
        this.context = context;
        this.storesActivity = storesActivity;
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
//            Log.d("EndlessList", "Loading: " + loading);
//            Log.d("EndlessList", "Item total: " + totalItemCount);
//            Log.d("EndlessList", "Previous total: " + previousTotal);
//            Log.d("EndlessList", "Current Page: " + currentPage);
//            Log.d("EndlessList", "Fetching Page: " + (currentPage + 1));

            SearchService service = new SearchService(this.storesActivity, this.context);
            service.setSearchTerm("");
            service.setPage(currentPage + 1);
            service.setHasLocation(false);
            service.execute();
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
