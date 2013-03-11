package com.vosto.customer;

import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.orders.vos.OrderVo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public abstract class VostoBaseActivity extends Activity {
	
	protected ProgressDialog pleaseWaitDialog;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	public void saveCart(Cart cart){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		context.setLatestCart(cart);
	}
	
	public void deleteCart(){
		((VostoCustomerApp)getApplicationContext()).clearCart();
	}
	
	public VostoCustomerApp getContext(){
		return (VostoCustomerApp)getApplicationContext();
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
		String token = settings.getString("userToken", "CXTTTTED2ASDBSD4");
		Log.d("AUTH", "Returning token: " + token);
		return token;
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