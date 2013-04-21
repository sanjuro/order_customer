package com.vosto.customer.accounts.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.accounts.vos.CustomerVo;
import com.vosto.customer.services.IRestResult;
import com.vosto.customer.services.RestResult;

public class UpdateCustomerResult extends RestResult implements IRestResult {

    private JSONObject jsonObj;
    private CustomerVo customer;
    private boolean updateSuccessful;
    private String errorMessage;

    public UpdateCustomerResult(){
        super();
    }

    public UpdateCustomerResult(int responseCode, String responseJson){
        super(responseCode, responseJson);
    }

    public JSONObject getJSONObject(){
        return this.jsonObj;
    }

    public CustomerVo getCustomer(){
        return this.customer;
    }

    public boolean wasUpdateSuccessful(){
        return this.updateSuccessful;
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }


    @Override
    public boolean processJsonAndPopulate(){
        try{
            this.jsonObj = new JSONObject(this.getResponseJson());
            if(!this.jsonObj.isNull("error")){
                this.updateSuccessful = false;
                this.errorMessage = this.jsonObj.getString("detail");
                return true;
            }

            this.updateSuccessful = true;
            this.customer = new CustomerVo();
            this.customer.authentication_token = this.jsonObj.getString("authentication_token");
            this.customer.full_name = this.jsonObj.getString("full_name");
            this.customer.first_name = this.jsonObj.getString("first_name");
            this.customer.mobile_number= this.jsonObj.getString("mobile_number");
            this.customer.email = this.jsonObj.getString("email");

            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
    }

}