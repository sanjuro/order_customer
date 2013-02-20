package com.vosto.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainTabActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("");
	}
	
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	  } 
	
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.itemStores:
	    	  Intent intent = new Intent(this, StoresActivity.class);
	       	   startActivity(intent);
	       	   finish();
	      break;
	    
	    default:
	      break;
	    }

	    return true;
	  } 
	
	
	
	
}