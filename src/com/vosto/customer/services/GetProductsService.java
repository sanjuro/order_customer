package com.vosto.customer.services;

public class GetProductsService extends RestService {
	
	public GetProductsService(OnRestReturn listener){
		super("http://107.22.211.58:9000/api/v1/products", RequestMethod.GET, ResultType.GET_PRODUCTS, listener);
	}
	
	public GetProductsService(OnRestReturn listener, int taxonId){
		super("http://107.22.211.58:9000/api/v1/taxons/"+taxonId+"/products", RequestMethod.GET, ResultType.GET_PRODUCTS, listener);
	}

}