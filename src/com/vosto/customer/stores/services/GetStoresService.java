package com.vosto.customer.stores.services;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetStoresService extends RestService {
	
	public GetStoresService(OnRestReturn listener, VostoBaseActivity context){
		super("http://107.22.211.58:9000/api/v1/stores", RequestMethod.GET, ResultType.GET_STORES, listener, context);
	}
	
	public GetStoresService(OnRestReturn listener, VostoBaseActivity context, int storeId){
		super("http://107.22.211.58:9000/api/v1/stores/" + storeId, RequestMethod.GET, ResultType.GET_STORES, listener, context);
		
	}

}