package com.vosto.customer.accounts.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.VostoCustomerApp;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class RegisterDeviceService extends RestService {
	private VostoCustomerApp context;
	private String gcmId; //The id assigned and returned by gcm when registering the device with them
	
	public RegisterDeviceService(OnRestReturn listener, VostoCustomerApp context, String gcmId){
		super("http://107.22.211.58:9000/api/v1/devices/register", RequestMethod.POST, ResultType.REGISTER_DEVICE, listener);
		this.context = context;
		this.gcmId = gcmId;
	}
	
	public void setGcmId(String gcmId){
		this.gcmId = gcmId;
	}


	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", this.context.getAuthenticationToken());
			JSONObject device = new JSONObject();
			device.put("device_type", "android");
			device.put("device_identifier", this.gcmId);
			root.put("device", device);
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
	}
	
	/* Request format:
	 * {
    "authentication_token": "CXTTTTED2ASDBSD3",
    "device": {
        "device_type": "blackberry",
        "device_identifier": "NFW3-44LG-E557-E",
        "device_token": "1234123",
    }
}
	 */


}