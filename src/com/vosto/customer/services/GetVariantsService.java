package com.vosto.customer.services;

public class GetVariantsService extends RestService {
	
	private int productId;
	
	public GetVariantsService(OnRestReturn listener, int productId){
		super("http://107.22.211.58:9000/api/v1/products/" + productId + "/variants", RequestMethod.GET, ResultType.GET_VARIANTS, listener);
		this.productId = productId;
	}
}