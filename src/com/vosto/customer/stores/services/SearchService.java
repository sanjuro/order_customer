package com.vosto.customer.stores.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class SearchService extends RestService {
	
	public String searchTerm;
	public double latitude;
	public double longitude;
	public boolean hasLocation;
	private VostoBaseActivity context;
	
	public SearchService(OnRestReturn listener, VostoBaseActivity context){
		super("http://107.22.211.58:9000/api/v1/stores/search", RequestMethod.POST, ResultType.SEARCH_STORES, listener);
		this.context = context;
		this.hasLocation = false;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	

	public boolean hasLocation() {
		return hasLocation;
	}

	public void setHasLocation(boolean hasLocation) {
		this.hasLocation = hasLocation;
	}

	public String getRequestJson(){
		try{
			JSONObject root = new JSONObject();
			root.put("authentication_token", context.getAuthenticationToken());
			root.put("query_term", this.searchTerm);
			if(this.hasLocation){
				root.put("latitude", this.latitude);
				root.put("longitude", this.longitude);
			}
			Log.d("Location", "search json: " + root.toString());
			return root.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
		
	}
	
	

}