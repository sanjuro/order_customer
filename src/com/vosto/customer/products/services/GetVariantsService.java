package com.vosto.customer.products.services;

import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class GetVariantsService extends RestService {
	
	public GetVariantsService(OnRestReturn listener, int productId){
		super("http://107.22.211.58:9000/api/v1/products/" + productId + "/variants", RequestMethod.GET, ResultType.GET_VARIANTS, listener);
	}
}