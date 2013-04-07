package com.vosto.customer.stores.services;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetTagsService extends RestService {
	
	public GetTagsService(OnRestReturn listener, VostoBaseActivity context){
		super(SERVER_URL + "/tags" +
                "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_TAGS, listener, context);
		Log.d("TAG", "Tags auth token: " + context.getAuthenticationToken());
	}

}