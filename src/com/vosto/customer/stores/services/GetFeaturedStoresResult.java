package com.vosto.customer.stores.services;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;

public class GetFeaturedStoresResult extends RestResult implements IRestResult {

    private JSONArray jsonArr;
    private StoreVo[] stores;

    public GetFeaturedStoresResult(){
        super();
    }

    public GetFeaturedStoresResult(int responseCode, String responseJson){
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
                currentStore.setIsOnline(jsonObj.getBoolean("is_online"));
                currentStore.setUrl(jsonObj.getString("url"));
                currentStore.setStoreImage(jsonObj.getString("store_image"));
                currentStore.setId(jsonObj.getInt("id"));
                currentStore.setUniqueId(jsonObj.getString("unique_id"));
                currentStore.setDistance(-1);
                this.stores[i] = currentStore;
            }

            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
    }

}