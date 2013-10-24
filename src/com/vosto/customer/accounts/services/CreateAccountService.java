package com.vosto.customer.accounts.services;

import java.util.Date;

import org.apache.http.StatusLine;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import android.util.Log;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;
import static com.vosto.customer.utils.CommonUtilities.SERVER_AUTHENTICATION_TOKEN;

public class CreateAccountService extends RestService {
	
	private String firstName;
	private String lastName;
	private String email;
    private String gender;
	private String birthDate;
	private String mobileNumber;
	private String userPin;
	
	public CreateAccountService(OnRestReturn listener, VostoBaseActivity activity){
		super(SERVER_URL + "/users/create_customer", RequestMethod.POST, ResultType.CREATE_CUSTOMER, listener, activity);
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
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
			root.put("authentication_token", SERVER_AUTHENTICATION_TOKEN);
			JSONObject user = new JSONObject();
			user.put("first_name", this.firstName);
			user.put("last_name", this.lastName);
			user.put("email", this.email);
			user.put("mobile_number", this.mobileNumber);
			user.put("birthday", this.birthDate);
			user.put("user_pin", this.userPin);
			root.put("user", user);
			// Log.d("jsontest", "Request JSON: " + root.toString());
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