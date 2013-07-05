package com.vosto.customer.orders.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class GetAddressResult extends RestResult implements IRestResult {
	
	private String address;
	private String zipcode;
	private String city;
	private String country;
	
	 public GetAddressResult(){
		 super();
	 }
	 
	 public GetAddressResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	public String getAddress() {
		return address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public boolean processJsonAndPopulate(){
		try{
			
			Log.d("ADDR", "Get address response JSON: " + this.getResponseJson());
	        
	        JSONObject addressObj = new JSONObject(this.getResponseJson());
	        
	        this.address = addressObj.getString("address");
	        this.zipcode = addressObj.getString("zipcode");
	        this.city = addressObj.getString("city");
	        this.country = addressObj.getString("country");	
	        
	    	return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}