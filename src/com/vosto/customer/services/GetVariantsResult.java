package com.vosto.customer.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.services.vos.OptionValueVo;
import com.vosto.customer.services.vos.VariantVo;

public class GetVariantsResult extends RestResult implements IRestResult {
	
	private JSONArray jsonArr;
	private ArrayList<VariantVo> variants;
	private int productId;
	
	 public GetVariantsResult(){
		 super();
	 }
	 
	 public GetVariantsResult(int responseCode, String responseJson){
		 super(responseCode, responseJson);
	 }
	 
	 
	 public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public JSONArray getJSONArray(){
		 return this.jsonArr;
	 }
	 
	 public ArrayList<VariantVo> getVariants(){
		 return this.variants;
	 }
	 
	@Override
	public boolean processJsonAndPopulate(){
		try{
			this.jsonArr = new JSONArray(this.getResponseJson());
			this.variants = new ArrayList<VariantVo>();
		
			for(int i = 0; i<this.jsonArr.length(); i++){
				JSONArray variantArr = this.jsonArr.getJSONArray(i);
				JSONObject variantObj = variantArr.getJSONObject(0).getJSONObject("variant");
				JSONArray optionValuesArr = variantArr.getJSONObject(1).getJSONArray("option_values");
				
				
				VariantVo currentVariant = new VariantVo();
				currentVariant.setId(variantObj.getInt("id"));
				currentVariant.setPosition(variantObj.getInt("position"));
				currentVariant.setPrice(variantObj.getDouble("price"));
				currentVariant.setProduct_id(variantObj.getInt("product_id"));
				currentVariant.setSku(variantObj.getString("sku"));
				currentVariant.setMaster(variantObj.getBoolean("is_master"));
				
				OptionValueVo[] optionValues = new OptionValueVo[optionValuesArr.length()];
				
				for(int j = 0; j<optionValuesArr.length(); j++){
					JSONObject optionValueObj = optionValuesArr.getJSONObject(j).getJSONObject("option_value");
					OptionValueVo currentOptionValue = new OptionValueVo();
					currentOptionValue.setId(optionValueObj.getInt("id"));
					currentOptionValue.setName(optionValueObj.getString("name"));
					currentOptionValue.setOptionTypeId(optionValueObj.getInt("option_type_id"));
					currentOptionValue.setPresentation(optionValueObj.getString("presentation"));
					currentOptionValue.setPosition(optionValueObj.getInt("position"));
					optionValues[j] = currentOptionValue;
				}
				
				currentVariant.setOptionValues(optionValues);
				this.variants.add(currentVariant);
			}
			return true;
		}catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}
	
}