package com.vosto.customer;

import com.vosto.customer.services.GetTaxonsService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public abstract class VostoActivity extends Activity implements IMainMenuListener {
	
	protected Menu mainMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    this.mainMenu = menu;
	    return true;
	  } 
	
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.itemStores:
	    	storesPressed();
	    	break;
	    case R.id.itemCart:
	    	cartPressed();
	    	break;
	    case R.id.itemOrders:
	    	ordersPressed();
	    	break;
	    case R.id.itemSettings:
	    	settingsPressed();
	    	break;
	    default:
	      break;
	    }

	    return true;
	  }

	public abstract void storesPressed();

	public void cartPressed(){
		  Intent intent = new Intent(this, CartActivity.class);
      	  startActivity(intent);
	}

	public abstract void ordersPressed();

	public abstract void settingsPressed();
	
	
}