package com.vosto.customer.loyalties.services;

import android.util.Log;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.loyalties.services.GetLoyaltyCardsResult;
import com.vosto.customer.loyalties.vos.LoyaltyVo;
import com.vosto.customer.loyalties.vos.LoyaltyCardVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetLoyaltyCardsService extends RestService {

    public GetLoyaltyCardsService(OnRestReturn listener, VostoBaseActivity context){
        super(SERVER_URL + "/loyalty_cards/by_customer?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_LOYALTY_CARDS, listener, context);
        Log.d("LOYAL", "Previous orders url: " + SERVER_URL + "/loyalty_cards/by_customer?authentication_token=" + context.getAuthenticationToken());
    }

//    public GetLoyaltyCardsService(OnRestReturn listener, VostoBaseActivity context, int loyaltyCard){
//        super(SERVER_URL + "/loyalty_cards/" + loyaltyCard + "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_LOYALTY_CARDS, listener, context);
//    }

}