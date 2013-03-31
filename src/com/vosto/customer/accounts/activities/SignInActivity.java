package com.vosto.customer.accounts.activities;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.services.AuthenticateResult;
import com.vosto.customer.accounts.services.AuthenticationService;
import com.vosto.customer.accounts.services.ResetPasswordResult;
import com.vosto.customer.accounts.services.ResetPasswordService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.GCMUtils;



/**
 * The Sign In screen where an existing user logs in.
 *
 */
public class SignInActivity extends VostoBaseActivity implements OnRestReturn {
	
	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signin);
        
        TextView lblForgotPin = (TextView)findViewById(R.id.lblForgotPin);
        lblForgotPin.setText(Html.fromHtml("<u>Forgot Pin?</u>"));        
    }
	
	/**
	 * Redirects the user to signup if she does not have an account yet.
	 * @param v The signup button instance.
	 */
	public void signUpClicked(View v){
		Intent intent = new Intent(this, SignUpActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	/**
	 * Validates the input fields and makes the REST call to authenticate.
	 * @param v the sign in button instance
	 */
	public void signInClicked(View v){
		if(!GCMUtils.checkGCMAndAlert(this, false)){
			return;
		}
		
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
		
		//Make the REST call:
		AuthenticationService service = new AuthenticationService(this, this);
		service.setEmail(email);
		service.setPin(pin);
		service.execute();
	}

	/**
	 * Prompts for an email and then calls the reset pin service.
	 * @param v The forgot pin link instance
	 */
	public void forgotPinClicked(View v){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter your e-mail");
		alert.setMessage("E-mail:");

		// Space to enter email address:
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
	
	/**
	 * Called after the user has entered an email and confirmed to reset pin.
	 * @param email
	 */
	public void confirmResetPassword(String email){  
		ResetPasswordService service = new ResetPasswordService(this, this, email);
		service.execute();
	}
	
	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
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
	
	/**
	 * Called when the authentication rest call returns.
	 * If the auth is successful, save the token, etc and redirect to the home screen.
	 * @param authResult
	 */
	private void processAuthenticateResult(AuthenticateResult authResult){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userToken", "");
		editor.putString("userName", "");
        editor.putString("userEmail", "");
        editor.putString("userMobile", "");
		editor.commit();
		
		if(!authResult.wasAuthenticationSuccessful()){
			this.showAlertDialog("Login Failed", authResult.getErrorMessage());
		}else{
			 // Save the auth token in the app's shared preferences.
            editor = settings.edit();
            editor.putString("userToken", authResult.getCustomer().authentication_token);
            editor.putString("userName", authResult.getCustomer().full_name);
            editor.putString("userEmail", authResult.getCustomer().email);
            editor.putString("userMobileNumber", authResult.getCustomer().mobile_number);
            editor.commit();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
		}
	}
	
	/**
	 * Called when the reset pin rest call returns. Asks the user to check their email for a new pin.
	 * @param result
	 */
	private void processResetPasswordResult(ResetPasswordResult result){
		if(result.getResponseMessage() != null && result.getResponseMessage().trim().toLowerCase(Locale.US).equals("new pin sent")){
			this.showAlertDialog("Pin reset", "Please check your e-mail");
		}else{
			this.showAlertDialog("ERROR:", "Your pin could not be reset. Please check your e-mail address and try again.");
		}
		
	}
	
	
}