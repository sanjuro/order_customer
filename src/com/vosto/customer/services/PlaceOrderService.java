package com.vosto.customer.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings.Secure;
import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItem;
import com.vosto.customer.services.vos.LineItemVo;
import com.vosto.customer.services.vos.OrderVo;
import com.vosto.customer.services.vos.StoreVo;

public class PlaceOrderService extends RestService {
	
	private Cart cart;
	private OrderVo previousOrder;
	private StoreVo store;
	private VostoBaseActivity context;
	
	public PlaceOrderService(OnRestReturn listener, VostoBaseActivity context){
		super("http://107.22.211.58:9000/api/v1/orders", RequestMethod.POST, ResultType.PLACE_ORDER, listener);
		this.context = context;
	}


	public String getRequestJson(){
		if(this.cart != null && this.previousOrder == null){
			return this.getNewOrderRequestJson();
		}else if(this.previousOrder != null && this.store != null && this.cart == null){
			return this.getPreviousOrderRequestJson();
		}else{
			return "";
		}
	}
	
	private String getNewOrderRequestJson(){
		if(this.cart == null){
			return "";
		}
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", this.context.getAuthenticationToken());
			JSONObject order = new JSONObject();
			if(this.cart.getStore() == null){
				Log.d("ERR", "Cart's store is null");
			}else if(this.cart.getStore().getUniqueId() == null){
				Log.d("ERR", "Unique id is null");
			}
			order.put("unique_id", this.cart.getStore().getUniqueId());
			order.put("device_type", "android");
			order.put("device_identifier", Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID));
			JSONArray lineItemsArr = new JSONArray();
			ArrayList<CartItem> items = this.cart.getItems();
			for(int i = 0; i<items.size(); i++){
				JSONObject lineItemObj = new JSONObject();
				CartItem item = items.get(i);
				lineItemObj.put("variant_id", item.getVariant().getId());
				lineItemObj.put("quantity", item.getQuantity());
				lineItemObj.put("special_instructions", item.getSpecialInstructions());
				lineItemsArr.put(lineItemObj);
			}
			order.put("line_items", lineItemsArr);
			
			root.put("order", order);
			Log.d("jsontest", "Order Request JSON: " + root.toString());
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
		
		/*
		 * 
		 * {
    "authentication_token": "CXTTTTED2ASDBSD3",
    "user": {
        "first_name": "Test",
        "last_name": "Customer",
        "email": "test@gmail.com",
        "mobile_number": "0833908314",
        "birthday": "1981-12-04",
        "user_pin": "12345"
    }
}
		 * 
		 * 
		 */
	}
	
	private String getPreviousOrderRequestJson(){
		if(this.previousOrder == null || this.store == null){
			return "";
		}
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", this.context.getAuthenticationToken());
			JSONObject order = new JSONObject();
			order.put("unique_id", this.store.getUniqueId());
			order.put("device_type", "android");
			order.put("device_identifier", Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID));
			JSONArray lineItemsArr = new JSONArray();
			LineItemVo[] items = this.previousOrder.getLineItems();
			for(int i = 0; i<items.length; i++){
				JSONObject lineItemObj = new JSONObject();
				LineItemVo item = items[i];
				lineItemObj.put("variant_id", item.getVariant_id());
				lineItemObj.put("quantity", item.getQuantity());
				lineItemObj.put("special_instructions", item.getSpecial_instructions());
				lineItemsArr.put(lineItemObj);
			}
			order.put("line_items", lineItemsArr);
			
			root.put("order", order);
			Log.d("jsontest", "Order Request JSON: " + root.toString());
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
	}


	public Cart getCart() {
		return cart;
	}


	public void setCart(Cart cart) {
		this.cart = cart;
	}
	
	public void setOrder(OrderVo order){
		this.previousOrder = order;
	}
	
	public void setStore(StoreVo store){
		this.store = store;
	}
	
	
	
	
	
}