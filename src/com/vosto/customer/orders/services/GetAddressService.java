package com.vosto.customer.orders.services;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetAddressService extends RestService {
	
	private double latitude;
	private double longitude;
	private VostoBaseActivity context;
	
	public GetAddressService(OnRestReturn listener, VostoBaseActivity context, double latitude, double longitude){
		super(SERVER_URL + "/addresses/get_address", RequestMethod.POST, ResultType.GET_ADDRESS, listener, context);
		this.context = context;
		this.latitude = latitude;
		this.longitude = longitude;
	}


	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("latitude", this.latitude);
			root.put("longitude", this.longitude);
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
	}
	
	
	
	
}