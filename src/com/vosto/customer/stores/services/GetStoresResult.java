package com.vosto.customer.stores.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;

public class GetStoresResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private StoreVo[] stores;
	 
	 public GetStoresResult(){
		 super();
	 }
	 
	 public GetStoresResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public StoreVo[] getStores(){
		 return this.stores;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.stores = new StoreVo[this.jsonArr.length()];
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONObject(i).getJSONObject("store");
				StoreVo currentStore = new StoreVo();
				currentStore.setUniqueId(jsonObj.getString("unique_id"));
				currentStore.setName(jsonObj.getString("store_name"));
				currentStore.setDescription(jsonObj.getString("store_description"));
				currentStore.setAddress(jsonObj.getString("address"));
				currentStore.setEmail(jsonObj.getString("email"));
				currentStore.setManagerContact(jsonObj.getString("manager_contact"));
				currentStore.setUrl(jsonObj.getString("url"));
                currentStore.setStoreImage(jsonObj.getString("store_image"));
                currentStore.setIsOnline(jsonObj.getBoolean("is_online"));
                currentStore.setLatitude(jsonObj.getDouble("latitude"));
                currentStore.setLongitude(jsonObj.getDouble("longitude"));
                currentStore.setCanDeliver(jsonObj.getBoolean("can_deliver"));
				currentStore.setId(jsonObj.getInt("id"));
				this.stores[i] = currentStore;
			}
			return true;
		}catch(JSONException e){
			// Maybe it's a single store, so try that:
			Log.d("ARR", "Array exception. trying single object...");
			return processJsonAndPopulateSingleStore();
		}
	}
	
	public boolean processJsonAndPopulateSingleStore(){
		try{
			this.stores = new StoreVo[1];
			JSONObject jsonObj = new JSONObject(this.getResponseJson());
			StoreVo currentStore = new StoreVo();
			currentStore.setUniqueId(jsonObj.getString("unique_id"));
			currentStore.setName(jsonObj.getString("store_name"));
			currentStore.setDescription(jsonObj.getString("store_description"));
			currentStore.setAddress(jsonObj.getString("address"));
			currentStore.setEmail(jsonObj.getString("email"));
			currentStore.setManagerContact(jsonObj.getString("manager_contact"));
			currentStore.setUrl(jsonObj.getString("url"));
            currentStore.setStoreImage(jsonObj.getString("store_image"));
            currentStore.setLatitude(jsonObj.getDouble("latitude"));
            currentStore.setLongitude(jsonObj.getDouble("longitude"));
            currentStore.setIsOnline(jsonObj.getBoolean("is_online"));
            currentStore.setCanDeliver(jsonObj.getBoolean("can_deliver"));
			currentStore.setId(jsonObj.getInt("id"));
			this.stores[0] = currentStore;
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
		
	}
	
}