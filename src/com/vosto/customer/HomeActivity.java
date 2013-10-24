package com.vosto.customer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.location.Geocoder;
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
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.services.GetDealsService;
import com.vosto.customer.stores.services.GetDealsResult;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.stores.vos.DealVo;

import com.vosto.customer.utils.NetworkUtils;


public class HomeActivity extends VostoBaseActivity implements OnRestReturn, OnDismissListener, OnItemClickListener {

    public Location bestLocation;
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
        TextView username = (TextView)findViewById(R.id.username);
        Button signUpButton = (Button)findViewById(R.id.signUpButton);


        // Display either a sign in button or the user's name depending if someone is logged in:
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
        	//User logged in:
            notJoinedYet.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            username.setVisibility(View.VISIBLE);

            username.setText("Welcome, " + settings.getString("userName", "user"));

        }else{
        	//User not logged in:
            username.setVisibility(View.GONE);
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

        this.bestLocation = this.getBestLocation(true);
        if(bestLocation == null){
            // The getBestLocation() method will show any errors to the user.
            return;
        }else{
            double LATITUDE = this.bestLocation.getLatitude();
            double LONGITUDE = this.bestLocation.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null && addresses.size() > 0) {
                    // Help here to get only the street name
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getAddressLine(1);

                    TextView current_location = (TextView)findViewById(R.id.current_location);
                    current_location.setText(address + ", " + city);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Log.d("DEALS", "Get Deals");
        GetDealsService service = new GetDealsService(this, this);
        service.execute();
    }
	
	public void signInClicked(View v){
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	// finish();
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
        // finish();

        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }
	
	/**
	 * Makes a search call with the given query term, including a GPS location only if available.
	 */
	private void searchByQueryTerm(String queryTerm){
		// Check if we have an updated GPS location, otherwise try to get a new one:	
		Location location = getBestLocation(false); 
				
		SearchService service = new SearchService(this, this);
		service.setSearchTerm(queryTerm);
        service.setPage(1);
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
	    	// finish();

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        Log.d("STO", "Passing deal to StoreActivity: " + this.deals[position].getId());
        Intent intent = new Intent(this, TaxonsActivity.class);
        intent.putExtra("store", this.deals[position].getStore());
        intent.putExtra("storeName", this.deals[position].getStore().getName());
        intent.putExtra("storeTel", this.deals[position].getStore().getTelephone());
        intent.putExtra("storeAddress", this.deals[position].getStore().getAddress());
        intent.putExtra("callingActivity", "StoresActivity");
        startActivity(intent);
        // finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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


//        double LATITUDE = bestLocation.getLatitude();
//        double LONGITUDE = bestLocation.getLongitude();
//        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
//
//        try {
//            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
//            if (addresses != null && addresses.size() > 0) {
//                // Help here to get only the street name
//                String address = addresses.get(0).getAddressLine(0);
//                String city = addresses.get(0).getAddressLine(1);
//                String country = addresses.get(0).getAddressLine(2);
//
//                TextView current_location = (TextView)findViewById(R.id.current_location);
//                current_location.setText(address + ", " + city);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // We have a location. Make the search call:
		SearchService service = new SearchService(this, this);
		//Set the search term blank because we are searching by location only:
		service.setSearchTerm("");
        service.setPage(1);
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

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}


    @Override
    public void onLocationChanged(Location location) {
        double LATITUDE = location.getLatitude();
        double LONGITUDE = location.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        Log.d("GPS", "Got New Location onLocationChanged: (" + location.getLatitude() + " , " + location.getLongitude() + " )");

        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            Log.d("GPS", "Checking Location: (" + addresses.get(0).getAddressLine(0) + " )");

            if (addresses != null && addresses.size() > 0) {
                Log.d("GPS", "Setting Location: (" + location.getLatitude() + " , " + location.getLongitude() + " )");
                // Help here to get only the street name
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);

                TextView current_location = (TextView)findViewById(R.id.current_location);
                current_location.setText(address + ", " + city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {

    }

	
}