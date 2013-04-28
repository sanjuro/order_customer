package com.vosto.customer.orders.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.cart.vos.LineItemVo;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

import android.util.Log;

public class PlaceOrderResult extends RestResult implements IRestResult {
	
	
	 private boolean orderCreated;
	 private String errorMessage;
	 
	 private OrderVo order;
	
	 
	 public PlaceOrderResult(){
		 super();
		 this.orderCreated = false;
		 this.errorMessage = "";
	 }
	 
	 public PlaceOrderResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 public boolean wasOrderCreated(){
		 return this.orderCreated;
	 }
	 
	 public String getErrorMessage(){
		 return this.errorMessage;
	 }
	 
	public OrderVo getOrder() {
		return order;
	}
	
	@Override
	public boolean processJsonAndPopulate(){
		Log.d("order response", this.getResponseJson());

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(this.getResponseJson());
			if(jsonObj.isNull("number")){
				this.orderCreated = false;
				if(!jsonObj.isNull("detail")){
					this.errorMessage = jsonObj.getString("detail");
				}
				return false;
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
			this.orderCreated = true;
			this.order = new OrderVo();
			this.order.setId(jsonObj.getInt("id"));
			this.order.setNumber(jsonObj.getString("number"));
            this.order.setStoreOrderNumber(jsonObj.getString("store_order_number"));
            this.order.setTimeToReady(jsonObj.getString("time_to_ready"));
			this.order.setStore_id(jsonObj.getInt("store_id"));
			this.order.setState(jsonObj.getString("state"));
			this.order.setCreatedAt(dateFormat.parse(jsonObj.getString("created_at")));
			this.order.setTotal(jsonObj.getDouble("total"));
		
			
			JSONArray lineItemsArr = jsonObj.getJSONArray("line_items");
			LineItemVo[] lineItems = new LineItemVo[lineItemsArr.length()];
			for(int i = 0; i<lineItemsArr.length(); i++){
				JSONObject lineItemObj = lineItemsArr.getJSONObject(i);
				LineItemVo lineItem = new LineItemVo();
				lineItem.setId(lineItemObj.getInt("id"));
				lineItem.setName(lineItemObj.getString("name"));
				lineItem.setOption_values(lineItemObj.getString("option_values"));
				lineItem.setOrder_id(lineItemObj.getInt("order_id"));
				lineItem.setQuantity(lineItemObj.getInt("quantity"));
				lineItem.setPrice(lineItemObj.getDouble("price"));
				lineItem.setSku(lineItemObj.getString("sku"));
				lineItem.setSpecial_instructions(lineItemObj.getString("special_instructions"));
				lineItem.setVariant_id(lineItemObj.getInt("variant_id"));
				lineItems[i] = lineItem;
			}
			
			this.order.setLineItems(lineItems);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch(ParseException pe){
			pe.printStackTrace();
		}	
		
		return true;
	}
}