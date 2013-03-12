package com.vosto.customer.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.vosto.customer.accounts.services.AuthenticateResult;
import com.vosto.customer.accounts.services.CreateAccountResult;
import com.vosto.customer.accounts.services.ResetPasswordResult;
import com.vosto.customer.orders.services.GetPreviousOrdersResult;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.services.GetTaxonsResult;
import com.vosto.customer.products.services.GetVariantsResult;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.services.SearchResult;

import android.os.AsyncTask;

public class RestService extends AsyncTask <Void, Void, RestResult> {
	
	protected OnRestReturn listener;
	protected String url;
	protected ResultType resultType;
	
	protected HttpClient httpClient;
	protected HttpContext localContext;
	protected HttpGet httpGet;
	protected HttpPost httpPost;
	protected HttpResponse response;
	protected List<NameValuePair> nameValuePairs; 
	
	protected RequestMethod requestMethod;
	
	
	
	
	

public RestService(String url, RequestMethod requestMethod, ResultType resultType, OnRestReturn listener){
	this.url = url;
	this.resultType = resultType;
	this.listener = listener;
	this.requestMethod = requestMethod;
	
	this.httpClient = new DefaultHttpClient();
	this.localContext = new BasicHttpContext();
	this.httpGet = new HttpGet(this.url);
	this.nameValuePairs = new ArrayList<NameValuePair>();
}
	
protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
	InputStream in = entity.getContent();
	StringBuffer out = new StringBuffer();
	int n = 1;
	while (n>0) {
		byte[] b = new byte[4096];
		n =  in.read(b);
		if (n>0) out.append(new String(b, 0, n));
	}
	return out.toString();
}

public void addPostParameter(String key, String value){
	nameValuePairs.add(new BasicNameValuePair(key, value));  
}

public void clearPostParameters(){
	this.nameValuePairs = new ArrayList<NameValuePair>();
}

public String getRequestJson(){
	return "";
}

@Override
protected RestResult doInBackground(Void... params) {
	
	String text = null;
	if(this.requestMethod == RequestMethod.POST){
		try {
			this.httpPost = new HttpPost(this.url);
			if(this.nameValuePairs.size() > 0){
				this.httpPost.setEntity(new UrlEncodedFormEntity(this.nameValuePairs)); 
			}else if(this.getRequestJson() != ""){
				StringEntity entity = new StringEntity(this.getRequestJson(), HTTP.UTF_8);
				entity.setContentType("application/json");
				this.httpPost.setEntity(entity);
			}
			this.response = httpClient.execute(this.httpPost, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);
			return this.getRestResult(response.getStatusLine(), text);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}else{
		// GET request:
		try {
			this.httpGet = new HttpGet(this.url);
			this.response = httpClient.execute(this.httpGet, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);
			return this.getRestResult(response.getStatusLine(), text);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
		
	}

}

protected void onPostExecute(RestResult result) {
	this.listener.onRestReturn(result);
}

protected RestResult getRestResult(StatusLine statusLine, String responseJson){
	RestResult result = null;
	if(this.resultType == ResultType.CREATE_CUSTOMER){
		result = new CreateAccountResult(200, responseJson);
	}
	if(this.resultType == ResultType.AUTHENTICATE_CUSTOMER){
		result = new AuthenticateResult(200, responseJson);
	}
	if(this.resultType == ResultType.RESET_PIN){
		result = new ResetPasswordResult(200, responseJson);
	}
	if(this.resultType == ResultType.GET_STORES){
		result = new GetStoresResult(200, responseJson);
	}
	if(this.resultType == ResultType.SEARCH_STORES){
		result = new SearchResult(200, responseJson);
	}
	if(this.resultType == ResultType.GET_PRODUCTS){
		result = new GetProductsResult(200, responseJson);
	}
	
	if(this.resultType == ResultType.GET_TAXONS){
		result = new GetTaxonsResult(200, responseJson);
	}
	
	if(this.resultType == ResultType.GET_VARIANTS){
		result = new GetVariantsResult(200, responseJson);
	}
	
	if(this.resultType == ResultType.PLACE_ORDER){
		result = new PlaceOrderResult(200, responseJson);
	}
	
	if(this.resultType == ResultType.GET_PREVIOUS_ORDERS){
		result = new GetPreviousOrdersResult(200, responseJson);
	}
	
	if(result != null){
		if(statusLine != null){
			result.setStatusCode(statusLine.getStatusCode());
		}
		result.processJsonAndPopulate();	
	}
	return result;
}



}