package com.vosto.customer.services;

public class RestResult implements IRestResult {
	
	private int statusCode;
	private String responseJson;
	
	public RestResult(){
		this.statusCode = 200;
	}
	
	public RestResult(int statusCode){
		this.statusCode = statusCode;
	}
	
	public RestResult(int statusCode, String responseJson){
		this.statusCode = statusCode;
		this.responseJson = responseJson;
	}
	
	public int getStatusCode(){
		return this.statusCode;
	}
	
	public void setStatusCode(int code){
		this.statusCode = code;
	}
	
	public String getResponseJson(){
		return this.responseJson;
	}
	
	public void setResponseJson(String json){
		this.responseJson = json;
	}

	@Override
	public boolean processJsonAndPopulate() {
		// TODO Auto-generated method stub
		return true;
	}
	

	
	
}