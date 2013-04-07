package com.vosto.customer.products.services;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetVariantsService extends RestService {
	
	public GetVariantsService(OnRestReturn listener, VostoBaseActivity context, int productId){
		super(SERVER_URL + "/products/" + productId + "/variants", RequestMethod.GET, ResultType.GET_VARIANTS, listener, context);
	}
}