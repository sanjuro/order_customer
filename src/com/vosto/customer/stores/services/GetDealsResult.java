package com.vosto.customer.stores.services;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.DealVo;

public class GetDealsResult extends RestResult implements IRestResult {

    private JSONArray jsonArr;
    private DealVo[] deals;

    public GetDealsResult(){
        super();
    }

    public GetDealsResult(int responseCode, String responseJson){
        super(responseCode, responseJson);
    }

    public JSONArray getJSONArray(){
        return this.jsonArr;
    }

    public DealVo[] getDeals(){
        return this.deals;
    }

    @Override
    public boolean processJsonAndPopulate(){
        try{

            this.jsonArr = new JSONArray(this.getResponseJson());
            this.deals = new DealVo[this.jsonArr.length()];
            for(int i = 0; i<this.jsonArr.length(); i++){
                JSONObject jsonObj = this.jsonArr.getJSONObject(i);
                DealVo currentDeal = new DealVo();
                currentDeal.setName(jsonObj.getString("deal_name"));
                currentDeal.setDescription(jsonObj.getString("deal_description"));
                currentDeal.setDealableType(jsonObj.getString("dealable_type"));
                currentDeal.setDealableId(jsonObj.getInt("dealable_id"));
                currentDeal.setDealImage(jsonObj.getString("deal_image"));
                currentDeal.setIsActive(jsonObj.getBoolean("is_active"));
                this.deals[i] = currentDeal;
            }
            Log.d("DEA", "Get Deal Data");
            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
    }

}