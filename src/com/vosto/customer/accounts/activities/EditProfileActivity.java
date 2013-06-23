package com.vosto.customer.accounts.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.pages.activities.ContactActivity;
import com.vosto.customer.pages.activities.TermsActivity;
import com.vosto.customer.accounts.services.*;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.StringUtils;

/**
 * The Sign In screen where an existing user logs in.
 *
 */
public class EditProfileActivity extends VostoBaseActivity implements OnRestReturn {

    private SlideHolder mSlideHolder;
//    private TextView tvDisplayDate;
//    private DatePicker dpResult;
//
//    private int year;
//    private int month;
//    private int day;
//
//    static final int DATE_DIALOG_ID = 999;

    @Override
    public void onCreate(Bundle args)
    {
        super.onCreate(args);
        setContentView(R.layout.activity_editprofile);

        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);

        EditText txtName = (EditText)findViewById(R.id.txtName);
        EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
        EditText txtMobileNumber = (EditText)findViewById(R.id.txtMobileNumber);

        txtName.setText(settings.getString("userName", "user"));
        txtEmail.setText(settings.getString("userEmail", "user"));
        txtMobileNumber.setText(settings.getString("userMobile", "user"));
        

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
            //User logged in:
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

            View toggleView = findViewById(R.id.menuButton);
            toggleView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSlideHolder.toggle();
                }
            });
        }else{
            //User not logged in:

        }
    }

    /**
     * Redirects the user to signup if she does not have an account yet.
     * @param v The signup button instance.
     */
    public void saveClicked(View v){

        EditText txtName = (EditText)findViewById(R.id.txtName);
        EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
        EditText txtMobileNumber = (EditText)findViewById(R.id.txtMobileNumber);
//        Spinner spinnerGender = (Spinner)findViewById(R.id.spinnerGender);
//        DatePicker dpResult = (DatePicker)findViewById(R.id.dpResult);

        String name = txtName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String mobile_number = txtMobileNumber.getText().toString().trim();
//        String gender = spinnerGender.getSelectedItem().toString();
//        int day = dpResult.getDayOfMonth();
//        int month = dpResult.getMonth() + 1;
//        int year = dpResult.getYear();
//
//        SimpleDateFormat birthday = new SimpleDateFormat("dd/MM/yyyy");
//        Date birthday_date = birthday.parse("21/12/2012");
//
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);


        boolean inputValid = true;
        if(name.equals("")){
            txtName.setError("Enter your full name.");
            inputValid = false;
        }
        if(email.equals("")){
            txtEmail.setError("Enter your e-mail.");
            inputValid = false;
        }
        if(mobile_number.equals("")){
            txtMobileNumber.setError("Enter your mobile number.");
            inputValid = false;
        }

        if(!inputValid){
            return;
        }

        //Make the REST call:
        UpdateCustomerService service = new UpdateCustomerService(this, this);
        service.setName(name);
        service.setEmail(email);
        service.setMobileNumber(mobile_number);
//        service.setGender(gender);
//        service.setBirthday(birthday_date);
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
        if(result instanceof UpdateCustomerResult){
            processUpdateResult((UpdateCustomerResult)result);
        }
        if(result instanceof ResetPasswordResult){
        	processResetPasswordResult((ResetPasswordResult)result);
        }
    }

    /**
     * Called when the authentication rest call returns.
     * If the auth is successful, save the token, etc and redirect to the home screen.
     * @param authResult
     */
    private void processUpdateResult(UpdateCustomerResult authResult){
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userToken", "");
        editor.putString("userName", "");
        editor.commit();

        if(!authResult.wasUpdateSuccessful()){
            this.showAlertDialog("Login Failed", authResult.getErrorMessage());
        }else{
            // Save the auth token in the app's shared preferences.
            editor = settings.edit();
            editor.putString("userToken", authResult.getCustomer().authentication_token);
            editor.putString("userName", authResult.getCustomer().full_name);
            editor.putString("userEmail", authResult.getCustomer().email);
            editor.putString("userMobile", authResult.getCustomer().mobile_number);
            editor.commit();

            // send toast message
            CharSequence text = "You have successfully updated your profile.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();

        }
    }
    
    private void processResetPasswordResult(ResetPasswordResult result){
    	if(result.wasSuccessful()){
    		this.showAlertDialog("PIN reset successful", "An e-mail has been sent to you with your new PIN.");
    	}else{
    		this.showAlertDialog("ERROR", "Could not reset your PIN. Please check the e-mail address and try again.");
    	}
    }

    public void resetPinPressed(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter your e-mail");
        alert.setMessage("E-mail:");

        // Space to enter email address:
        final EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alert.setView(emailInput);

        alert.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	if(!StringUtils.isValidEmail(emailInput.getText())){
            		emailInput.setError("Invalid e-mail");
            		EditProfileActivity.this.showAlertDialog("Invalid e-mail", "Please try again.");
            	    return;
            	}
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

    public void faqPressed(View v){

    }

    public void contactVostoPressed(View v) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void termsPressed(View v){
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    /**
     * Called after the user has entered an email and confirmed to reset pin.
     * @param email
     */
    public void confirmResetPassword(String email){
        ResetPasswordService service = new ResetPasswordService(this, this, email);
        service.execute();
    }

//    @Override
//    protected Dialog onCreateDialog(int id) {
//        switch (id) {
//            case DATE_DIALOG_ID:
//                // set date picker as current date
//                return new DatePickerDialog(this, datePickerListener,
//                        year, month,day);
//        }
//        return null;
//    }
//
//    private DatePickerDialog.OnDateSetListener datePickerListener
//            = new DatePickerDialog.OnDateSetListener() {
//
//        // when dialog box is closed, below method will be called.
//        public void onDateSet(DatePicker view, int selectedYear,
//                              int selectedMonth, int selectedDay) {
//            year = selectedYear;
//            month = selectedMonth;
//            day = selectedDay;
//
//            // set selected date into textview
//            tvDisplayDate.setText(new StringBuilder().append(month + 1)
//                    .append("-").append(day).append("-").append(year)
//                    .append(" "));
//
//            // set selected date into datepicker also
//            dpResult.init(year, month, day, null);
//
//        }
//    };


}