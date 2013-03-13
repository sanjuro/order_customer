package com.vosto.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;

import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.cart.activities.CartActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.activities.StoresActivity;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.stores.services.SearchService;

/**
 * 
 * @author Flippie Scholtz <flippiescholtz@gmail.com>
 * 
 * This is the home screen containing the search box. It is where the user starts searching for stores.
 *
 */
public class HomeActivity extends VostoBaseActivity implements OnRestReturn {
	
	private ProgressDialog pleaseWaitDialog;
    private SlideHolder mSlideHolder;
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_home);
        
      
        TextView userNameLabel = (TextView)findViewById(R.id.user_name_label);
        userNameLabel.setVisibility(View.GONE);
        
        ImageButton signInButton = (ImageButton)findViewById(R.id.sign_in_arrow_button);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });


        // Display either a sign in button or the user's name depending if someone is logged in:
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
        	//User logged in:
        	signInButton.setVisibility(View.GONE);
            TextView nameOfUser = (TextView)findViewById(R.id.name_of_user);
            nameOfUser.setText(settings.getString("userName", "user"));
        }else{
        	//User not logged in:
        	userNameLabel.setVisibility(View.GONE);
        }
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
		
		// Try to get a location. If we don't have a location we just leave it blank in the search.
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = locationManager
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);
		

	    double latitude = 0;
	    double longitude = 0;
	    boolean hasLocation = false;
	    
		if(enabled){
			//GPS is turned on.
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);

		    
		    // Initialize the location fields
		    if (location != null) {
		       latitude = location.getLatitude();
		       longitude = location.getLongitude();
		     hasLocation = true;
		    } else {
		    	hasLocation = false;
		    }
		}
		  
		this.pleaseWaitDialog = ProgressDialog.show(this, "Searching", "Please wait...", true);
		
		SearchService service = new SearchService(this, this);
		service.setSearchTerm(txtSearch.getText().toString().trim());
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
		if(result != null && result instanceof SearchResult){
			SearchResult searchResult = (SearchResult)result;
			
			//Pass the returned stores on to the stores list activity, and redirect:
			Intent intent = new Intent(this, StoresActivity.class);
			intent.putExtra("stores", searchResult.getStores());
			intent.putExtra("hasLocation", searchResult.hasLocation());
	    	startActivity(intent);
	    	//finish();
		}
		
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
		editor.putString("userPin", "");
		editor.commit();
		deleteCart();
		
		//Blank slate, redirect to signin page for new user signin:
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
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

	
}