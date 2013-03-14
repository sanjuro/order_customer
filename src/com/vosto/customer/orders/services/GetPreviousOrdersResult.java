package com.vosto.customer.orders.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.joda.money.Money;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.cart.vos.LineItemVo;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

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
			Log.d("ORD", "prev Orders response json: " + this.getResponseJson());
			
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
	            	currentOrder.setTotal(Money.parse("ZAR " + currentObj.getDouble("total")));
	            	currentOrder.setStore_id(currentObj.getInt("store_id"));
	            	
	            	//Add line items:
	            	JSONArray lineItemsArr = currentObj.getJSONArray("line_items");
	    			LineItemVo[] lineItems = new LineItemVo[lineItemsArr.length()];
	    			for(int i = 0; i<lineItemsArr.length(); i++){
	    				JSONObject lineItemObj = lineItemsArr.getJSONObject(i);
	    				LineItemVo lineItem = new LineItemVo();
	    				lineItem.setId(lineItemObj.getInt("id"));
	    				lineItem.setName(lineItemObj.getString("name"));
	    				lineItem.setOption_values(lineItemObj.getString("option_values"));
	    				lineItem.setOrder_id(lineItemObj.getInt("order_id"));
	    				lineItem.setQuantity(lineItemObj.getInt("quantity"));
	    				lineItem.setPrice(lineItemObj.getDouble("price"));
	    				lineItem.setSku(lineItemObj.getString("sku"));
	    				lineItem.setSpecial_instructions(lineItemObj.getString("special_instructions"));
	    				lineItem.setVariant_id(lineItemObj.getInt("variant_id"));
	    				lineItems[i] = lineItem;
	    			}
	    			
	    			currentOrder.setLineItems(lineItems);
	            	
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