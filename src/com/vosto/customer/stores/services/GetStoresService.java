package com.vosto.customer.stores.services;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetStoresService extends RestService {
	
	public GetStoresService(OnRestReturn listener, VostoBaseActivity context){
		super(SERVER_URL + "/stores", RequestMethod.GET, ResultType.GET_STORES, listener, context);
	}
	
	public GetStoresService(OnRestReturn listener, VostoBaseActivity context, int storeId){
		super(SERVER_URL + "/stores/" + storeId, RequestMethod.GET, ResultType.GET_STORES, listener, context);
		
	}

}