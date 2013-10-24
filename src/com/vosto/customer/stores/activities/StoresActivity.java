package com.vosto.customer.stores.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView.OnScrollListener;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.stores.activities.FoodCategoriesActivity;
import com.vosto.customer.stores.EndlessScrollListener;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.NetworkUtils;

import com.agimind.widget.SlideHolder;

public class StoresActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnScrollListener {
	
    private StoreVo[] stores;
    private SlideHolder mSlideHolder;

    private int visibleThreshold = 1;
    private int currentPage = 1;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean hasLocation = false;
    ListView list;
    View footerView;
    StoreListAdapter storeListAdapter;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stores);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
            //User logged in:
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

        }else{
            //User not logged in:
        }

        list = (ListView)findViewById(R.id.lstStores);

		Object[] objects = (Object[]) this.getIntent().getSerializableExtra("stores");
        this.stores = new StoreVo[objects.length];
		for(int i = 0; i<objects.length; i++){
			this.stores[i] = (StoreVo)objects[i];
		}

        storeListAdapter = new StoreListAdapter(this, R.layout.store_item_row, this.stores);
		list.setAdapter(storeListAdapter);

        list.setOnScrollListener(this);
        list.setOnItemClickListener(this);


		boolean hasLocation = getIntent().getBooleanExtra("hasLocation", false);
		TextView lblStoresListHeading = (TextView)findViewById(R.id.lblStoresListHeading);
        LinearLayout storesResults = (LinearLayout)findViewById(R.id.storesResults);

		if(this.stores.length > 0){
            storesResults.setVisibility(View.GONE);
			lblStoresListHeading.setText(hasLocation ? "Close to you" : "Search Results");
		}else{
            storesResults.setVisibility(View.VISIBLE);
			lblStoresListHeading.setText("No stores found");
		}

	}
	
	public void onResume(){
		super.onResume();
		//GetStoresService service = new GetStoresService(this);
		//service.execute();
	}

	
	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
		if(result == null){
			return;
		}
		ListView list = (ListView)findViewById(R.id.lstStores);
		if(list == null){
			Log.d("ERROR", "List of stores is null");
		}

        if(result instanceof SearchResult){
            this.stopListeningForGps();
            loading = false;

            SearchResult searchResult = (SearchResult)result;

            if (searchResult.getStores().length > 0){
                for(StoreVo store : searchResult.getStores() ){
                    insertStore(store);
                }
                Log.d("SEARCH", "Stores length: " + this.stores.length);

                StoreListAdapter storeListAdapter = new StoreListAdapter(this, R.layout.store_item_row, this.stores);
                list.setAdapter(storeListAdapter);
                storeListAdapter.notifyDataSetChanged();

                list.setSelection(this.stores.length - 16);

            }else{

            }


            // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        if(result instanceof GetStoresResult){

		    this.stores = ((GetStoresResult)result).getStores();
		    list.setAdapter(new StoreListAdapter(this, R.layout.store_item_row, this.stores));
        }
	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		/*
		  Cart cart = getCart();
		  cart.setStore(this.stores[position]);
		  saveCart(cart);
		  */
		if(!NetworkUtils.isNetworkAvailable(this)){
			this.showAlertDialog("Connection Error", "Please connect to the internet.");
			return;
		}
		Log.d("STO", "Passing store to TaxonActivity: " + this.stores[position].getId());
        Intent intent = new Intent(this, TaxonsActivity.class);
        intent.putExtra("store", this.stores[position]);
        intent.putExtra("storeName", this.stores[position].getName());
        intent.putExtra("storeTel", this.stores[position].getManagerContact());
        intent.putExtra("storeAddress", this.stores[position].getAddress());
        intent.putExtra("callingActivity", "StoresActivity");
        startActivity(intent);
      	 // finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	  }


    /**
     * Called from the activity_home.xml when the find by location button is pressed.
     * Initiates the find by nearest location search, or prompts for gps.
     * @param v An instance of the button
     */
    public void findByLocationClicked(View v){

        Location bestLocation = this.getBestLocation(true);
        if(bestLocation == null){
            // The getBestLocation() method will show any errors to the user.
            this.hasLocation = false;
            return;
        }
        this.hasLocation = true;

        // We have a location. Make the search call:
        SearchService service = new SearchService(this, this);
        //Set the search term blank because we are searching by location only:
        service.setSearchTerm("");
        service.setPage(this.currentPage);
        service.setHasLocation(true);
        service.setLatitude(bestLocation.getLatitude());
        service.setLongitude(bestLocation.getLongitude());
        service.execute();
    }


	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.itemStores:
	      break;
	    
	    default:
	      break;
	    }

	    return true;
	  }

	
        public void ordersPressed(View v) {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
            finish();
        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                if (list.getLastVisiblePosition() >= list.getCount() - visibleThreshold) {
                    currentPage++;
                    //load more list items:

                    Location bestLocation = this.getBestLocation(true);
                    if(bestLocation == null){
                        // The getBestLocation() method will show any errors to the user.
                        this.hasLocation = false;
                        return;
                    }
                    this.hasLocation = true;

                    Log.d("EndlessList", "Calling service with current page: " + currentPage);
                    SearchService service = new SearchService(this, this);
                    service.setSearchTerm("");
                    service.setPage(currentPage);

                    if (this.hasLocation){
                        service.setHasLocation(true);
                        service.setLatitude(bestLocation.getLatitude());
                        service.setLongitude(bestLocation.getLongitude());
                    }else{
                        service.setHasLocation(false);
                    }

                    service.execute();
                    loading = true;
                }
            }
        }

    public void insertStore(StoreVo store) {

        //Create a temp array with length equal to h
        StoreVo[] temp = new StoreVo[this.stores.length+1];

        //copy the horses to the temp array
        for (int i = 0; i < stores.length; i++){
            temp[i] = stores[i];
        }

        //add the horse on the temp array
        temp[temp.length-1] = store;

        //change the reference of the old array to the temp one
        this.stores = temp;
    }
}
