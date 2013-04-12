package com.vosto.customer.products.services;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class GetTaxonsService extends RestService {
	
	public GetTaxonsService(OnRestReturn listener, VostoBaseActivity context, int storeId){
		super(SERVER_URL + "/stores/" + storeId + "/taxons", RequestMethod.GET, ResultType.GET_TAXONS, listener, context);
	}
}