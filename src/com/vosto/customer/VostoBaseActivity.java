package com.vosto.customer;

import com.vosto.customer.orders.Cart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public abstract class VostoBaseActivity extends Activity implements IMainMenuListener {
	
	protected ProgressDialog pleaseWaitDialog;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	public void saveCart(Cart cart){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		context.setLatestCart(cart);
	}
	
	public Cart getCart(){
		Log.d("Cart", "Getting cart in base activity.");
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		Cart cart;
		if(!context.hasOpenCart()){
			Log.d("Cart", "Creating new cart in base activity.");
			cart = new Cart();
		}else{
			Log.d("Cart", "Getting existing latest cart in base activity.");
			cart = context.getLatestCart();
			if(cart == null){
				Log.d("Cart", "But it is null");
			}
		}
		if(cart == null){
			Log.d("Cart", "Base activity returning a null cart!");
		}
		return cart;
	}
	
	public String getAuthenticationToken(){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		return settings.getString("userToken", "CXTTTTED2ASDBSD4");
	}
	
	public abstract void storesPressed();

	public void cartPressed(){
		  Intent intent = new Intent(this, CartActivity.class);
      	  startActivity(intent);
	}

	public abstract void ordersPressed();

	public abstract void settingsPressed();
	
}