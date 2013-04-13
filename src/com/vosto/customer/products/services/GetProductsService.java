package com.vosto.customer.products.services;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.products.vos.VariantVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetProductsService extends RestService {
	
	public GetProductsService(OnRestReturn listener, VostoBaseActivity context){
		super(SERVER_URL + "/products", RequestMethod.GET, ResultType.GET_PRODUCTS, listener, context);
	}
	
	public GetProductsService(OnRestReturn listener, VostoBaseActivity context, int taxonId){
		super(SERVER_URL + "/taxons/"+taxonId+"/products", RequestMethod.GET, ResultType.GET_PRODUCTS, listener, context);
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
			
				for(int i = 0; i<products.length; i++){
					this.httpGet = new HttpGet(SERVER_URL + "/products/" + products[i].getId()+"/variants");
					this.response = httpClient.execute(this.httpGet, localContext);
					httpEntity = response.getEntity();
					text = getASCIIContentFromEntity(httpEntity);
					
					products[i].setVariants(this.getVariantsFromJson(text));
				}
				
				productsResult.setProducts(products);
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
				
				
				String[] optionValueStrings = optionValuesObj.getString("option_values").split(",");
				ConcurrentHashMap<String, String> optionMap = new ConcurrentHashMap<String, String>();
				
				for(int j=0; j<optionValueStrings.length; j++){
					if(optionValueStrings[j].trim().equals("")){
						continue;
					}
				
					String[] optionNameAndValue = optionValueStrings[j].trim().split(":");
			
					if(optionNameAndValue.length != 2){
						continue;
					}
					
					optionMap.put(optionNameAndValue[0], optionNameAndValue[1].trim().toLowerCase(Locale.US));
				}
				
				variant.setOptionValueMap(optionMap);
				
				variants[i] = variant;
				
			}
			
			return variants;
		
		}catch(JSONException e){
			e.printStackTrace();
			return new VariantVo[0];
		}
	}

}