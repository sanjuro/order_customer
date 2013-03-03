package com.vosto.customer.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PlaceOrderResult extends RestResult implements IRestResult {
	
	
	 private boolean orderCreated;
	 private String errorMessage;
	 
	 private String orderNumber;
	
	 
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
	 
	 public String getOrderNumber(){
		 return this.orderNumber;
	 }
	 
	 public String getErrorMessage(){
		 return this.errorMessage;
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
			
			this.orderCreated = true;
			this.orderNumber = jsonObj.getString("number");
			
		
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		return true;
	}
}