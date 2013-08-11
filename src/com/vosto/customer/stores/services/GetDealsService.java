package com.vosto.customer.stores.services;

import android.util.Log;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetDealsService extends RestService {

    public GetDealsService(OnRestReturn listener, VostoBaseActivity context){
        super(SERVER_URL + "/deals", RequestMethod.GET, ResultType.GET_DEALS, listener, context);
        Log.d("DEAL", "Calling Deals: " + SERVER_URL + "/deals");
    }

    public GetDealsService(OnRestReturn listener, VostoBaseActivity context, int dealId){
        super(SERVER_URL + "/deals/" + dealId, RequestMethod.GET, ResultType.GET_DEALS, listener, context);

    }

}