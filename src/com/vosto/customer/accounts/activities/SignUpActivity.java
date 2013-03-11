package com.vosto.customer.accounts.activities;

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
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends Activity implements OnRestReturn {
	
	private ProgressDialog pleaseWaitDialog;
	
	@Override
    protected void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_signup);   
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
		
		String email = txtEmail.getText().toString().trim().toLowerCase();
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
				   editor.commit();
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