package com.vosto.customer.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.services.vos.TaxonVo;

public class GetTaxonsResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private ArrayList<TaxonVo> taxons;
	private int storeId;
	
	 public GetTaxonsResult(){
		 super();
	 }
	 
	 public GetTaxonsResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 
	 public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public ArrayList<TaxonVo> getTaxons(){
		
		 return this.taxons;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.taxons = new ArrayList<TaxonVo>();
		
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONObject(i);
				
				TaxonVo currentTaxon = new TaxonVo();
				currentTaxon.setName(jsonObj.getString("name"));
				currentTaxon.setParentId(jsonObj.getInt("parent_id"));
				currentTaxon.setId(jsonObj.getInt("id"));
				
				this.taxons.add(currentTaxon);
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}