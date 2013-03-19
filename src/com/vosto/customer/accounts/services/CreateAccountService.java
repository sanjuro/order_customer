package com.vosto.customer.accounts.services;

import java.util.Date;

import org.apache.http.StatusLine;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import android.util.Log;

public class CreateAccountService extends RestService {
	
	private String firstName;
	private String lastName;
	private String email;
	private Date birthDate;
	private String mobileNumber;
	private String userPin;
	
	public CreateAccountService(OnRestReturn listener){
		super("http://107.22.211.58:9000/api/v1/users/create_customer", RequestMethod.POST, ResultType.CREATE_CUSTOMER, listener);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getUserPin() {
		return userPin;
	}

	public void setUserPin(String userPin) {
		this.userPin = userPin;
	}
	
	
	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", "DXTTTTED2ASDBSD3");
			JSONObject user = new JSONObject();
			user.put("first_name", this.firstName);
			user.put("last_name", this.lastName);
			user.put("email", this.email);
			user.put("mobile_number", this.mobileNumber);
			user.put("birthday", this.birthDate);
			user.put("user_pin", this.userPin);
			root.put("user", user);
			Log.d("jsontest", "Request JSON: " + root.toString());
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
		
		/*
		 * 
		 * {
    "authentication_token": "CXTTTTED2ASDBSD3",
    "user": {
        "first_name": "Test",
        "last_name": "Customer",
        "email": "test@gmail.com",
        "mobile_number": "0833908314",
        "birthday": "1981-12-04",
        "user_pin": "12345"
    }
}
		 * 
		 * 
		 */
	}
	
	@Override
	protected CreateAccountResult getRestResult(StatusLine statusLine, String responseJson){
		CreateAccountResult result = new CreateAccountResult(200, responseJson);
		result.setUserPin(this.userPin);
		result.processJsonAndPopulate();	
		return result;
	}
	
	
	
}