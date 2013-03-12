package com.vosto.customer.stores.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;

public class SearchResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private StoreVo[] stores;
	private boolean hasLocation;
	 
	 public SearchResult(){
		 super();
		 this.hasLocation = false;
	 }
	 
	 public SearchResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
		 this.hasLocation = false;
	 }
	 
	 public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public StoreVo[] getStores(){
		 return this.stores;
	 }
	 
	 public boolean hasLocation(){
		 return this.hasLocation;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.hasLocation = false;
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.stores = new StoreVo[this.jsonArr.length()];
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONObject(i);
				StoreVo currentStore = new StoreVo();
				currentStore.setName(jsonObj.getString("store_name"));
				currentStore.setDescription(jsonObj.getString("store_description"));
				currentStore.setAddress(jsonObj.getString("address"));
				currentStore.setEmail(jsonObj.getString("email"));
				currentStore.setManagerContact(jsonObj.getString("manager_contact"));
				currentStore.setUrl(jsonObj.getString("url"));
				currentStore.setId(jsonObj.getInt("id"));
				currentStore.setUniqueId(jsonObj.getString("unique_id"));
				if(jsonObj.has("distance")){
					currentStore.setDistance(jsonObj.getDouble("distance"));
					this.hasLocation = true;
				}else{
					currentStore.setDistance(-1);
				}
				this.stores[i] = currentStore;
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}