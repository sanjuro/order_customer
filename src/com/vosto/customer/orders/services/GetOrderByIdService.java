package com.vosto.customer.orders.services;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;


public class GetOrderByIdService extends RestService {
	
	public GetOrderByIdService(OnRestReturn listener, VostoBaseActivity context, int orderId){
		super(SERVER_URL + "/orders/" + orderId + "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_ORDER_BY_ID, listener, context);
	}

}