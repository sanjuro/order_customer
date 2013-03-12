package com.vosto.customer.products.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class GetProductsResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private ProductVo[] products;
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
	
	public ProductVo[] getProducts(){
		return this.products;
	}
	 
	 public ProductVo[] getProductsForStoreId(){
		 ArrayList<ProductVo> requestedProducts = new ArrayList<ProductVo>();
		 for(int i = 0; i<this.products.length; i++){
			 if(this.products[i].getStoreId() == this.storeId){
				 requestedProducts.add(this.products[i]);
			 }
		 }
		 
		 ProductVo[] requestedProductsArr = new ProductVo[requestedProducts.size()];
		 for(int j = 0; j<requestedProducts.size(); j++){
			 requestedProductsArr[j] = requestedProducts.get(j);
		 }
		 return requestedProductsArr;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
	
		// Skip if products are already set:
		if(this.products != null && this.products.length > 0 && this.products[0] != null){
			return true;
		}
		
		try{
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.products = new ProductVo[this.jsonArr.length()];
		
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONObject jsonObj = this.jsonArr.getJSONObject(i);
				
				ProductVo currentProduct = new ProductVo();
				currentProduct.setName(jsonObj.getString("name"));
				currentProduct.setDescription(jsonObj.getString("description"));
				currentProduct.setPrice(jsonObj.getDouble("price"));
				currentProduct.setStoreId(jsonObj.getInt("store_id"));
				currentProduct.setId(jsonObj.getInt("id"));
				
				this.products[i] = currentProduct;
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}