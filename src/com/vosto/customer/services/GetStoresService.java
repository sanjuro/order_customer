package com.vosto.customer.services;

public class GetStoresService extends RestService {
	
	
	public GetStoresService(OnRestReturn listener){
		super("http://107.22.211.58:9000/api/v1/stores", RequestMethod.GET, ResultType.GET_STORES, listener);
	}

}