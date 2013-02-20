package com.vosto.customer.accounts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.ParseException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vosto.customer.DatePickerFragment;
import com.vosto.customer.MainTabActivity;
import com.vosto.customer.R;
import com.vosto.customer.services.CreateAccountResult;
import com.vosto.customer.services.CreateAccountService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class CreateAccountActivity extends FragmentActivity implements OnRestReturn {
	
	private EditText txtFirstName;
	private EditText txtLastName;
	private EditText txtEmail;
	private EditText txtMobileNumber;
	private EditText txtUserPin;
	private TextView txtBirthDate;
	private Date birthDate;
	
	private ProgressDialog pleaseWaitDialog;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		txtFirstName = (EditText)findViewById(R.id.textFirstName);
		txtLastName = (EditText)findViewById(R.id.textLastName);
		txtEmail = (EditText)findViewById(R.id.textEmail);
		txtUserPin = (EditText)findViewById(R.id.textUserPin);
		txtMobileNumber = (EditText)findViewById(R.id.textMobileNumber);
		txtBirthDate = (TextView)findViewById(R.id.textBirthDate);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_account, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createAccount(View view){
		this.pleaseWaitDialog = ProgressDialog.show(this, "Creating Account", "Please wait...", true);
		CreateAccountService service = new CreateAccountService(this);
		service.setFirstName(txtFirstName.getText().toString());
		service.setLastName(txtLastName.getText().toString());
		service.setEmail(txtEmail.getText().toString());
		service.setMobileNumber(txtMobileNumber.getText().toString());
		service.setUserPin(txtUserPin.getText().toString());
		if(this.birthDate != null){
			service.setBirthDate(this.birthDate);
		}else{
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 1, 1);
			service.setBirthDate(cal.getTime());
		}
		service.execute();
	}
	
	public void showBirthDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	public void birthDateChosen(int day, int month, int year){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		this.birthDate = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		txtBirthDate.setText(format.format(this.birthDate));
	}

	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			return;
		}
		
		if(result instanceof CreateAccountResult){
			processAccountCreatedResult((CreateAccountResult)result);
		}
		
	
		
	}
	
	
	private void processAccountCreatedResult(CreateAccountResult result){
		if(result == null){
			return;
		}
		
		if(!result.wasAccountCreated()){
			Toast.makeText(this, "ERROR: Could not create customer. (" + result.getStatusCode() + ")", Toast.LENGTH_LONG).show();
			return;
		}
		
			Intent intent = new Intent(this, MainTabActivity.class);
    	   startActivity(intent);
    	   finish();
		
		//Toast.makeText(this, "Customer Created: " + result.getAccountResponseWrapper().customer.id, Toast.LENGTH_LONG).show();
	}
	

 
 

}
