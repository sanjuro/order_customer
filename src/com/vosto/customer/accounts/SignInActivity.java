package com.vosto.customer.accounts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.AuthenticateResult;
import com.vosto.customer.services.AuthenticationService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.ResetPasswordResult;
import com.vosto.customer.services.ResetPasswordService;
import com.vosto.customer.services.RestResult;


public class SignInActivity extends VostoBaseActivity implements OnRestReturn {

	private ProgressDialog pleaseWaitDialog;
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signin);
        
        TextView lblForgotPin = (TextView)findViewById(R.id.lblForgotPin);
        lblForgotPin.setText(Html.fromHtml("<u>Forgot Pin?</u>"));        
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

	public void forgotPinClicked(View v){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter your e-mail");
		alert.setMessage("E-mail:");

		// Set an EditText view to get user input 
		final EditText emailInput = new EditText(this);
		emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		alert.setView(emailInput);

		alert.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			 String email = emailInput.getText().toString().trim();
			 confirmResetPassword(email);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		
	}
	
	public void confirmResetPassword(String email){  
		this.pleaseWaitDialog = ProgressDialog.show(this, "Sending Password", "Please wait...", true);
		ResetPasswordService service = new ResetPasswordService(this, this, email);
		service.execute();
	}
	
	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			this.showAlertDialog("Login Failed", "Please try again.");
			return;
		}
		if(result instanceof AuthenticateResult){
			processAuthenticateResult((AuthenticateResult)result);
		}else if(result instanceof ResetPasswordResult){
			processResetPasswordResult((ResetPasswordResult)result);
		}
	}
	
	private void processAuthenticateResult(AuthenticateResult authResult){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userToken", "");
		editor.putString("userName", "");
		editor.putString("userPin", "");
		editor.commit();
		
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
	
	private void processResetPasswordResult(ResetPasswordResult result){
		if(result.getResponseMessage() != null && result.getResponseMessage().trim().toLowerCase().equals("new pin sent")){
			this.showAlertDialog("Pin reset", "Please check your e-mail");
		}else{
			this.showAlertDialog("ERROR:", "Your pin could not be reset. Please check your e-mail address and try again.");
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