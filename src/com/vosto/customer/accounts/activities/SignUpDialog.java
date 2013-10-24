package com.vosto.customer.accounts.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.vosto.customer.R;
import com.vosto.customer.accounts.activities.SignUpActivity;
import com.vosto.customer.accounts.vos.CustomerVo;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/12
 * Time: 12:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SignUpDialog extends Dialog implements android.view.View.OnClickListener {

    private SignUpActivity signUpActivity;
    private CustomerVo customer;
    private String firstName;
    private String lastName;
    private String email;
    private String birthday;
    private String gender;

    public SignUpDialog(SignUpActivity signUpActivity, String firstName, String lastName, String email, String birthday, String gender){
        super(signUpActivity, R.style.DialogSlideAnim);
        this.signUpActivity = signUpActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(signUpActivity).inflate(R.layout.dialog_signup, null);
        setContentView(dialogLayout);

        TextView txtName = (TextView)findViewById(R.id.txtName);
        TextView txtFirstName = (TextView)findViewById(R.id.txtFirstName);
        TextView txtLastName = (TextView)findViewById(R.id.txtLastName);
        TextView txtMobile = (TextView)findViewById(R.id.txtMobile);
        TextView txtEmail = (TextView)findViewById(R.id.txtEmail);
        TextView txtGender = (TextView)findViewById(R.id.txtGender);
        TextView txtBirthday = (TextView)findViewById(R.id.txtBirthday);
        TextView txtUserPin = (TextView)findViewById(R.id.txtUserPin);

        txtName.setText(this.firstName);
        txtName.setVisibility(View.GONE);

        txtFirstName.setText(this.firstName);
        txtFirstName.setVisibility(View.GONE);

        txtLastName.setText(this.lastName);
        txtLastName.setVisibility(View.GONE);

        txtEmail.setText(this.email);
        txtEmail.setVisibility(View.GONE);

        txtGender.setText(this.gender);
        txtGender.setVisibility(View.GONE);

        txtBirthday.setText(this.birthday);
        txtBirthday.setVisibility(View.GONE);

        Button updateButton = (Button)dialogLayout.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.updateButton:
                this.signUpActivity.updateCustomer(this.getCustomer());
                break;
        }
    }

    public CustomerVo getCustomer(){
        TextView txtFirstName = (TextView)findViewById(R.id.txtFirstName);
        TextView txtLastName = (TextView)findViewById(R.id.txtLastName);
        TextView txtMobile = (TextView)findViewById(R.id.txtMobile);
        TextView txtEmail = (TextView)findViewById(R.id.txtEmail);
        TextView txtGender = (TextView)findViewById(R.id.txtGender);
        TextView txtBirthday = (TextView)findViewById(R.id.txtBirthday);
        TextView txtUserPin = (TextView)findViewById(R.id.txtUserPin);

        CustomerVo customer = new CustomerVo();
        if(!txtFirstName.getText().toString().trim().equals("")){
            customer.setFirstName(txtFirstName.getText().toString().trim());
        }

        if(!txtLastName.getText().toString().trim().equals("")){
            customer.setLastName(txtLastName.getText().toString().trim());
        }

        if(!txtMobile.getText().toString().trim().equals("")){
            customer.setMobileNumber(txtMobile.getText().toString().trim());
        }

        if(!txtEmail.getText().toString().trim().equals("")){
            customer.setEmail(txtEmail.getText().toString().trim());
        }

        if(!txtBirthday.getText().toString().trim().equals("")){
            customer.setBirthday(txtBirthday.getText().toString().trim());
        }

        if(!txtGender.getText().toString().trim().equals("")){
            customer.setGender(txtGender.getText().toString().trim());
        }

        if(!txtUserPin.getText().toString().trim().equals("")){
            customer.setUserPin(txtUserPin.getText().toString().trim());
        }

        return customer;
    }
}
