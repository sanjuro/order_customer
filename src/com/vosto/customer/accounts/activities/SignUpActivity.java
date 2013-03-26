package com.vosto.customer.accounts.activities;

import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.services.CreateAccountResult;
import com.vosto.customer.accounts.services.CreateAccountService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.GCMUtils;

public class SignUpActivity extends VostoBaseActivity implements OnRestReturn {
	
	private ProgressDialog pleaseWaitDialog;
	private String gcmRegistrationId; // The id assigned by gcm for this app / device combination
	
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signup);  
        
        if(!GCMUtils.checkGCMAndAlert(this, false)){
        	return;
        }
        
        // Now we know we have GCM support and a Google account:
        this.gcmRegistrationId = GCMRegistrar.getRegistrationId(this);
        if(!this.gcmRegistrationId.equals("")){
        	Log.d("GCM", "Device already registered with gcm. Not registering again.");
        	Log.d("GCM", "GCM id: " + this.gcmRegistrationId);
        } 
    }
	
	public void signInClicked(View v){
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	public void submitClicked(View v){
		
		if(!GCMUtils.checkGCMAndAlert(this, false)){
			return;
		}
		
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
	
	
	
}