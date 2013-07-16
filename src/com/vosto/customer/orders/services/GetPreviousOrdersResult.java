package com.vosto.customer.orders.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.Collections;

import org.joda.money.Money;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.cart.vos.LineItemVo;
import com.vosto.customer.orders.vos.AddressVo;
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

            List orders = listFromJsonSorted(outerObj);

	        OrderVo currentOrder = null;
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
//	        while(keys.hasNext() ){
//	            String key = (String)keys.next();

            for( Object order: orders ) {
	            if(order instanceof JSONObject){
	            	JSONObject currentObj = (JSONObject)order;
	            	currentOrder = new OrderVo();
	            	currentOrder.setNumber(currentObj.getString("number"));
	            	if(!currentObj.isNull("store_order_number")){
	            		currentOrder.setStoreOrderNumber(currentObj.getString("store_order_number"));
	            	}
	            	if(!currentObj.isNull("time_to_ready")){
	            		currentOrder.setTimeToReady(currentObj.getString("time_to_ready"));
	            	}
	            	if(!currentObj.isNull("adjustment_total")){
	            		currentOrder.setAdjustmentTotal(Money.parse("ZAR " + currentObj.getDouble("adjustment_total")));
	    	        }
	            	currentOrder.setCreatedAt(dateFormat.parse(currentObj.getString("created_at")));
	            	currentOrder.setTotal(Money.parse("ZAR " + currentObj.getDouble("total")));
	            	currentOrder.setStore_id(currentObj.getInt("store_id"));
	            	currentOrder.setState(currentObj.getString("state"));
	            	
	            	// Delivery Address:
	    	        if(!currentObj.isNull("address")){
	    	        	AddressVo address = new AddressVo();
	    	        	JSONObject addressObj = currentObj.getJSONObject("address");
	    	        	address.setAddress1(addressObj.getString("address1"));
	    	        	address.setAddress2(addressObj.getString("address2"));
	    	        	address.setSuburb(addressObj.getString("suburb"));
	    	        	address.setSuburb_id(!addressObj.isNull("suburb_id") ? addressObj.getInt("suburb_id") : 1);
	    	        	address.setCity(addressObj.getString("city"));
	    	        	address.setState(addressObj.getString("state"));
	    	        	address.setCountry(addressObj.getString("country"));
	    	        	address.setZipcode(addressObj.getString("zip"));
	    	        	currentOrder.setDeliveryAddress(address);
	    	        }
	            	
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

    public static List listFromJsonSorted(JSONObject json) {
        if (json == null) return null;
        SortedMap map = new TreeMap(Collections.reverseOrder());
        Iterator i = json.keys();
        while (i.hasNext()) {
            try {
                String key = i.next().toString();
                JSONObject j = json.getJSONObject(key);
                map.put(key, j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new LinkedList(map.values());
    }
	
}