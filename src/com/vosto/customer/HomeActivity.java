package com.vosto.customer;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.accounts.activities.SignUpActivity;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.stores.activities.FoodCategoriesActivity;
import com.vosto.customer.stores.activities.StoresActivity;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.services.GetFeaturedStoresService;
import com.vosto.customer.stores.services.GetFeaturedStoresResult;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.NetworkUtils;


public class HomeActivity extends VostoBaseActivity implements OnRestReturn, LocationListener, OnDismissListener, OnItemClickListener {

    private StoreVo[] stores;
    private SlideHolder mSlideHolder;

    /*
     * If the gps is enabled, this activity will keep listening for location updates
     * for the life of the activity, and it will keep updating this variable.
     * When we do a search by location, it uses this, or else it just gets the location from
     * the best available provider. (Remember that gps takes time to get a location fix, so we have to
     * start listening for it before the user presses the search button).
     */
    private Location currentGpsLocation;
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_home);
        Log.d("TAG", "On Create .....");

        initialize();

        ImageButton signInButton = (ImageButton)findViewById(R.id.sign_in_arrow_button);
        TextView notJoinedYet = (TextView)findViewById(R.id.notJoinedYet);
        Button signUpButton = (Button)findViewById(R.id.signUpButton);
        RelativeLayout bottom_bar = (RelativeLayout)findViewById(R.id.bottom_bar);


        // Display either a sign in button or the user's name depending if someone is logged in:
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
        	//User logged in:
        	signInButton.setVisibility(View.GONE);
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
        
        //Start listening for gps updates:
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 5, // min 5 seconds between location updates (can't be too frequent...battery life)
				20, // will only update for every 20 meters the device moves
				this);
        Log.d("GPS", "Listening for GPS updates...");

        ListView list = (ListView)findViewById(R.id.lstfeaturedStores);
        list.setOnItemClickListener(this);

    }

	public void onResume(Bundle args){
		//Start listening for gps updates (user has returned to this activity and might want to search soon):
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 5, // min 5 seconds between location updates (can't be too frequent...battery life)
				20, // will only update for every 20 meters the device moves
				this);
        Log.d("GPS", "Listening for GPS updates...");

	}

    private void initialize(){
        if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
            this.pleaseWaitDialog.dismiss();
        }
        Log.d("STO", "Get Featured stores");
        GetFeaturedStoresService service = new GetFeaturedStoresService(this, this);
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
	
	private Location getBestLocation(){
		Location location = null;
		if(this.currentGpsLocation != null){
			location = this.currentGpsLocation;
		}else{
			// No gps location saved, so try to get a new one:
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			boolean gpsEnabled = locationManager
			  .isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(gpsEnabled){
				//GPS is turned on.
				Criteria criteria = new Criteria();
				String provider = locationManager.getBestProvider(criteria, false);
			    location = locationManager.getLastKnownLocation(provider);
			}
		}
		return location;
	}
	
	
	/**
	 * Makes a search call with the given query term, including a GPS location only if available.
	 */
	private void searchByQueryTerm(String queryTerm){
		// Check if we have an updated GPS location, otherwise try to get a new one:	
		Location location = getBestLocation(); 
				
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
        Log.d("STORE", "Rest Return");
		if(result == null){
			return;
		}

		if(result instanceof SearchResult){
			// Stop listening for gps updates:
			  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			  locationManager.removeUpdates(this);
			  Log.d("GPS", "Not listening for GPS updates anymore.");

			
			SearchResult searchResult = (SearchResult)result;

			//Pass the returned stores on to the stores list activity, and redirect:
			Intent intent = new Intent(this, StoresActivity.class);
			intent.putExtra("stores", searchResult.getStores());
			intent.putExtra("hasLocation", searchResult.hasLocation());
	    	startActivity(intent);
	    	finish();
		} else if(result instanceof GetFeaturedStoresResult){
            GetFeaturedStoresResult featuredStoresResult = (GetFeaturedStoresResult)result;
            this.stores = featuredStoresResult.getStores();

            Log.d("STORE", "Got Stores Count:" + featuredStoresResult.getStores().length);
            ListView list = (ListView)findViewById(R.id.lstfeaturedStores);
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
    }

	/**
	 * Called from the activity_home.xml when the find by location button is pressed.
	 * Initiates the find by nearest location search, or prompts for gps.
	 * @param v An instance of the button
	 */
	public void findByLocationClicked(View v){
		// If the GPS is disabled, ask the user to enable it:
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			this.showAlertDialog("Enable GPS", "Please turn on your GPS to search by location.");
			return;
		}
		
		/*
		 * Check if we have a recent (less than 30 mins old) updated location from the gps provider, otherwise we just
		 * try to get one from the best available provider:
		 */
		Location bestLocation = null;
		if(this.currentGpsLocation != null && System.currentTimeMillis() - this.currentGpsLocation.getTime() <= 30 * 60 * 1000){
			Log.d("GPS", "Current updated location is  NOT null");
			// We have a recent location updated by the gps provider.
			bestLocation = this.currentGpsLocation;
		}else{
			Log.d("GPS", "Current updated location is null");
			// We don't have an updated gps location or it's older than 30 mins. Try to get a new one from the best available provider:
			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria, false);
			bestLocation = locationManager.getLastKnownLocation(bestProvider);
		}
		
		if(bestLocation == null){
			// We've tried everything but couldn't get a location. The gps could still be waiting for a location fix.
			this.showAlertDialog("Location problem", "Please wait for your GPS to determine your location and try again.");
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
		Location location = getBestLocation(); 
		Intent intent = new Intent(this, FoodCategoriesActivity.class);
		intent.putExtra("hasLocation", location != null);
		if(location != null){
			intent.putExtra("latitude", location.getLatitude());
			intent.putExtra("longitude", location.getLongitude());
		}
		startActivity(intent);
	}


	@Override
	public void onLocationChanged(Location location) {
		/* Location update received from the gps provider. 
		*  Update the location variable so we have it when the user searches:
		*/
		this.currentGpsLocation = location;
		Log.d("GPS", "Location updated: (" + location.getLatitude() + " , " + location.getLongitude() + " )");
	}


	@Override
	public void onProviderDisabled(String provider) {
		if(provider.equals(LocationManager.GPS_PROVIDER)){
			// GPS has been enabled:
			Log.d("GPS", "GPS Disabled");
		}
	}


	@Override
	public void onProviderEnabled(String provider) {
		if(provider.equals(LocationManager.GPS_PROVIDER)){
			// GPS has been enabled:
			Log.d("GPS", "GPS enabled");
		}
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

	
}