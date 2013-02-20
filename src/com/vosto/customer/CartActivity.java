package com.vosto.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItemAdapter;
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
public class CartActivity extends VostoActivity implements OnRestReturn, OnItemClickListener {
	
	private Cart cart;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		ListView list = (ListView)findViewById(R.id.lstCartItems);
		list.setOnItemClickListener(this);
		
		getActionBar().setTitle("");
		this.cart = ((VostoCustomerApp)getApplicationContext()).getLatestCart();
		
		Log.d("CRT", "Items in cart: " + this.cart.getNumberOfItems());
		
		list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, this.cart.getItems()));
		Log.d("CRT", "Cart Adapter set.");
		
		TextView lblCartTotal = (TextView)findViewById(R.id.lblCartTotal);
		lblCartTotal.setText("R " + this.cart.getTotalPrice());
	}
	
	public void onResume(){
		super.onResume();
		GetStoresService service = new GetStoresService(this);
		service.execute();
	}

	
	@Override
	public void onRestReturn(RestResult result) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 
	}
	


	@Override
	public void storesPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cartPressed() {
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
