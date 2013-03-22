package com.vosto.customer.orders.services;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;


public class GetOrderByIdService extends RestService {
	
	private int orderId; // The id of the order to fetch
	
	public GetOrderByIdService(OnRestReturn listener, VostoBaseActivity context, int orderId){
		super("http://107.22.211.58:9000/api/v1/orders/" + orderId + "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_ORDER_BY_ID, listener);
		this.orderId = orderId;
	}

}