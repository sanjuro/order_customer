package com.vosto.customer.stores.services;

import android.util.Log;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetFeaturedStoresService extends RestService {

    public GetFeaturedStoresService(OnRestReturn listener, VostoBaseActivity context){
        super(SERVER_URL + "/stores/featured", RequestMethod.GET, ResultType.GET_FEATURED_STORES, listener, context);
        Log.d("STORE", "Calling Featured stores");
    }

}