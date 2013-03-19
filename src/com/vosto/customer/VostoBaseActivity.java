package com.vosto.customer;

import android.content.Intent;
import android.view.View;
import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.cart.activities.CartActivity;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.accounts.activities.EditProfileActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * This is the base class from which all activities should inherit.
 * It provides common functionality such as checking user login status, getting the auth token,
 * getting and saving the current cart, etc.
 * 
 * @author Flippie Scholtz <flippiescholtz@gmail.com>
 *
 */
public abstract class VostoBaseActivity extends Activity {
	
	// Subclasses can display a basic please wait dialog with spinner:
	protected ProgressDialog pleaseWaitDialog;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * Saves the given cart object to the app's context for later retrieval.
	 * At the moment the cart is lost when the app is terminated.
	 * 
	 * @param cart
	 */
	public void saveCart(Cart cart){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		context.setLatestCart(cart);
	}
	
	/**
	 * Clears the cart from the app context. Used when the user logs out, etc.
	 */
	public void deleteCart(){
		((VostoCustomerApp)getApplicationContext()).clearCart();
	}
	
	
	
	/**
	 * Gets the base application context. This should be used wherever an application context is needed.
	 * 
	 */
	public VostoCustomerApp getContext(){
		return (VostoCustomerApp)getApplicationContext();
	}
	
	/**
	 * Returns the cart stored in the app context, or creates a new cart if there is none.
	 * @return The cart instance.
	 */
	public Cart getCart(){
		VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
		Cart cart;
		if(!context.hasOpenCart()){
			cart = new Cart();
		}else{
			cart = context.getLatestCart();
		}
		return cart;
	}
	
	/**
	 * Returns the stores user auth token, or returns the default Android token if there isn't a user token.
	 */
	public String getAuthenticationToken(){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		String token = settings.getString("userToken", "DXTTTTED2ASDBSD3");
		if(token.trim().equals("")){
			token = "DXTTTTED2ASDBSD3";
		}
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

    /**
     * Called when the main bottom menu bar's orders button is pressed.
     * Simply opens the orders activity
     * @param v
     */
    public void myOrdersPressed(View v) {
        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);
    }

    public void storesPressed(View v){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void myCartPressed(View v) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void profilePressed(View v){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Clears all the locally stored data (auth token, pin, username, cart, order)
     *
     * @param v The logout button, at the moment it's just the user name label for debugging purposes,
     * the design doesn't have a real logout button yet.
     */
    public void logout(View v){
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userToken", "");
        editor.putString("userName", "");
        editor.putString("userEmail", "");
        editor.putString("userMobile", "");
        editor.putString("userPin", "");
        editor.commit();
        deleteCart();

        //Blank slate, redirect to signin page for new user signin:
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
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