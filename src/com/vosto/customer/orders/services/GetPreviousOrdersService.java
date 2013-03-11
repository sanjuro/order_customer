package com.vosto.customer.orders.services;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;


public class GetPreviousOrdersService extends RestService {
	
	public GetPreviousOrdersService(OnRestReturn listener, VostoBaseActivity context){
		super("http://107.22.211.58:9000/api/v1/orders/page/1?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_PREVIOUS_ORDERS, listener);
		Log.d("PREV", "Previous orders url: " + "http://107.22.211.58:9000/api/v1/orders/page/1?authentication_token=" + context.getAuthenticationToken());
	}
	
	public GetPreviousOrdersService(OnRestReturn listener, VostoBaseActivity context, int page){
		super("http://107.22.211.58:9000/api/v1/orders/page/" + page + "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_PREVIOUS_ORDERS, listener);
	}

}