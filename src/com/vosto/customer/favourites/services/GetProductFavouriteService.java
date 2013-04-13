package com.vosto.customer.favourites.services;

import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.vos.OptionValueVo;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.products.vos.VariantVo;
import com.vosto.customer.services.*;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.utils.Constants;
import com.vosto.customer.utils.ProductFavouritesManager;

import java.util.List;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetProductFavouriteService extends RestService {

    private ProductFavouritesManager favourites;

    public GetProductFavouriteService(OnRestReturn listener, VostoBaseActivity context) {
        super(SERVER_URL + "/products/by_ids", RequestMethod.POST, ResultType.GET_PRODUCTS, listener, context);
        favourites = new ProductFavouritesManager(context);
    }

    @Override
    public String getRequestJson() {
        try {
            JSONObject object = new JSONObject();
            object.put("product_ids", new JSONArray(favourites.load()));
            String json = object.toString();
            Log.d(Constants.TAG, json);
            return json;
        } catch (JSONException je) {
            Log.e(Constants.TAG, "Could not get json", je);
            return super.getRequestJson();
        }
    }

//    @Override
//    protected RestResult doInBackground(Void... params) {
//
//        String text = null;
//
//        // GET request:
//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            this.httpPost = new HttpPost(this.url);
//
//            JSONObject object = new JSONObject();
//            object.put("product_ids", new JSONArray(favourites.load()));
//            String json = object.getString("product_ids").toString();
//
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//            nameValuePairs.add(new BasicNameValuePair("product_ids", json));
//            this.httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//            this.response = httpClient.execute(this.httpPost, localContext);
//            HttpEntity entity = response.getEntity();
//            text = getASCIIContentFromEntity(entity);
//            Log.d("VAR", "Response " + text);
//            GetProductsResult productsResult =  (GetProductsResult)this.getRestResult(response.getStatusLine(), text);
//
//            productsResult.processJsonAndPopulate();
//
//            // For each product, get the variants:
//            ProductVo[] products = productsResult.getProducts();
//
//            HttpEntity httpEntity;
//            Log.d("VAR", "Getting variants for " + products.length + " products.");
//            for(int i = 0; i<products.length; i++){
//                Log.d("VAR", "Variants url: " + SERVER_URL + "/products/" + products[i].getId()+"/variants");
//                this.httpGet = new HttpGet(SERVER_URL + "/products/" + products[i].getId()+"/variants");
//                this.response = httpClient.execute(this.httpGet, localContext);
//                httpEntity = response.getEntity();
//                text = getASCIIContentFromEntity(httpEntity);
//                Log.d("VAR", "Variants response: " + text);
//                products[i].setVariants(this.getVariantsFromJson(text));
//                Log.d("VAR", "Product " + i + " variants: " + products[i].getVariants().length);
//            }
//            return productsResult;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }


    @Override
    protected RestResult doInBackground(Void... params) {

        String text = null;
        if(this.requestMethod == RequestMethod.POST){
            Log.d("VAR", "My doInBackground");
            try {
                this.httpPost = new HttpPost(this.url);
                if(this.nameValuePairs.size() > 0){
                    this.httpPost.setEntity(new UrlEncodedFormEntity(this.nameValuePairs));
                }else if(this.getRequestJson() != ""){
                    StringEntity entity = new StringEntity(this.getRequestJson(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    this.httpPost.setEntity(entity);
                }
                this.response = httpClient.execute(this.httpPost, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                GetProductsResult productsResult =  (GetProductsResult)this.getRestResult(response.getStatusLine(), text);

                productsResult.processJsonAndPopulate();

                ProductVo[] products = productsResult.getProducts();

                Log.d("VAR", "Getting variants for " + products.length + " products.");
                for(int i = 0; i<products.length; i++){
                    Log.d("VAR", "Variants url: " + SERVER_URL + "/products/" + products[i].getId()+"/variants");
                    this.httpGet = new HttpGet(SERVER_URL + "/products/" + products[i].getId()+"/variants");
                    this.response = httpClient.execute(this.httpGet, localContext);
                    HttpEntity httpEntity = response.getEntity();
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
        }else{
            // GET request:
            try {
                this.httpGet = new HttpGet(this.url);
                this.response = httpClient.execute(this.httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                return this.getRestResult(response.getStatusLine(), text);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

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
