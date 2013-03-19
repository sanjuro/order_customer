package com.vosto.customer.accounts.services;

import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

public class UpdateCustomerService extends RestService {

    private String authToken;
    private String name;
    private String email;
//    private String gender;
//    private String birthday;

    public UpdateCustomerService(OnRestReturn listener){
        super("http://107.22.211.58:9000/api/v1/customers/update", RequestMethod.POST, ResultType.UPDATE_CUSTOMER, listener);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getBirthday() {
//        return birthday;
//    }
//
//    public void setBirthday(String birthday) {
//        this.birthday = birthday;
//    }

    public String getRequestJson(){
        try{
            JSONObject root = new JSONObject();
            JSONObject user_object = new JSONObject();

            user_object.put("full_name", this.name);
            user_object.put("email", this.email);
//            user_object.put("gender", this.gender);
//            user_object.put("birthday", this.birthday);

            root.put("authentication_token", "b5a27178456753ba773d83666d276631");
            root.put("user", user_object);

            return root.toString();
        }catch(JSONException e){
            e.printStackTrace();
            return "";
        }

    }

    @Override
    protected UpdateCustomerResult getRestResult(StatusLine statusLine, String responseJson){
        UpdateCustomerResult result = new UpdateCustomerResult(200, responseJson);
        result.processJsonAndPopulate();
        return result;
    }



}