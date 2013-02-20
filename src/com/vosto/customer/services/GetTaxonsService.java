package com.vosto.customer.services;

public class GetTaxonsService extends RestService {
	
	private int storeId;
	
	public GetTaxonsService(OnRestReturn listener, int storeId){
		super("http://107.22.211.58:9000/api/v1/stores/" + storeId + "/taxons", RequestMethod.GET, ResultType.GET_TAXONS, listener);
		this.storeId = storeId;
	}
}