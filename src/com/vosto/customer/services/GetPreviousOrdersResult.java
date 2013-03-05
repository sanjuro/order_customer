package com.vosto.customer.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.services.vos.OrderVo;

public class GetPreviousOrdersResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private OrderVo[] orders;
	
	 public GetPreviousOrdersResult(){
		 super();
	 }
	 
	 public GetPreviousOrdersResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 
	
	public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public OrderVo[] getOrders(){
		 return this.orders;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			JSONObject outerObj = new JSONObject(this.getResponseJson());
			
			ArrayList<OrderVo> ordersList = new ArrayList<OrderVo>();
			
	        Iterator<?> keys = outerObj.keys();

	        OrderVo currentOrder = null;
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	        while(keys.hasNext() ){
	            String key = (String)keys.next();
	            if(outerObj.get(key) instanceof JSONObject){
	            	JSONObject currentObj = (JSONObject)outerObj.get(key);
	            	currentOrder = new OrderVo();
	            	currentOrder.setNumber(currentObj.getString("number"));
	            	currentOrder.setCreatedAt(dateFormat.parse(currentObj.getString("created_at")));
	            	ordersList.add(currentOrder);
	            }
	        }
		
	        this.orders = new OrderVo[ordersList.size()];
			for(int i = 0; i<ordersList.size(); i++){
				this.orders[i] = ordersList.get(i);
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}catch(ParseException e){
			e.printStackTrace();
			return false;
		}
	}
	
}