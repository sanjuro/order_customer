package com.vosto.customer.stores.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;
import com.vosto.customer.utils.Constants;
import com.vosto.customer.utils.StoreFavouritesManager;

public class GetStoreFavouriteService extends RestService {

    private StoreFavouritesManager favourites;

    public GetStoreFavouriteService(OnRestReturn listener, VostoBaseActivity context) {
        super("http://107.22.211.58:9000/api/v1/stores/by_store_ids", RequestMethod.POST, ResultType.GET_STORES, listener, context);
        favourites = new StoreFavouritesManager(context);
    }

    @Override
    public String getRequestJson() {
        try {
            JSONObject object = new JSONObject();
            object.put("store_ids", new JSONArray(favourites.load()));
            String json = object.toString();
            Log.d(Constants.TAG, json);
            return json;
        } catch (JSONException je) {
            Log.e(Constants.TAG, "Could not get json", je);
            return super.getRequestJson();
        }
    }

}
