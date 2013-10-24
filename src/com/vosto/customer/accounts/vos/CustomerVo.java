package com.vosto.customer.accounts.vos;

import java.util.Date;

public class CustomerVo {
	public int id;
	public String authentication_token;
	public String birthday;
	public Date created_at;
	public Date current_sign_in_at;
	public String current_sign_in_ip;
	public Date last_sign_in_at;
	public String last_sign_in_ip;
	public String email;
	public String encrypted_password;
	public String facebook_id;
	public String first_name;
	public String last_name;
	public String full_name;
	public String mobile_number;
    public String gender;
	public int sign_in_count;
	public String user_pin;
	public String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getMobileNumber() {
        return mobile_number;
    }

    public void setMobileNumber(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUserPin() {
        return user_pin;
    }

    public void setUserPin(String user_pin) {
        this.user_pin = user_pin;
    }
}