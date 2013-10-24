package com.vosto.customer.accounts.services;

import org.apache.http.StatusLine;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class AuthenticationService extends RestService {
	
	private String email;
	private String pin;
	
	public AuthenticationService(OnRestReturn listener, VostoBaseActivity activity){
		super(SERVER_URL + "/customers/authenticate", RequestMethod.POST, ResultType.AUTHENTICATE_CUSTOMER, listener, activity);
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
			root.put("authentication_token", "DXTTTTED2ASDBSD3");
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
    "authentication_token": "DXTTTTED2ASDBSD3",
    "email": "test@gmail.com",
    "pin": "123456"
}
}
		 * 
		 * 
		 */
	}
	
	@Override
	protected AuthenticateResult getRestResult(StatusLine statusLine, String responseJson){
		AuthenticateResult result = new AuthenticateResult(200, responseJson);
		result.processJsonAndPopulate();
		if(result.wasAuthenticationSuccessful()){
			result.getCustomer().user_pin = this.pin;
		}
		return result;
	}

}