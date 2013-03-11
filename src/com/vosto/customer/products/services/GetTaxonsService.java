package com.vosto.customer.products.services;

import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetTaxonsService extends RestService {
	
	private int storeId;
	
	public GetTaxonsService(OnRestReturn listener, int storeId){
		super("http://107.22.211.58:9000/api/v1/stores/" + storeId + "/taxons", RequestMethod.GET, ResultType.GET_TAXONS, listener);
		this.storeId = storeId;
	}
}