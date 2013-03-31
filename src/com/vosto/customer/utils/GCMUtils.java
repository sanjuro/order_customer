package com.vosto.customer.utils;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.google.android.gcm.GCMRegistrar;
import com.vosto.customer.VostoBaseActivity;

public class GCMUtils {
	
	/**
	 * Checks if the device supports GCM messaging.
	 * @param context - the activity where this function is called from
	 * @return boolean - whether device supports gcm
	 */
	public static boolean supportsGCM(VostoBaseActivity context){
		try{
			 GCMRegistrar.checkDevice(context);
        	 GCMRegistrar.checkManifest(context);
			
        	 // No exceptions, so device supports gcm:
        	 return true;
		}catch(Exception e){
			//Device does not support gcm.
			return false;
		}
	}
	
	/**
	 * Checks whether the device has a Google account set up.
	 * @param context - the activity where this function is called from
	 * @return whether device has a google account
	 */
	public static boolean hasGoogleAccount(VostoBaseActivity context){
		 AccountManager accMan = AccountManager.get(context);
    	 Account[] accArray = accMan.getAccountsByType("com.google");
    	 return accArray.length >= 1;
	}
	
	/**
	 * Checks whether this device is GCM registered with a GCM id.
	 * @param context - the activity where this function is called from
	 * @return whether the device has a gcm id.
	 */
	public static boolean hasRegistrationId(VostoBaseActivity context){
		String gcmId = GCMRegistrar.getRegistrationId(context);
		return gcmId != null && !gcmId.trim().equals("");
	}
	
	/**
	 * Send an unregister call to GCM. GCM will then call onUnregistered in the GCMIntentService.
	 * @param context - the activity where this function is called from
	 */
	public static void unregister(VostoBaseActivity context){
		 GCMRegistrar.unregister(context);
	}
	
	/**
	 * Checks GCM support, Google account and GCM id, and displays the appropriate error message 
	 * according to the type of error.
	 * 
	 * @param context - the activity where this function is called from.
	 * @return - whether GCM is OK or not. If false, something is wrong with gcm 
	 * (not supported or missing google account or no gcm id.) 
	 */
	public static boolean checkGCMAndAlert(VostoBaseActivity context){
		// We default the mustBeRegistered to true:
		return GCMUtils.checkGCMAndAlert(context, true);
	}
	
	/**
	 * Checks GCM support, Google account and GCM id, and displays the appropriate error message 
	 * according to the type of error.
	 * 
	 * @param context - the activity where this function is called from.
	 * @param mustBeRegistered - Set to true if the user must already be GCM registered at this point. Set to false if this check happens before GCM registration.
	 * @return - whether GCM is OK or not. If false, something is wrong with gcm 
	 * (not supported or missing google account or no gcm id.) 
	 */
	public static boolean checkGCMAndAlert(VostoBaseActivity context, boolean mustBeRegistered){
		if(!GCMUtils.supportsGCM(context)){
			context.showAlertDialog("Sorry", "Google Cloud Messaging is not supported on your phone. You can't use this app at the moment. Please contact support.");
			return false;
		}
		if(!GCMUtils.hasGoogleAccount(context)){
			context.showAlertDialog("Google Account Required", "This app requires a Google account to be set up on your phone. Please set up a Google account.");
        	return false;
		}
		if(mustBeRegistered && !GCMUtils.hasRegistrationId(context)){
			context.showAlertDialog("Missing GCM id", "You are not registered with GCM. Please sign up again or contact support.");
			return false;
		}
		
		// Everything is OK:
		return true;
	}
	
}