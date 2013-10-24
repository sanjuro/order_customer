package com.vosto.customer.accounts.services;

import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;
import org.apache.http.StatusLine;
import org.json.JSONException;
import org.json.JSONObject;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/11
 * Time: 10:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocialSignInService extends RestService {

    private String email;

    public SocialSignInService(OnRestReturn listener, VostoBaseActivity activity){
        super(SERVER_URL + "/users/social_signin", RequestMethod.POST, ResultType.SOCIAL_SIGNIN, listener, activity);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }


    public String getRequestJson(){
        try{
            JSONObject root = new JSONObject();
            root.put("authentication_token", "DXTTTTED2ASDBSD3");
            root.put("email", this.email);
            return root.toString();
        }catch(JSONException e){
            e.printStackTrace();
            return "";
        }
    }

}