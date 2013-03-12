package com.vosto.customer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.orders.vos.OrderVo;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * The overridden application context, so that we can store the cart, authtoken, order, etc in the app
 * while it is running. This should be used whenever an application context is required.
 *
 */
public class VostoCustomerApp extends Application {

  private Cart latestCart;
  private OrderVo currentOrder;
  private String authenticationToken;

  public Cart getLatestCart(){
    return this.latestCart;
  }
  
  public void setLatestCart(Cart cart){
    this.latestCart = cart;
  }
  
  public boolean hasOpenCart(){
	  return this.latestCart != null && this.latestCart.isOpen();
  }
  
  public boolean hasClosedCart(){
	  return this.latestCart != null && !this.latestCart.isOpen();
  }
  
  public void closeCart(){
	  Log.d("CLO", "Closing cart.");
	  if(this.hasOpenCart()){
		  this.latestCart.close();
	  }
  }
  
  public void clearCart(){
	  this.latestCart = null;
  }
  
  public void saveCurrentOrder(OrderVo order){
	  this.currentOrder = order;
	  Gson gson = new Gson();
	  SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
	  SharedPreferences.Editor editor = settings.edit();
	  editor.putString("currentOrderJson", gson.toJson(this.currentOrder));
      editor.commit();
  }
  
  public boolean hasPendingOrder(){
	  return this.currentOrder != null;
  }
  
  public OrderVo getCurrentOrder(){
	  Gson gson = new Gson();
	  SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
	  if(!settings.contains("currentOrderJson")){
		  return null;
	  }
	  try{
		  return gson.fromJson(settings.getString("currentOrderJson", ""), OrderVo.class);
	  }catch(JsonSyntaxException e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  public String getAuthenticationToken(){
	  return this.authenticationToken;
  }
  
  public void setAuthenticationToken(String authToken){
	  this.authenticationToken = authToken;
  }
}
