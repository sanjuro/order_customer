package com.vosto.customer.orders.services;

import org.joda.money.Money;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class GetDeliveryPriceResult extends RestResult implements IRestResult {
	
	
	private Money deliveryPrice;
	
	 
	 public GetDeliveryPriceResult(){
		 super();
		 this.deliveryPrice = null;
	 }
	 
	 public GetDeliveryPriceResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	public Money getDeliveryPrice() {
		return deliveryPrice;
	}
	
	@Override
	public boolean processJsonAndPopulate(){
		Log.d("delivery price response", this.getResponseJson());

		JSONObject jsonObj;
		try {
			this.deliveryPrice = Money.parse("ZAR " + this.getResponseJson());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}