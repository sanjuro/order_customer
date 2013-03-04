package com.vosto.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.vosto.customer.orders.Cart;
import com.vosto.customer.services.GetStoresResult;
import com.vosto.customer.services.GetStoresService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.stores.StoreListAdapter;
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
		
	}
	
	public void onResume(){
		super.onResume();
		//GetStoresService service = new GetStoresService(this);
		//service.execute();
	}

	
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
		  Cart cart = getCart();
		  cart.setStore(this.stores[position]);
		  saveCart(cart);
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

	@Override
	public void storesPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ordersPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void settingsPressed() {
		// TODO Auto-generated method stub
		
	} 
}
