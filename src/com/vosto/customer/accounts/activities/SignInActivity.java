package com.vosto.customer.accounts.activities;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.services.*;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.GCMUtils;

import com.vosto.customer.utils.ToastExpander;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import static com.vosto.customer.utils.CommonUtilities.SENDER_ID;

/**
 * The Sign In screen where an existing user logs in.
 *
 */
public class SignInActivity extends VostoBaseActivity implements OnRestReturn {

    private String gcmRegistrationId;
    SocialAuthAdapter adapter;
    Profile profileMap;

	@Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signin);
        
        TextView lblForgotPin = (TextView)findViewById(R.id.lblForgotPin);
        lblForgotPin.setText(Html.fromHtml("<u>Forgot Pin?</u>"));

        // Adapter initialization
        adapter = new SocialAuthAdapter(new ResponseListener());

        if(!GCMUtils.checkGCMAndAlert(this, false)){
            return;
        }
        this.gcmRegistrationId = GCMRegistrar.getRegistrationId(this);
        Log.d("GCM", "GCM id: " + this.gcmRegistrationId);
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

    public void facebookLoginClicked(View v){
        adapter.authorize(this, SocialAuthAdapter.Provider.FACEBOOK);
    }

    public void signInUserFromFacebook(String email, String userPin){

        Toast aToast = Toast.makeText(this, "You have successfully signed in using your Facebook Account.", Toast.LENGTH_LONG);
        ToastExpander.showFor(aToast, 4000);

        AuthenticationService service = new AuthenticationService(this, this);
        service.setEmail(email);
        service.setPin(userPin);
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

        /*
        *  Register the device with GCM if not already registered.
        *  GCM will respond and the callback in GCMIntentService will be called.
        */
        Log.d("GCM", "GCM Registrsion id: " + this.gcmRegistrationId );
        if(this.gcmRegistrationId != null && this.gcmRegistrationId.equals("")){
            // GCM is supported, but device has not been registered yet.
            Log.d("GCM", "Calling gcm register...");
            GCMRegistrar.register(this, SENDER_ID);
        }else{
            Log.d("GCM", "Not registering with gcm");
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

    // To receive the response after authentication
    private final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {
            Log.d("SOCIAL", "Authentication Successful");

            profileMap = adapter.getUserProfile();
            Log.d("SOCIAL",  "ID = "       + profileMap.getValidatedId());
            Log.d("SOCIAL",  "Email      = "       + profileMap.getEmail());

            String email = profileMap.getEmail();
            String validID = profileMap.getValidatedId();

            String userPin = validID.substring(0,6);

            signInUserFromFacebook(email,userPin);
        }

        @Override
        public void onError(SocialAuthError error) {
            Log.d("SOCIAL", "Error");
            error.printStackTrace();
        }

        @Override
        public void onCancel() {
            Log.d("SOCIAL", "Cancelled");
        }

        @Override
        public void onBack() {
            Log.d("SOCIAL", "Dialog Closed by pressing Back Key");

        }
    }

	
}