package com.vosto.customer.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vosto.customer.services.vos.CreateAccountResponseWrapper;

public class CreateAccountResult extends RestResult implements IRestResult {
	
	 private CreateAccountResponseWrapper responseWrapper;
	 private boolean accountCreated;
	 private String errorMessage;
	 private String userPin;
	
	 
	 public CreateAccountResult(){
		 super();
		 this.accountCreated = false;
		 this.errorMessage = "";
	 }
	 
	 public CreateAccountResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 public boolean wasAccountCreated(){
		 return this.accountCreated;
	 }
	 
	 public String getErrorMessage(){
		 return this.errorMessage;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		Log.d("json", this.getResponseJson());
		Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		this.responseWrapper = gson.fromJson(this.getResponseJson(), CreateAccountResponseWrapper.class);
	//	Log.d("user", "The ID: " + this.responseWrapper.customer.id);
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(this.getResponseJson());
			if(jsonObj.isNull("detail")){
				this.accountCreated = true;
			}else{
				try{
				
					if(!jsonObj.isNull("detail")){
						this.errorMessage = jsonObj.getString("detail");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		return true;
	}
	
	public CreateAccountResponseWrapper getAccountResponseWrapper(){
		return this.responseWrapper;
	}

	public String getUserPin() {
		return userPin;
	}

	public void setUserPin(String userPin) {
		this.userPin = userPin;
	}
	
	
}