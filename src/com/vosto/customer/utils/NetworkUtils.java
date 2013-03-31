package com.vosto.customer.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtils {

	/**
	 * Checks if the device is connected to a network.
	 * Note: It will return true even if it's connected to a wifi without outside internet access, or with proxy authentication.
	 * So we will use hasOutsideInternetConnection to determine whether we can actually access the internet.
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		if(context == null){
			Log.d("NET", "Context is null in networkutils");
		}
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	/**
	 * Checks if the device can access google.com, which means it has an outside internet connection.
	 * @param context
	 * @return
	 */
	public static boolean hasOutsideInternetConnection(Context context) {
		if (NetworkUtils.isNetworkAvailable(context)) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500); 
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				return false;
			}
		} else {
        	return false;
    	}
	}

}