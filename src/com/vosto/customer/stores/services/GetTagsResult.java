package com.vosto.customer.stores.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreTagVo;


public class GetTagsResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private StoreTagVo[] tags;
	 
	 public GetTagsResult(){
		 super();
	 }
	 
	 public GetTagsResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public StoreTagVo[] getTags(){
		 return this.tags;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			Log.d("TAG", "Tag json: " + this.getResponseJson());
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.tags = new StoreTagVo[this.jsonArr.length()];
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONObject(i);
				StoreTagVo currentTag = new StoreTagVo();
				currentTag.setId(jsonObj.getInt("id"));
				currentTag.setTitle(jsonObj.getString("title"));
				this.tags[i] = currentTag;
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}