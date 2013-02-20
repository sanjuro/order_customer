package com.vosto.customer.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.vosto.customer.services.vos.StoreVo;

public class SearchResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private StoreVo[] stores;
	 
	 public SearchResult(){
		 super();
	 }
	 
	 public SearchResult(int responseCode, String responseJson){
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
				JSONObject jsonObj = this.jsonArr.getJSONObject(i);
				StoreVo currentStore = new StoreVo();
				currentStore.setName(jsonObj.getString("store_name"));
				currentStore.setDescription(jsonObj.getString("store_description"));
				currentStore.setAddress(jsonObj.getString("address"));
				currentStore.setEmail(jsonObj.getString("email"));
				currentStore.setManagerContact(jsonObj.getString("manager_contact"));
				currentStore.setUrl(jsonObj.getString("url"));
				currentStore.setId(jsonObj.getInt("id"));
				this.stores[i] = currentStore;
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}