package com.vosto.customer.accounts.activities;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import com.agimind.widget.SlideHolder;
import com.google.android.gcm.GCMRegistrar;
import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.services.CreateAccountResult;
import com.vosto.customer.accounts.services.CreateAccountService;
import com.vosto.customer.accounts.vos.CustomerVo;
import com.vosto.customer.cart.activities.AddressDialog;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.GCMUtils;
import com.vosto.customer.utils.StringUtils;
import com.vosto.customer.utils.ToastExpander;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import static com.vosto.customer.utils.CommonUtilities.SENDER_ID;

public class SignUpActivity extends VostoBaseActivity implements OnRestReturn {

	private String gcmRegistrationId; // The id assigned by gcm for this app / device combination

    // SocialAuth Components
    SocialAuthAdapter adapter;
    Profile profileMap;
    private SlideHolder mSlideHolder;
    private SignUpDialog signupDialog;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        if(!GCMUtils.checkGCMAndAlert(this, false)){
        	return;
        }
        // GCMRegistrar.unregister(this);
        // Now we know we have GCM support and a Google account:
        this.gcmRegistrationId = GCMRegistrar.getRegistrationId(this);
        if(!this.gcmRegistrationId.equals("")){
        	Log.d("GCM", "Device already registered with gcm. Not registering again.");
        	Log.d("GCM", "GCM id: " + this.gcmRegistrationId);
        }

        // Adapter initialization
        adapter = new SocialAuthAdapter(new ResponseListener());
    }

	public void signInClicked(View v){
		Intent intent = new Intent(this, SignInActivity.class);
    	startActivity(intent);
    	finish();
	}

    public void facebookLoginClicked(View v){
        adapter.authorize(this, SocialAuthAdapter.Provider.FACEBOOK);
    }

    public void createUserFromFacebook(String firstName, String lastName, String email, String validID, String birthday, String gender){

        this.signupDialog = new SignUpDialog(this, firstName, lastName, email, birthday, gender);

        this.signupDialog.setContentView(R.layout.dialog_signup);

        Window window = this.signupDialog.getWindow();
        WindowManager.LayoutParams lp = this.signupDialog.getWindow().getAttributes();
        lp.dimAmount=0.8f;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        this.signupDialog.getWindow().setAttributes(lp);
        this.signupDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        this.signupDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        this.signupDialog.show();
    }
	
	public void submitClicked(View v){
		
		if(!GCMUtils.checkGCMAndAlert(this, false)){
			return;
		}
		
		CreateAccountService service = new CreateAccountService(this, this);
		TextView txtName = (TextView)findViewById(R.id.txtName);
        TextView txtMobile = (TextView)findViewById(R.id.txtMobile);
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

        String mobilenumber = txtMobile.getText().toString().trim();

        if(mobilenumber.length() == 0){
            txtMobile.setError("Please enter your mobile number.");
            return;
        }
        
        if(mobilenumber.length() < 10){
        	txtMobile.setError("Mobile number should be at least 10 digits.");
        	return;
        }
        
		
		String email = txtEmail.getText().toString().trim().toLowerCase(Locale.getDefault());
		if(email.length() == 0){
			txtEmail.setError("Please enter your email.");
			return;
		}
		
		if(!StringUtils.isValidEmail(txtEmail.getText())){
			txtEmail.setError("Invalid email.");
			return;
		}
		
		String pin = txtPin.getText().toString().trim();
		if(pin.length() == 0){
			txtPin.setError("Please choose a security PIN.");
			return;
		}
        if(pin.length() != 5){
            txtPin.setError("Your pin has to be 5 digits long.");
            return;
        }

		service.setFirstName(firstName);
		service.setLastName(lastName);
		service.setEmail(email);
		service.setUserPin(pin);
		service.setMobileNumber(mobilenumber);
		service.execute();
	}

	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
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
                editor.putString("userMobile", createResult.getAccountResponseWrapper().mobile_number);
                editor.commit();

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
				   
				Intent intent = new Intent(this, HomeActivity.class);
		    	startActivity(intent);
		    	finish();
			}else{
				this.showAlertDialog("Signup Failed", createResult.getErrorMessage());
			}
		}

	}

    public void updateCustomer(CustomerVo customer) {
        CreateAccountService service = new CreateAccountService(this,this);
        service.setFirstName(customer.getFirstName());
        service.setLastName(customer.getLastName());
        service.setEmail(customer.getEmail());
        service.setUserPin(customer.getUserPin());
        service.setGender(customer.getGender());
        service.setBirthDate(customer.getBirthday());
        service.setMobileNumber(customer.getMobileNumber());
        service.execute();
    }


    // To receive the response after authentication
    private final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {
            Log.d("SOCIAL" , "Authentication Successful");

            profileMap = adapter.getUserProfile();
            Log.d("SOCIAL",  "ID = "       + profileMap.getValidatedId());
            Log.d("SOCIAL",  "First Name = "       + profileMap.getFirstName());
            Log.d("SOCIAL",  "Last Name  = " + profileMap.getLastName());
            Log.d("SOCIAL",  "Email      = "       + profileMap.getEmail());
            Log.d("SOCIAL",  "Gender  = " + profileMap.getGender());
            Log.d("SOCIAL",  "Profile Image URL  = " + profileMap.getProfileImageURL());


            String firstName = profileMap.getFirstName();
            String lastName = profileMap.getLastName();
            String email = profileMap.getEmail();
            String validID = profileMap.getValidatedId();
            String gender = profileMap.getGender();
            String birthday = profileMap.getDob().toString();

            createUserFromFacebook(firstName,lastName,email,validID,birthday,gender);
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