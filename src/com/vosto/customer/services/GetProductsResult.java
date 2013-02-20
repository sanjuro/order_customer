package com.vosto.customer.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.services.vos.ProductVo;

public class GetProductsResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private ArrayList<ProductVo> products;
	private int storeId;
	
	 public GetProductsResult(){
		 super();
	 }
	 
	 public GetProductsResult(int responseCode, String responseJson){
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
	 
	 public ArrayList<ProductVo> getProducts(){
		 ArrayList<ProductVo> requestedProducts = new ArrayList<ProductVo>();
		 for(int i = 0; i<this.products.size(); i++){
			 if(this.products.get(i).getStoreId() == this.storeId){
				 requestedProducts.add(this.products.get(i));
			 }
		 }
		 return requestedProducts;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.products = new ArrayList<ProductVo>();
		
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONObject(i).getJSONObject("product");
				
				ProductVo currentProduct = new ProductVo();
				currentProduct.setName(jsonObj.getString("name"));
				currentProduct.setDescription(jsonObj.getString("description"));
				currentProduct.setPrice(jsonObj.getDouble("price"));
				currentProduct.setStoreId(jsonObj.getInt("store_id"));
				currentProduct.setId(jsonObj.getInt("id"));
				
				this.products.add(currentProduct);
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}