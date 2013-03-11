package com.vosto.customer.accounts;

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

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.services.AuthenticateResult;
import com.vosto.customer.services.AuthenticationService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;


public class SignInActivity extends Activity implements OnRestReturn {

	private ProgressDialog pleaseWaitDialog;
	
	@Override
    protected void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signin);
        
    }
	
	public void signUpClicked(View v){
		Intent intent = new Intent(this, SignUpActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	public void signInClicked(View v){
		this.pleaseWaitDialog = ProgressDialog.show(this, "Authenticating", "Please wait...", true);
		TextView txtEmail = (TextView)findViewById(R.id.txtEmail);
		TextView txtPin = (TextView)findViewById(R.id.txtSecurityPin);
		String email = txtEmail.getText().toString().trim();
		String pin = txtPin.getText().toString().trim();
		
		boolean inputValid = true;
		if(email.equals("")){
			txtEmail.setError("Enter your e-mail.");
			inputValid = false;
		}
		if(pin.equals("")){
			txtPin.setError("Enter your pin.");
			inputValid = false;
		}
		
		if(!inputValid){
			return;
		}
		
		AuthenticationService service = new AuthenticationService(this);
		service.setEmail(email);
		service.setPin(pin);
		Log.d("auth", "Request json: " + service.getRequestJson());
		service.execute();
	}

	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
//		Log.d("auth", "Resp json: " + result.getResponseJson());
		if(result != null && result instanceof AuthenticateResult){
			SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("userToken", "");
			editor.putString("userName", "");
			editor.putString("userPin", "");
			editor.commit();
			AuthenticateResult authResult = (AuthenticateResult)result;
			
			if(!authResult.wasAuthenticationSuccessful()){
				this.showAlertDialog("Login Failed", authResult.getErrorMessage());
			}else{
				 // Save the auth token:
			       
				   editor = settings.edit();
				   editor.putString("userToken", authResult.getCustomer().authentication_token);
				   editor.putString("userName", authResult.getCustomer().first_name);
				   editor.putString("userPin", authResult.getCustomer().user_pin);
				   editor.commit();
				   Intent intent = new Intent(this, HomeActivity.class);
			    	startActivity(intent);
			    	finish();
				 //  this.showAlertDialog("Login Successful!", "Authtoken: " + authResult.getCustomer().authentication_token);
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