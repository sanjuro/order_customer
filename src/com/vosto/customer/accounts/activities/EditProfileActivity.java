package com.vosto.customer.accounts.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.services.*;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;

/**
 * The Sign In screen where an existing user logs in.
 *
 */
public class EditProfileActivity extends VostoBaseActivity implements OnRestReturn {

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
    }

    /**
     * Redirects the user to signup if she does not have an account yet.
     * @param v The signup button instance.
     */
    public void saveClicked(View v){
        this.pleaseWaitDialog = ProgressDialog.show(this, "Updating", "Please wait...", true);

        EditText txtName = (EditText)findViewById(R.id.txtName);
        EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
//        Spinner spinnerGender = (Spinner)findViewById(R.id.spinnerGender);
//        DatePicker dpResult = (DatePicker)findViewById(R.id.dpResult);

        String name = txtName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
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

        if(!inputValid){
            return;
        }

        //Make the REST call:
        UpdateCustomerService service = new UpdateCustomerService(this);
        service.setName(name);
        service.setEmail(email);
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
        this.pleaseWaitDialog.dismiss();
        if(result == null){
            this.showAlertDialog("Login Failed", "Please try again.");
            return;
        }
        if(result instanceof UpdateCustomerResult){
            processUpdateResult((UpdateCustomerResult)result);
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
        editor.putString("userPin", "");
        editor.commit();

        if(!authResult.wasUpdateSuccessful()){
            this.showAlertDialog("Login Failed", authResult.getErrorMessage());
        }else{
            // Save the auth token in the app's shared preferences.
            editor = settings.edit();
            editor.putString("userToken", authResult.getCustomer().authentication_token);
            editor.putString("userName", authResult.getCustomer().first_name);
            editor.putString("userPin", authResult.getCustomer().user_pin);
            editor.commit();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
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