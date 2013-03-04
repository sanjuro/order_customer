package com.vosto.customer;

import com.vosto.customer.orders.Cart;
import com.vosto.customer.services.vos.OrderVo;

import android.app.Application;

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
	  if(this.hasOpenCart()){
		  this.latestCart.close();
	  }
  }
  
  public String getAuthenticationToken(){
	  return this.authenticationToken;
  }
  
  public void setAuthenticationToken(String authToken){
	  this.authenticationToken = authToken;
  }
}
