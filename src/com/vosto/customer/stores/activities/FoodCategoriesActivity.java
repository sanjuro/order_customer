package com.vosto.customer.stores.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.TagsListAdapter;
import com.vosto.customer.stores.services.GetTagsResult;
import com.vosto.customer.stores.services.GetTagsService;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.vos.StoreTagVo;

/**
 * Displays the list of food category tags when the user wants to search by food category.
 * The user can then select one to start the search.
 */
public class FoodCategoriesActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {
	private StoreTagVo[] storeTags;
	
	private ProgressDialog pleaseWaitDialog;
	
	//Passed from the HomeActivity:
	private float latitude;
	private float longitude;
	private boolean hasLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_categories);
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading Categories", "Please wait...", true);
		
		this.latitude = getIntent().getFloatExtra("latitude", -1.0f);
		this.longitude = getIntent().getFloatExtra("longitude", -1.0f);
		this.hasLocation = getIntent().getBooleanExtra("hasLocation", false);
		if(!getIntent().hasExtra("latitude") || !getIntent().hasExtra("longitude")){
			this.hasLocation = false;
		}
		
		GetTagsService service = new GetTagsService(this, this);
		service.execute();
	}

	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			return;
		}
		
		
		if(result instanceof GetTagsResult){
			this.storeTags = ((GetTagsResult)result).getTags();
			ListView tagsList = (ListView)findViewById(R.id.lstFoodCategories);
			tagsList.setAdapter(new TagsListAdapter(this, R.layout.tag_item_row, this.storeTags));
			tagsList.setOnItemClickListener(this);
		}else if(result instanceof SearchResult){
			SearchResult searchResult = (SearchResult)result;
			//Pass the returned stores on to the stores list activity, and redirect:
			Intent intent = new Intent(this, StoresActivity.class);
			intent.putExtra("stores", searchResult.getStores());
			intent.putExtra("hasLocation", searchResult.hasLocation());
	    	startActivity(intent);
	    	//finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		this.pleaseWaitDialog = ProgressDialog.show(this, "Searching", "Please wait...", true);
		
		String queryTerm = this.storeTags[position].getTitle();
		SearchService service = new SearchService(this, this);
		service.setSearchTerm(queryTerm);
		if(this.hasLocation){
			//We have a location, so pass the coordinates on to the search service:
			service.setHasLocation(true);
			service.setLatitude(this.latitude);
			service.setLongitude(this.longitude);
		}
		service.execute();
	}
	

	@Override
	public void onBackPressed() {
		finish();
	}

	public void homeClicked(){
		Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
		finish();
	}

}