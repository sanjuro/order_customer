package com.vosto.customer.orders.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.orders.vos.AddressVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class GetAddressResult extends RestResult implements IRestResult {
	
	private String addressString;
	private AddressVo address;
	private boolean success;
	
	 public GetAddressResult(){
		 super();
		 this.success = false;
	 }
	 
	 public GetAddressResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	public AddressVo getAddress() {
		return address;
	}
	
	public boolean wasSuccessful(){
		return this.success;
	}

	

	@Override
	public boolean processJsonAndPopulate(){
		try{
			
			Log.d("ADDR", "Get address response JSON: " + this.getResponseJson());
	        
	        JSONObject addressObj = new JSONObject(this.getResponseJson());
	        
	        this.addressString = addressObj.getString("address");
	        this.address = new AddressVo(this.getResponseJson());
	        
	        this.success = true;
	    	return true;
		}catch(JSONException e){
			e.printStackTrace();
			this.success = false;
			return false;
		}
	}
	
}