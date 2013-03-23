package com.vosto.customer.accounts.activities;

import java.util.Locale;

import com.google.android.gcm.GCMRegistrar;
import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.accounts.services.CreateAccountResult;
import com.vosto.customer.accounts.services.CreateAccountService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends Activity implements OnRestReturn {
	
	private ProgressDialog pleaseWaitDialog;
	private String gcmRegistrationId; // The id assigned by gcm for this app / device combination
	
	@Override
    protected void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signup);  
        
        // Check if GCM is OK and see if there's an existing registration id:
        try{
        	 GCMRegistrar.checkDevice(this);
        	 GCMRegistrar.checkManifest(this);
             // GCMRegistrar.unregister(this);
             this.gcmRegistrationId = GCMRegistrar.getRegistrationId(this);
        	 if(!this.gcmRegistrationId.equals("")){
        		 Log.d("GCM", "Device already registered with gcm. Not registering again.");
        		 Log.d("GCM", "GCM id: " + this.gcmRegistrationId);
        	 }
        }catch(Exception e){
        	// GCM is not supported or permissions not set up correctly.
        	e.printStackTrace();
        	Log.d("GCM", "GCM can't be initialized. GCM not supported or problem with manifest.");
        }
    }
	
	public void signInClicked(View v){
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	public void submitClicked(View v){
		CreateAccountService service = new CreateAccountService(this);
		TextView txtName = (TextView)findViewById(R.id.txtName);
		TextView txtEmail = (TextView)findViewById(R.id.txtEmail);
		TextView txtPin = (TextView)findViewById(R.id.txtSecurityPin);
		
		String fullName = txtName.getText().toString().trim();
		if(fullName.length() == 0){
			txtName.setError("Please enter your name.");
			return;
		}
		String[] nameParts = fullName.split("\\s");
		String firstName = nameParts[0];
		String lastName = "";
		if(nameParts.length > 1){
			for(int i = 1; i<nameParts.length; i++){
				lastName += nameParts[i] + " ";
			}
		}
		lastName = lastName.trim();
		
		String email = txtEmail.getText().toString().trim().toLowerCase(Locale.getDefault());
		if(email == ""){
			txtEmail.setError("Please enter your email.");
			return;
		}
		
		String pin = txtPin.getText().toString().trim();
		
		if(pin == ""){
			txtPin.setError("Please choose a security PIN.");
			return;
		}
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Creating Account", "Please wait...", true);
		
		service.setFirstName(firstName);
		service.setLastName(lastName);
		service.setEmail(email);
		service.setUserPin(pin);
		service.setMobileNumber("0718900136");
		service.execute();
	}

	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result != null && result instanceof CreateAccountResult){
			CreateAccountResult createResult = (CreateAccountResult)result;
			SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
			SharedPreferences.Editor editor = settings.edit();
			 editor.putString("userToken", "");
			   editor.putString("userName", "");
			   editor.commit();
			if(createResult.wasAccountCreated()){
				  editor = settings.edit();
				   editor.putString("userToken", createResult.getAccountResponseWrapper().authentication_token);
				   editor.putString("userName", createResult.getAccountResponseWrapper().first_name);
                   editor.putString("userEmail", createResult.getAccountResponseWrapper().email);
                   editor.putString("userPin", createResult.getUserPin());
				   editor.commit();
				   
				   /*
				    *  Register the device with GCM if not already registered.
				    *  GCM will respond and the callback in GCMIntentService will be called.
				    */
				   if(this.gcmRegistrationId != null && this.gcmRegistrationId.equals("")){
					   // GCM is supported, but device has not been registered yet.
					   Log.d("GCM", "Calling gcm register...");
					   GCMRegistrar.register(this, "1091536520954"); // The Vosto project id as assigned by GCM at the beginning
			            //1091536520954 - shadley's account: used for production
					   //263607631818 - flippie test account
				   }else{
					   Log.d("GCM", "Not registering with gcm");
				   }
				   
				Intent intent = new Intent(this, HomeActivity.class);
		    	startActivity(intent);
		    	finish();
			}else{
				this.showAlertDialog("Signup Failed", createResult.getErrorMessage());
			}
		}
		
	}
	
	public void showAlertDialog(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
}