package com.vosto.customer.accounts.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.accounts.vos.CustomerVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class AuthenticateResult extends RestResult implements IRestResult {
	
	private JSONObject jsonObj;
	private CustomerVo customer;
	private boolean authenticationSuccessful;
	private String errorMessage;
	 
	 public AuthenticateResult(){
		 super();
	 }
	 
	 public AuthenticateResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 public JSONObject getJSONObject(){
		 return this.jsonObj;
	 }
	 
	 public CustomerVo getCustomer(){
		 return this.customer;
	 }
	 
	 public boolean wasAuthenticationSuccessful(){
		 return this.authenticationSuccessful;
	 }
	 
	 public String getErrorMessage(){
		 return this.errorMessage;
	 }
	 
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.jsonObj = new JSONObject(this.getResponseJson());
			if(!this.jsonObj.isNull("error")){
				this.authenticationSuccessful = false;
				this.errorMessage = this.jsonObj.getString("detail");
				return true;
			}
			
			this.authenticationSuccessful = true;
			this.customer = new CustomerVo();
			this.customer.authentication_token = this.jsonObj.getString("authentication_token");
			this.customer.full_name = this.jsonObj.getString("full_name");
			this.customer.first_name = this.jsonObj.getString("first_name");
            this.customer.email = this.jsonObj.getString("email");
            this.customer.mobile_number = this.jsonObj.getString("mobile_number");
			
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}