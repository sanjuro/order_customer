package com.vosto.customer.services;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.services.vos.OptionValueVo;
import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.VariantVo;

public class GetProductsService extends RestService {
	
	public GetProductsService(OnRestReturn listener){
		super("http://107.22.211.58:9000/api/v1/products", RequestMethod.GET, ResultType.GET_PRODUCTS, listener);
	}
	
	public GetProductsService(OnRestReturn listener, int taxonId){
		super("http://107.22.211.58:9000/api/v1/taxons/"+taxonId+"/products", RequestMethod.GET, ResultType.GET_PRODUCTS, listener);
	}
	
	@Override
	protected RestResult doInBackground(Void... params) {
		
		String text = null;
		
			// GET request:
			try {
				this.httpGet = new HttpGet(this.url);
				this.response = httpClient.execute(this.httpGet, localContext);
				HttpEntity entity = response.getEntity();
				text = getASCIIContentFromEntity(entity);
				GetProductsResult productsResult =  (GetProductsResult)this.getRestResult(response.getStatusLine(), text);
				productsResult.processJsonAndPopulate();
				
				// For each product, get the variants:
				ProductVo[] products = productsResult.getProducts();
				HttpEntity httpEntity;
				Log.d("VAR", "Getting variants for " + products.length + " products.");
				for(int i = 0; i<products.length; i++){
					Log.d("VAR", "Variants url: " + "http://107.22.211.58:9000/api/v1/products/" + products[i].getId()+"/variants");
					this.httpGet = new HttpGet("http://107.22.211.58:9000/api/v1/products/" + products[i].getId()+"/variants");
					this.response = httpClient.execute(this.httpGet, localContext);
					httpEntity = response.getEntity();
					text = getASCIIContentFromEntity(httpEntity);
					Log.d("VAR", "Variants response: " + text);
					products[i].setVariants(this.getVariantsFromJson(text));
					Log.d("VAR", "Product " + i + " variants: " + products[i].getVariants().length);
				}
				return productsResult;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}	
			
			
	}
	
	private VariantVo[] getVariantsFromJson(String variantJson){
		try{
			JSONArray outerArr = new JSONArray(variantJson);
			VariantVo[] variants = new VariantVo[outerArr.length()];
			for(int i = 0; i<outerArr.length(); i++){
				JSONArray variantArr = outerArr.getJSONArray(i);
				JSONObject variantObj = variantArr.getJSONObject(0).getJSONObject("variant");
				JSONObject optionValuesObj = variantArr.getJSONObject(1);
				
				VariantVo variant = new VariantVo();
				variant.setId(variantObj.getInt("id"));
				variant.setPrice(variantObj.getDouble("price"));
				variant.setMaster(variantObj.getBoolean("is_master"));
				variant.setSku(variantObj.getString("sku"));
				variant.setPosition(variantObj.getInt("position"));
				variant.setProduct_id(variantObj.getInt("product_id"));
				
				OptionValueVo optionValue = new OptionValueVo();
				optionValue.setName(optionValuesObj.getString("option_values"));
				optionValue.setPresentation(optionValue.getName());
				
				OptionValueVo[] optionValues = new OptionValueVo[1];
				optionValues[0] = optionValue;
				variant.setOptionValues(optionValues);
				
				variants[i] = variant;
				
			}
			
			return variants;
		
		}catch(JSONException e){
			e.printStackTrace();
			return new VariantVo[0];
		}
	}

}