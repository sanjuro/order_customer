package com.vosto.customer.stores.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.R.id;
import com.vosto.customer.R.layout;
import com.vosto.customer.R.menu;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.vos.StoreVo;
/**
 * @author flippiescholtz
 *
 */
public class StoresActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {
	
	private StoreVo[] stores;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stores);
		ListView list = (ListView)findViewById(R.id.lstStores);
		list.setOnItemClickListener(this);
		
	

		Object[] objects = (Object[]) this.getIntent().getSerializableExtra("stores");
		this.stores = new StoreVo[objects.length];
		for(int i = 0; i<objects.length; i++){
			this.stores[i] = (StoreVo)objects[i];
		}
		
		list.setAdapter(new StoreListAdapter(this, R.layout.store_item_row, this.stores));
		
		boolean hasLocation = getIntent().getBooleanExtra("hasLocation", false);
		TextView lblStoresListHeading = (TextView)findViewById(R.id.lblStoresListHeading);
		if(this.stores.length > 0){
			lblStoresListHeading.setText(hasLocation ? "Close to you" : "Search Results");
		}else{
			lblStoresListHeading.setText("No stores found");
		}
		
	}
	
	public void onResume(){
		super.onResume();
		//GetStoresService service = new GetStoresService(this);
		//service.execute();
	}

	
	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
		if(result == null){
			return;
		}
		ListView list = (ListView)findViewById(R.id.lstStores);
		if(list == null){
			Log.d("ERROR", "List is null");
		}
		
		this.stores = ((GetStoresResult)result).getStores();
		list.setAdapter(new StoreListAdapter(this, R.layout.store_item_row, this.stores));
	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		/*
		  Cart cart = getCart();
		  cart.setStore(this.stores[position]);
		  saveCart(cart);
		  */
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
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	  } 
	
	
	public void homeClicked(){
		Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.itemStores:
	      break;
	    
	    default:
	      break;
	    }

	    return true;
	  }

	
	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
		finish();
	}

	
}
