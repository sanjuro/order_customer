package com.vosto.customer.accounts.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class ResetPasswordService extends RestService {
	private String email;
	
	public ResetPasswordService(OnRestReturn listener, VostoBaseActivity context, String email){
		super(SERVER_URL + "/customers/reset_pin", RequestMethod.POST, ResultType.RESET_PIN, listener, context);
		this.email = email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}


	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", "DXTTTTED2ASDBSD3");
			root.put("email", this.email);
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
	}
	


	
	
	
	
	
	
}