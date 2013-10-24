package com.vosto.customer.loyalties.services;

import java.text.DateFormat;

import android.util.Log;
import com.vosto.customer.loyalties.vos.LoyaltyCardVo;
import com.vosto.customer.loyalties.vos.LoyaltyVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;
import org.joda.money.Money;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/09/08
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetLoyaltyCardsResult extends RestResult implements IRestResult {

    private JSONArray jsonArr;
    private LoyaltyCardVo[] loyaltyCards;

    public GetLoyaltyCardsResult(){
        super();
    }

    public GetLoyaltyCardsResult(int responseCode, String responseJson){
        super(responseCode, responseJson);
    }

    public JSONArray getJSONArray(){
        return this.jsonArr;
    }

    public LoyaltyCardVo[] getLoyaltyCards(){
        return this.loyaltyCards;
    }

    @Override
    public boolean processJsonAndPopulate(){
        try{
            JSONObject outerObj = new JSONObject(this.getResponseJson());
            Log.d("ORD", "prev Loyalty Card response json: " + this.getResponseJson());

            ArrayList<LoyaltyCardVo> loyaltyCardList = new ArrayList<LoyaltyCardVo>();

            Iterator<?> keys = outerObj.keys();

            List loyatlyCards = listFromJsonSorted(outerObj);

            LoyaltyCardVo currentLoyaltyCard = null;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

            for( Object loyaltyCard: loyatlyCards ) {
                if(loyaltyCard instanceof JSONObject){
                    JSONObject currentObj = (JSONObject)loyaltyCard;
                    currentLoyaltyCard = new LoyaltyCardVo();
                    currentLoyaltyCard.setId(currentObj.getInt("id"));
                    currentLoyaltyCard.setCount(currentObj.getString("count"));
                    currentLoyaltyCard.setIsWon(currentObj.getBoolean("is_won"));
                    currentLoyaltyCard.setCreatedAt(dateFormat.parse(currentObj.getString("created_at")));

                    // Loyalty:
                    if(!currentObj.isNull("loyalty")){
                        LoyaltyVo loyalty = new LoyaltyVo();
                        JSONObject loyaltyObj = currentObj.getJSONObject("loyalty");
                        loyalty.setWinCount(loyaltyObj.getString("win_count"));
                        loyalty.setName(loyaltyObj.getString("name"));
                        loyalty.setDescription(loyaltyObj.getString("description"));
                        loyalty.setPrize(loyaltyObj.getString("prize"));

                        currentLoyaltyCard.setLoyalty(loyalty);
                    }

                loyaltyCardList.add(currentLoyaltyCard);
                }
            }

            this.loyaltyCards = new LoyaltyCardVo[loyaltyCardList.size()];
            for(int i = 0; i<loyaltyCardList.size(); i++){
                 this.loyaltyCards[i] = loyaltyCardList.get(i);
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

    public static List listFromJsonSorted(JSONObject json) {
        if (json == null) return null;
        SortedMap map = new TreeMap(Collections.reverseOrder());
        Iterator i = json.keys();
        while (i.hasNext()) {
            try {
                String key = i.next().toString();
                JSONObject j = json.getJSONObject(key);
                map.put(key, j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new LinkedList(map.values());
    }
}
