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

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.VostoCustomerApp;
import com.vosto.customer.accounts.services.AuthenticateResult;
import com.vosto.customer.accounts.services.CreateAccountResult;
import com.vosto.customer.accounts.services.RegisterDeviceResult;
import com.vosto.customer.accounts.services.ResetPasswordResult;
import com.vosto.customer.favourites.services.GetStoreFavouriteResult;
import com.vosto.customer.orders.services.GetOrderByIdResult;
import com.vosto.customer.orders.services.GetPreviousOrdersResult;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.services.GetTaxonsResult;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.services.GetTagsResult;
import com.vosto.customer.stores.services.SearchResult;
import com.vosto.customer.utils.NetworkUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class RestService extends AsyncTask <Void, Void, RestResult> {
	
	protected OnRestReturn listener;
	protected VostoBaseActivity activity;
	protected VostoCustomerApp context;
	protected String url;
	protected ResultType resultType;
	
	protected HttpClient httpClient;
	protected HttpContext localContext;
	protected HttpGet httpGet;
	protected HttpPost httpPost;
	protected HttpResponse response;
	protected List<NameValuePair> nameValuePairs; 
	
	public boolean hasInternetConnection;
	
	protected RequestMethod requestMethod;
	
	private Exception executeException; // Exception thrown while executing so we can handle the error on the ui thread.
	
	
	public RestService(String url, RequestMethod requestMethod, ResultType resultType, OnRestReturn listener, VostoCustomerApp context, VostoBaseActivity activity){
		this.url = url;
		this.resultType = resultType;
		this.listener = listener;
		this.activity = activity;
		this.context = context;
		this.requestMethod = requestMethod;
		
		this.httpClient = new DefaultHttpClient();
		this.localContext = new BasicHttpContext();
		this.httpGet = new HttpGet(this.url);
		this.nameValuePairs = new ArrayList<NameValuePair>();
		this.hasInternetConnection = true;
	}	
	
	
	public RestService(String url, RequestMethod requestMethod, ResultType resultType, OnRestReturn listener, VostoCustomerApp context){
		this.url = url;
		this.resultType = resultType;
		this.listener = listener;
		this.activity = null;
		this.context = context;
		this.requestMethod = requestMethod;
		
		this.httpClient = new DefaultHttpClient();
		this.localContext = new BasicHttpContext();
		this.httpGet = new HttpGet(this.url);
		this.nameValuePairs = new ArrayList<NameValuePair>();
		this.hasInternetConnection = true;
	}	
	

    public RestService(String url, RequestMethod requestMethod, ResultType resultType, OnRestReturn listener, VostoBaseActivity activity){
        this.url = url;
        this.resultType = resultType;
        this.listener = listener;
        this.activity = activity;
        this.requestMethod = requestMethod;

        this.httpClient = new DefaultHttpClient();
        this.localContext = new BasicHttpContext();
        this.httpGet = new HttpGet(this.url);
        this.nameValuePairs = new ArrayList<NameValuePair>();
        this.hasInternetConnection = true;
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
    protected  void onPreExecute()
    {
        Context tempContext = this.context != null ? this.context : (this.activity != null ? this.activity : null);
        if(tempContext == null){
            return;
        }
       // If we don't have an internet connection, cancel the task and alert the user:
        if(!NetworkUtils.isNetworkAvailable(tempContext)){
            cancel(false);
            if(this.activity != null){
                this.activity.showAlertDialog("Connection Error", "Please connect to the internet.");
            }
        }else{
            // Everything looks OK. Service can run:
            if(this.activity != null){
                this.activity.pleaseWaitDialog = ProgressDialog.show(this.activity, "Loading", "Please wait...", true, true,  new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel(true);
                    }
                });
            }else{
                Log.d("ACT", "Activity is null.");
            }
        }
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
                this.executeException = e;
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
                this.executeException = e;
                e.printStackTrace();
                return null;
            }

        }

    }

    protected void onPostExecute(RestResult result) {
        if(this.activity != null){
            this.activity.dismissPleaseWaitDialog();
        }
        if(this.executeException != null){
            // An error occured during execution, most likely a loss of connectivity.
            if(this.activity != null){
                this.activity.dismissPleaseWaitDialog();
                this.activity.showAlertDialog("ERROR", "Please check your internet connection and try again.");
            }
        }else{
            // The call completed successfully. Handle the result.
            this.listener.onRestReturn(result);
        }
    }

    protected RestResult getRestResult(StatusLine statusLine, String responseJson){
        RestResult result = null;
        if(this.resultType == ResultType.CREATE_CUSTOMER){
            result = new CreateAccountResult(200, responseJson);
        }
        if(this.resultType == ResultType.REGISTER_DEVICE){
            result = new RegisterDeviceResult(200, responseJson);
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

        if(this.resultType == ResultType.GET_TAGS){
            result = new GetTagsResult(200, responseJson);
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

        if(this.resultType == ResultType.PLACE_ORDER){
            result = new PlaceOrderResult(200, responseJson);
        }

        if(this.resultType == ResultType.GET_PREVIOUS_ORDERS){
            result = new GetPreviousOrdersResult(200, responseJson);
        }

        if(this.resultType == ResultType.GET_ORDER_BY_ID){
            result = new GetOrderByIdResult(200, responseJson);
        }

        if(this.resultType == ResultType.GET_FAVOURITE_STORES){
            result = new GetStoreFavouriteResult(200, responseJson);
        }

    //    if(this.resultType == ResultType.GET_FAVOURITE_PRODUCTS){
    //        result = new GetProdudctFavouriteResult(200, responseJson);
    //    }

        if(result != null){
            if(statusLine != null){
                result.setStatusCode(statusLine.getStatusCode());
            }
            result.processJsonAndPopulate();
        }
        return result;
    }
}