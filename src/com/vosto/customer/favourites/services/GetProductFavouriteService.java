package com.vosto.customer.favourites.services;

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
import com.vosto.customer.utils.ProductFavouritesManager;

public class GetProductFavouriteService extends RestService {

    private ProductFavouritesManager favourites;

    public GetProductFavouriteService(OnRestReturn listener, VostoBaseActivity context) {
        super("http://107.22.211.58:9000/api/v1/products/by_ids", RequestMethod.POST, ResultType.GET_PRODUCTS, listener, context);
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

}
