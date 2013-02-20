package com.vosto.customer.services;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationService extends RestService {
	
	private String authToken;
	private String email;
	private String pin;
	
	public AuthenticationService(OnRestReturn listener){
		super("http://107.22.211.58:9000/api/v1/customers/authenticate", RequestMethod.POST, ResultType.AUTHENTICATE_CUSTOMER, listener);
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", "CXTTTTED2ASDBSD4");
			root.put("email", this.email);
			root.put("pin", this.pin);
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
		
		/*
		 * 
	{
    "authentication_token": "CXTTTTED2ASDBSD3",
    "email": "test@gmail.com",
    "pin": "123456"
}
}
		 * 
		 * 
		 */
	}
	
	
	
}