package com.vosto.customer.services;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordResult extends RestResult implements IRestResult {
	
	private String responseMessage;
	 
	 public ResetPasswordResult(){
		 super();
	 }
	 
	 public ResetPasswordResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	
	 public String getResponseMessage(){
		 return this.responseMessage;
	 }
	 
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.responseMessage = new JSONObject(this.getResponseJson()).getString("success");
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}