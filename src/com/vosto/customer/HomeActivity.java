package com.vosto.customer;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.accounts.activities.SignUpActivity;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.DealListAdapter;
import com.vosto.customer.stores.activities.FoodCategoriesActivity;
import com.vosto.customer.stores.activities.StoresActivity;
import com.vosto.customer.stores.services.GetFeaturedStoresResult;
import com.vosto.customer.stores.services.GetFeaturedStoresService;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.services.GetDealsService;
import com.vosto.customer.stores.services.GetDealsResult;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.stores.vos.DealVo;

import com.vosto.customer.utils.NetworkUtils;


public class HomeActivity extends VostoBaseActivity implements OnRestReturn, OnDismissListener, OnItemClickListener {

    private DealVo[] deals;
    private SlideHolder mSlideHolder;
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_home);
        
        this.listenForGpsUpdates = true;
       
        Log.d("TAG", "On Create .....");

        initialize();

        TextView notJoinedYet = (TextView)findViewById(R.id.notJoinedYet);
        Button signUpButton = (Button)findViewById(R.id.signUpButton);
        RelativeLayout bottom_bar = (RelativeLayout)findViewById(R.id.bottom_bar);


        // Display either a sign in button or the user's name depending if someone is logged in:
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
        	//User logged in:
            notJoinedYet.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            bottom_bar.setVisibility(View.GONE);

            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

        }else{
        	//User not logged in:
        }

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        ListView list = (ListView)findViewById(R.id.lstDealofTheWeek);
        list.setOnItemClickListener(this);

    }

    private void initialize(){
        if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
            this.pleaseWaitDialog.dismiss();
        }
        Log.d("DEALS", "Get Deals");
        GetDealsService service = new GetDealsService(this, this);
        service.execute();
    }
	
	public void signInClicked(View v){
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	/**
	 * Assigned in the layout xml, called by android when search button is clicked.
	 * 
	 * @param v The button instance that was clicked.
	 */
	public void searchClicked(View v){
		EditText txtSearch = (EditText)findViewById(R.id.txtSearch);
		if(txtSearch.getText().toString().trim().equals("")){
			return;
		}
		
		searchByQueryTerm(txtSearch.getText().toString().trim());
		
	}

    /**
     * Redirects the user to signup if she does not have an account yet.
     * @param v The signup button instance.
     */
    public void signUpClicked(View v){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
	
	/**
	 * Makes a search call with the given query term, including a GPS location only if available.
	 */
	private void searchByQueryTerm(String queryTerm){
		// Check if we have an updated GPS location, otherwise try to get a new one:	
		Location location = getBestLocation(false); 
				
		SearchService service = new SearchService(this, this);
		service.setSearchTerm(queryTerm);	
//		if(location != null){
//			//We have a location, so pass the coordinates on to the search service:
//			service.setHasLocation(true);
//			service.setLatitude(location.getLatitude());
//			service.setLongitude(location.getLongitude());
//		}
		service.execute();
	}
	

	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
        Log.d("onRestReturn", "Rest Return");

		if(result == null){
            Log.d("onRestReturn", "Result is Null");
			return;
		}
        Log.d("onRestReturn", "Got Result");

		if(result instanceof SearchResult){
			this.stopListeningForGps();
			SearchResult searchResult = (SearchResult)result;

			//Pass the returned stores on to the stores list activity, and redirect:
			Intent intent = new Intent(this, StoresActivity.class);
			intent.putExtra("stores", searchResult.getStores());
			intent.putExtra("hasLocation", searchResult.hasLocation());
	    	startActivity(intent);
	    	finish();
		} else if(result instanceof GetDealsResult){
            Log.d("DEAL", "Got Deals" );

            GetDealsResult getDealsResult = (GetDealsResult)result;
            this.deals = getDealsResult.getDeals();

            Log.d("DEAL", "Got Deals Count:" + this.deals.length);

            ListView list = (ListView)findViewById(R.id.lstDealofTheWeek);
            list.setAdapter(new DealListAdapter(this, R.layout.deal_item_row, this.deals));
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
        Log.d("STO", "Passing store to TaxonActivity: " + this.deals[position].getId());
        Intent intent = new Intent(this, TaxonsActivity.class);
        intent.putExtra("deal", this.deals[position]);
        intent.putExtra("dealName", this.deals[position].getName());
        intent.putExtra("dealDescription", this.deals[position].getDescription());
        intent.putExtra("callingActivity", "StoresActivity");
        startActivity(intent);
        // finish();
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
			return;
		}
	 
	    // We have a location. Make the search call:
		SearchService service = new SearchService(this, this);
		//Set the search term blank because we are searching by location only:
		service.setSearchTerm("");
		service.setHasLocation(true);
		service.setLatitude(bestLocation.getLatitude());
		service.setLongitude(bestLocation.getLongitude());
		service.execute();
	}
	
	
	/**
	 * Called from activity_home.xml when the find by category button is pressed.
	 * @param v An instance of the button.
	 */
	public void findByCategoryClicked(View v){
		Location location = getBestLocation(false); 
		Intent intent = new Intent(this, FoodCategoriesActivity.class);
		intent.putExtra("hasLocation", location != null);
		if(location != null){
			intent.putExtra("latitude", location.getLatitude());
			intent.putExtra("longitude", location.getLongitude());
		}
		startActivity(intent);
	}


    @Override
    public void onDismiss(DialogInterface dialog) {

    }

	
}