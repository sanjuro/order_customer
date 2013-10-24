package com.vosto.customer.loyalties.services;

import android.util.Log;

import com.vosto.customer.loyalties.vos.LoyaltyVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.loyalties.vos.LoyaltyCardVo;
import com.vosto.customer.stores.vos.StoreVo;

import org.joda.money.Money;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/06
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetLoyaltyCardByIdResult extends RestResult implements IRestResult {

    private LoyaltyCardVo loyatlyCard;

    public GetLoyaltyCardByIdResult(int responseCode, String responseJson){
        super(responseCode, responseJson);
    }

    public LoyaltyCardVo getLoyaltyCard(){
        return this.loyatlyCard;
    }

    @Override
    public boolean processJsonAndPopulate(){
        try{

            if(this.getResponseJson().toLowerCase(Locale.getDefault()).contains("couldn't find loyalty card")){
                this.loyatlyCard = null;
                return true;
            }

            Log.d("LOYALTY", "Loyalty by id response json: " + this.getResponseJson());

            this.loyatlyCard = new LoyaltyCardVo();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

            JSONObject loyaltyCardObj = new JSONObject(this.getResponseJson());

            this.loyatlyCard.setId(loyaltyCardObj.getInt("id"));
            this.loyatlyCard.setCount(loyaltyCardObj.getString("count"));
            this.loyatlyCard.setIsWon(loyaltyCardObj.getBoolean("is_won"));
            this.loyatlyCard.setCreatedAt(dateFormat.parse(loyaltyCardObj.getString("created_at")));

            // Loyalty:
            if(!loyaltyCardObj.isNull("loyalty")){
                LoyaltyVo loyalty = new LoyaltyVo();
                JSONObject loyaltyObj = loyaltyCardObj.getJSONObject("loyalty");
                loyalty.setWinCount(loyaltyObj.getString("win_count"));
                loyalty.setName(loyaltyObj.getString("name"));
                loyalty.setDescription(loyaltyObj.getString("description"));
                loyalty.setPrize(loyaltyObj.getString("prize"));

                this.loyatlyCard.setLoyalty(loyalty);
            }

            // Store
            if(!loyaltyCardObj.isNull("stores")){
                JSONArray storesArr = loyaltyCardObj.getJSONArray("stores");
                StoreVo[] storeItems = new StoreVo[storesArr.length()];
                for(int i = 0; i<storesArr.length(); i++){
                    JSONObject storeObj = storesArr.getJSONObject(i);
                    StoreVo storeItem = new StoreVo();
                    storeItem.setId(storeObj.getInt("id"));
                    storeItem.setUniqueId(storeObj.getString("unique_id"));
                    storeItem.setName(storeObj.getString("store_name"));
                    storeItem.setDescription(storeObj.getString("store_description"));
                    storeItem.setAddress(storeObj.getString("address"));
                    storeItem.setEmail(storeObj.getString("email"));
                    storeItem.setManagerContact(storeObj.getString("manager_contact"));
                    storeItem.setUrl(storeObj.getString("url"));
                    storeItem.setStoreImage(storeObj.getString("store_image"));
                    storeItem.setIsOnline(storeObj.getBoolean("is_online"));
                    storeItem.setLatitude(storeObj.getDouble("latitude"));
                    storeItem.setLongitude(storeObj.getDouble("longitude"));
                    storeItem.setCanDeliver(storeObj.getBoolean("can_deliver"));
                    storeItem.setId(storeObj.getInt("id"));
                    storeItems[i] = storeItem;
                }
                this.loyatlyCard.setStoreItems(storeItems);
            }

            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }catch(ParseException e){
            e.printStackTrace();
            return false;
        }
    }
}
