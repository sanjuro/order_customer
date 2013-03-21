package com.vosto.customer;

import com.vosto.customer.accounts.services.RegisterDeviceResult;
import com.vosto.customer.accounts.services.RegisterDeviceService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService implements OnRestReturn {

	public GCMIntentService(){
		super("1091536520954"); // The Vosto project id as assigned by GCM at the beginning.
		//1091536520954 - shadley's account - used for production
		   //263607631818 - flippie test account
	}
	
	@Override
	protected void onError(Context context, String arg1) {
		// GCM has returned an unrecoverable error.
		Log.d("GCM", "GCM onError (unrecoverable)");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// A message has been received from GCM.
		Log.d("GCM", "GCM message received.");
		
		Intent notificationIntent=new Intent(context, HomeActivity.class);
		generateNotification(context, intent.getStringExtra("msg"), notificationIntent);
	}
	
	

	private static void generateNotification(Context context, String message, Intent notificationIntent) {
	    int icon = R.drawable.vosto_logo;
	    long when = System.currentTimeMillis();
	    NotificationManager notificationManager = (NotificationManager)
	            context.getSystemService(Context.NOTIFICATION_SERVICE);
	    Notification notification = new Notification(icon, message, when);
	    String title = context.getString(R.string.app_name);

	    // set intent so it does not start a new activity
	    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	            Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent intent =PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    notification.setLatestEventInfo(context, title, message, intent);
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    notificationManager.notify(0, notification);
	}

	@Override
	protected void onRegistered(Context context, String gcmId) {
		// Device has been registered with GCM. Save the id to the device preferences:
		Log.d("GCM", "GCM onRegistered");
		 SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		 SharedPreferences.Editor editor = settings.edit();
		 editor.putString("gcmId", gcmId);
	     editor.commit();
	     
	     // Now we register the device with Vosto, passing the new gcm id:
	     Log.d("GCM", "Registering device with Vosto...");
	     RegisterDeviceService service = new RegisterDeviceService(this, (VostoCustomerApp)context, gcmId);
	     service.execute();
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// Device has been unregistered from gcm. Remove the id from the app preferences:
		Log.d("GCM", "GCM onUnregistered");
		 SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		 SharedPreferences.Editor editor = settings.edit();
		 editor.remove("gcmId");
	     editor.commit();	
	}

	@Override
	public void onRestReturn(RestResult result) {
		if(result == null){
			return;
		}
		if(result instanceof RegisterDeviceResult){
			// Vosto has responded after we called devices/register
			RegisterDeviceResult registerResult = (RegisterDeviceResult)result;
			if(registerResult.wasSuccessful()){
				//Device has been registered with Vosto.
				Log.d("GCM", "Device registered with Vosto!");
			}else{
				Log.d("GCM", "Vosto could not register the device.");
			}
			
		}
		
	}
	
	
}