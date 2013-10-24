package com.vosto.customer;

import java.util.Random;
import java.util.List;


import android.app.Activity;
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

import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.vosto.customer.accounts.activities.EditProfileActivity;
import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.cart.activities.CartActivity;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.favourites.activities.StoreFavouritesActivity;
import com.vosto.customer.loyalties.activities.LoyaltyActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.pages.activities.TermsActivity;

/**
 * This is the base class from which all activities should inherit.
 * It provides common functionality such as checking user login status, getting the auth token,
 * getting and saving the current cart, etc.
 *
 *
 */
public abstract class VostoBaseActivity extends Activity implements LocationListener {
	
	// Subclasses can display a basic please wait dialog with spinner:
	public ProgressDialog pleaseWaitDialog;
	
	
    /*
     * If the gps is enabled, this activity will keep listening for location updates
     * for the life of the activity, and it will keep updating this variable.
     * When we need a location, it uses this, or else it just gets the location from
     * the best available provider.
     */
    protected Location currentGpsLocation;
    protected boolean listenForGpsUpdates; //If false, we don't listen to GPS updates.
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.listenForGpsUpdates = false; // Set this to true in the subclass activity to listen for gps updates.
	}
	
	  @Override
	  public void onStart() {
	    super.onStart();
	    
	    // Google Analytics:
	    EasyTracker.getInstance().activityStart(this);

          TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);

          SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
          if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
              //User logged in:
              nameOfUser.setText(settings.getString("userName", "user"));
          }
	    
	    if(this.listenForGpsUpdates){
	    	startListeningForGps(); // Start listening for GPS updates.
	    }
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    
	    // Google Analytics:
	    EasyTracker.getInstance().activityStop(this);
	    
	    this.stopListeningForGps();
	  }
	  
	  @Override
	  public void onResume(){
		  super.onResume();
		  if(this.listenForGpsUpdates){
			  this.startListeningForGps();
		  }
	  }
	  
	  
	
	/**
	 * Saves the given cart object to the app's context for later retrieval.
	 * At the moment the cart is lost when the app is terminated.
	 * 
	 * @param cart
	 */
	public void saveCart(Cart cart){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		context.setLatestCart(cart);
	}
	
	/**
	 * Clears the cart from the app context. Used when the user logs out, etc.
	 */
	public void deleteCart(){
		((VostoCustomerApp)getApplicationContext()).clearCart();
	}
	
	
	
	/**
	 * Gets the base application context. This should be used wherever an application context is needed.
	 * 
	 */
	public VostoCustomerApp getContext(){
		return (VostoCustomerApp)getApplicationContext();
	}
	
	/**
	 * Returns the cart stored in the app context, or creates a new cart if there is none.
	 * @return The cart instance.
	 */
	public Cart getCart(){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		Cart cart;
		if(!context.hasOpenCart()){
			cart = new Cart();
		}else{
			cart = context.getLatestCart();
		}
		return cart;
	}
	
	/**
	 * Returns the stores user auth token, or returns the default Android token if there isn't a user token.
	 */
	public String getAuthenticationToken(){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		return context.getAuthenticationToken();
	}
	
	public boolean isUserSignedIn(){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		String token = settings.getString("userToken", "");
		return !token.trim().equals("");
	}
	
	public void saveCurrentOrder(OrderVo order){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		context.saveCurrentOrder(order);
	}
	
	public OrderVo getCurrentOrder(){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		return context.getCurrentOrder();
	}

    /**
     * Called when the main bottom menu bar's orders button is pressed.
     * Simply opens the orders activity
     * @param v
     */
    public void myOrdersPressed(View v) {
        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);
    }

    public void storesPressed(View v){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void myCartPressed(View v) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void myFavouritesPressed(View v){
        Intent intent = new Intent(this, StoreFavouritesActivity.class);
        startActivity(intent);
    }

    public void myLoyaltyCardsPressed(View v){
        Intent intent = new Intent(this, LoyaltyActivity.class);
        startActivity(intent);
    }

    public void profilePressed(View v){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void feedbackPressed(View v){
        Intent mailer = new Intent(Intent.ACTION_SEND);
        mailer.setType("text/plain");
        mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{"customerservice@vosto.co.za"});
        startActivity(Intent.createChooser(mailer, "Send email..."));
    }

    public void termsPressed(View v){
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    /**
     * Clears all the locally stored data (auth token, pin, username, cart, order)
     *
     * @param v The logout button, at the moment it's just the user name label for debugging purposes,
     * the design doesn't have a real logout button yet.
     */
    public void logout(View v){
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userToken", "");
        editor.putString("userName", "");
        editor.putString("userEmail", "");
        editor.putString("userMobile", "");
        editor.commit();
        deleteCart();

        //Blank slate, redirect to signin page for new user signin:
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
    
    /** 
     * Shows a standard alert dialog with Close button in this activity.
     * @param title
     * @param message
     */
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
     * Dismisses the please wait dialog if it is showing.
     */
    public void dismissPleaseWaitDialog(){
    	if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
    		this.pleaseWaitDialog.dismiss();
    	}
    }

    public static String generateString()
    {
        char[] chars = "0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    
    /**
	 * Start listening for GPS updates so long (in case the user wants to populate the address by location).
	 */
	public void startListeningForGps(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 10, // min 10 seconds between location updates (can't be too frequent...battery life)
				20, // will only update for every 20 meters the device moves
				this);
        Log.d("GPS", "Listening for GPS updates...");
	}
	
	protected void stopListeningForGps(){
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.removeUpdates(this);
		Log.d("GPS", "Not listening for GPS updates anymore.");
	}
	 
	/**
	 *  Gets the best possible location that we have.
	 *  If GPS is disabled, ask user to enable it and return null.
	 *  If GPS doesn't work, try other location providers.
	 *  If that doesn't work, show an error and return null.
	 *  
	 *  requireGps: If true, we show an error if the GPS is disabled.
	 */
	protected Location getBestLocation(boolean requireGps){
        int minDistance = 5;
        long minTime = 10;

		// If the GPS is disabled, ask the user to enable it:
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(requireGps){
				this.showAlertDialog("Enable GPS", "Please turn on your GPS.");
			}
			return null;
		}
				
		/*
		 * Check if we have a recent (less than 30 mins old) updated location from the gps provider, otherwise we just
		 * try to get one from the best available provider:
		 */

        Location bestLocation = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;


        List<String> matchingProviders = locationManager.getAllProviders();
        for (String provider: matchingProviders) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestLocation = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime &&
                    bestAccuracy == Float.MAX_VALUE && time > bestTime){
                    bestLocation = location;
                    bestTime = time;
                }
            }
        }



//		if(this.currentGpsLocation != null && System.currentTimeMillis() - this.currentGpsLocation.getTime() <= 10 * 60 * 1000){
//			Log.d("GPS", "Current updated location is  NOT null");
//			// We have a recent location updated by the gps provider.
//			bestLocation = this.currentGpsLocation;
//		}else{
//			Log.d("GPS", "Current updated location is null");
//			// We don't have an updated gps location or it's older than 10 mins. Try to get a new one from the best available provider:
//			Criteria criteria = new Criteria();
//			String bestProvider = locationManager.getBestProvider(criteria, false);
//			bestLocation = locationManager.getLastKnownLocation(bestProvider);
//		}
				
		if(bestLocation == null){
			// We've tried everything but couldn't get a location. The gps could still be waiting for a location fix.
			this.showAlertDialog("Finding your location", "Vosto is waiting for your GPS to send your co-ordinates. Please retry you action in a few seconds.");
		   	return null;
		}
		
		return bestLocation;
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

	/*
	
	public abstract void storesPressed();

	public void cartPressed(){
		  Intent intent = new Intent(this, CartActivity.class);
      	  startActivity(intent);
	}

	public abstract void ordersPressed();

	public abstract void settingsPressed();
	
	*/
    
}