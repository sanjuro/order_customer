package com.vosto.customer.stores.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.SuburbVo;

public class GetSuburbsResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private SuburbVo[] suburbs;
	 
	 public GetSuburbsResult(){
		 super();
	 }
	 
	 public GetSuburbsResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public SuburbVo[] getSuburbs(){
		 return this.suburbs;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.suburbs = new SuburbVo[this.jsonArr.length()];
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONArray(i).getJSONObject(0);
				SuburbVo currentSuburb = new SuburbVo();
				currentSuburb.setId(jsonObj.getInt("id"));
				currentSuburb.setName(jsonObj.getString("name"));
				this.suburbs[i] = currentSuburb;
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
	
}