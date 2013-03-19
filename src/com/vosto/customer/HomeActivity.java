package com.vosto.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;

import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.TagsListAdapter;
import com.vosto.customer.stores.activities.StoresActivity;
import com.vosto.customer.stores.services.GetTagsResult;
import com.vosto.customer.stores.services.GetTagsService;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;
import com.vosto.customer.stores.vos.StoreTagVo;

/**
 * 
 * @author Flippie Scholtz <flippiescholtz@gmail.com>
 * 
 * This is the home screen containing the search box. It is where the user starts searching for stores.
 *
 */
public class HomeActivity extends VostoBaseActivity implements OnRestReturn, LocationListener {
	
	private ProgressDialog pleaseWaitDialog;
    private SlideHolder mSlideHolder;
    private StoreTagVo[] mStoreTags;
    
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


        ImageButton signInButton = (ImageButton)findViewById(R.id.sign_in_arrow_button);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);


        // Display either a sign in button or the user's name depending if someone is logged in:
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
        	//User logged in:
        	signInButton.setVisibility(View.GONE);
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

            View toggleView = findViewById(R.id.menuButton);
            toggleView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSlideHolder.toggle();
                }
            });
        }else{
        	//User not logged in:

        }
        
        //Start listening for gps updates:
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 5, // min 5 seconds between location updates (can't be too frequent...battery life)
				20, // will only update for every 20 meters the device moves
				this);
        Log.d("GPS", "Listening for GPS updates...");
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
	 * Makes a search call with the given query term, including a GPS location only if available.
	 */
	private void searchByQueryTerm(String queryTerm){
		// Check if we have an updated GPS location, otherwise try to get a new one:
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
				
				double latitude = 0;
			    double longitude = 0;
			    boolean hasLocation = false;
			    
			    if (location != null) {
			    	// We now have either the last updated gps location or a new one from the gps provider:
			       latitude = location.getLatitude();
			       longitude = location.getLongitude();
			       hasLocation = true;
			    } else {
			    	hasLocation = false;
			    }
			
				this.pleaseWaitDialog = ProgressDialog.show(this, "Searching", "Please wait...", true);
				
				SearchService service = new SearchService(this, this);
				service.setSearchTerm(queryTerm);
				if(hasLocation){
					//We have a location, so pass the coordinates on to the search service:
					service.setHasLocation(true);
					service.setLatitude(latitude);
					service.setLongitude(longitude);
				}
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
	    	//finish();
		}else if(result instanceof GetTagsResult){
			mStoreTags = ((GetTagsResult) result).getTags();
			promptForSearchTag();
		}
	}
	
	
	/**
	 * Opens a pop-up list of store tags so the user can choose one to search for.
	 * @param
	 */
	private void promptForSearchTag(){
		if(mStoreTags == null || mStoreTags.length == 0){
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose a food category");
		TagsListAdapter tagAdapter = new TagsListAdapter(this, R.layout.tag_item_row, mStoreTags);
		builder.setAdapter(tagAdapter, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	// perform the actual search. Same as the normal keyword search.
		        searchByQueryTerm(mStoreTags[item].getTitle());
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void showAlertDialog(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
	    this.pleaseWaitDialog = ProgressDialog.show(this, "Searching", "Please wait...", true);
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
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading categories", "Please wait...", true);
		GetTagsService service = new GetTagsService(this, this);
		service.execute();
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

	
}