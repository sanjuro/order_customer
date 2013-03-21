package com.vosto.customer.accounts.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class RegisterDeviceResult extends RestResult implements IRestResult {
	
	private boolean successful;
	 
	 public RegisterDeviceResult(){
		 super();
	 }
	 
	 public RegisterDeviceResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	
	 public boolean wasSuccessful(){
		 return this.successful;
	 }
	 
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			Log.d("GCM", "Register response: " + this.getResponseJson());
			JSONObject responseObj = new JSONObject(this.getResponseJson());
			// Assume the call was successful if we have a user set in the response:
			this.successful = responseObj.has("user");
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}