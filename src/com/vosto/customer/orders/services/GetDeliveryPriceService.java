package com.vosto.customer.orders.services;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.vos.AddressVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetDeliveryPriceService extends RestService {
	
	private int storeId;
	private AddressVo address;
	private VostoBaseActivity context;
	
	public GetDeliveryPriceService(OnRestReturn listener, VostoBaseActivity context, int storeId, AddressVo address){
		super(SERVER_URL + "/addresses/get_delivery_price", RequestMethod.POST, ResultType.GET_DELIVERY_PRICE, listener, context);
		this.context = context;
		this.storeId = storeId;
		this.address = address;
	}


	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("store_id", this.storeId);
			
			JSONArray addressArray = new JSONArray();
			addressArray.put(new JSONObject(this.address.toJson()));
			root.put("address", addressArray);
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
		
		/*
		 * {
    			"store_id": "15",
    			"address": {
        			"address1": "31 Ricketts Street",
        			"address2": "De Tyger",
        			"suburb_id": "3",
        			"city": "cape town",
        			"zipcode": "7500",
        			"country": "South Africa",
        			"latitude": "-33.960905",
        			"longitude": "18.470102"
    		}
		}
		 */
	}
	
	
	
	
}