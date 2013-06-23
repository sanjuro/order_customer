package com.vosto.customer.accounts.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class ResetPasswordResult extends RestResult implements IRestResult {
	
	private String responseMessage;
	private boolean success;
	 
	 public ResetPasswordResult(){
		 super();
		 this.success = false;
	 }
	 
	 public ResetPasswordResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
		 this.success = false;
	 }
	 
	
	 public String getResponseMessage(){
		 return this.responseMessage;
	 }
	 
	 public boolean wasSuccessful(){
		 return this.success;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.responseMessage = new JSONObject(this.getResponseJson()).getString("success");
			this.success = true;
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			this.success = false;
			return false;
		}
	}
	
}