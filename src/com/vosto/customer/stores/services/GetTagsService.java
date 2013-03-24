package com.vosto.customer.stores.services;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetTagsService extends RestService {
	
	public GetTagsService(OnRestReturn listener, VostoBaseActivity context){
		super("http://107.22.211.58:9000/api/v1/tags" +
                "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_TAGS, listener);
		Log.d("TAG", "Tags auth token: " + context.getAuthenticationToken());
	}

}