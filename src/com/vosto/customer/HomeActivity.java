package com.vosto.customer;

import com.vosto.customer.R;
import com.vosto.customer.accounts.SignInActivity;
import com.vosto.customer.services.CreateAccountResult;
import com.vosto.customer.services.CreateAccountService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.SearchResult;
import com.vosto.customer.services.SearchService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


public class HomeActivity extends VostoBaseActivity implements OnRestReturn {
	
	private ProgressDialog pleaseWaitDialog;
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_home);
        
      
        TextView userNameLabel = (TextView)findViewById(R.id.user_name_label);
        userNameLabel.setVisibility(View.GONE);
        
        ImageButton signInButton = (ImageButton)findViewById(R.id.sign_in_arrow_button);
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
        	//User logged in:
        	signInButton.setVisibility(View.GONE);
        	userNameLabel.setText("Hi, " + settings.getString("userName", "user"));
        	userNameLabel.setVisibility(View.VISIBLE);
        }else{
        	userNameLabel.setVisibility(View.GONE);
        	signInButton.setVisibility(View.VISIBLE);
        	
        }
    }
	
	
	public void signInClicked(View v){
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	
	public void searchClicked(View v){
		EditText txtSearch = (EditText)findViewById(R.id.txtSearch);
		if(txtSearch.getText().toString().trim().equals("")){
			return;
		}
		
		// Prompt user if the GPS is not enabled:
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = locationManager
		  .isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!enabled) {
			showAlertDialog("GPS Required", "Please turn on your phone's GPS.");
			return;
		} 
		
		
		 Criteria criteria = new Criteria();
		   String provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);

		    double latitude = 0;
		    double longitude = 0;
		    boolean hasLocation = false;
		    
		    // Initialize the location fields
		    if (location != null) {
		       latitude = location.getLatitude();
		       longitude = location.getLongitude();
		     hasLocation = true;
		    } else {
		    	hasLocation = false;
		    	showAlertDialog("ERROR", "Location not available.");
		    }
		  
		
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Searching", "Please wait...", true);
		
		SearchService service = new SearchService(this, this);
		service.setSearchTerm(txtSearch.getText().toString().trim());
		if(hasLocation){
			service.setHasLocation(true);
			service.setLatitude(latitude);
			service.setLongitude(longitude);
		}
		service.execute();
		
	}
	
	

	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result != null && result instanceof SearchResult){
			SearchResult searchResult = (SearchResult)result;
			Intent intent = new Intent(this, StoresActivity.class);
			intent.putExtra("stores", searchResult.getStores());
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
	
	public void logout(View v){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userToken", "");
		editor.putString("userName", "");
		editor.putString("userPin", "");
		editor.commit();
		deleteCart();
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
	}


	


	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
	}



	
}