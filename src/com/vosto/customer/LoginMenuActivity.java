package com.vosto.customer;

import com.vosto.customer.accounts.CreateAccountActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class LoginMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
	       // Check if we have a stored user token:
	       SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
	       String userToken = settings.getString("userToken", "");
	       if(userToken.trim() != ""){
	    	   Intent intent = new Intent(this, MainTabActivity.class);
	       	   startActivity(intent);
	       	   finish();
	       }else{
	    	   Toast.makeText(this, "Not Logged In!!!", Toast.LENGTH_LONG).show();
	       }
	      
	    

	   
	    /*
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putBoolean("silentMode", mSilentMode);
	      
	      */
		
	}
	
	public void createAccount(View view){
		 Intent intent = new Intent(this, CreateAccountActivity.class);
     	 startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
