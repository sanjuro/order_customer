package com.vosto.customer.loyalties.services;

import android.util.Log;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RequestMethod;
import com.vosto.customer.services.RestService;
import com.vosto.customer.services.ResultType;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/06
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetLoyaltyCardByIdService extends RestService {

    public GetLoyaltyCardByIdService(OnRestReturn listener, VostoBaseActivity context, int loyaltyCardId){
        super(SERVER_URL + "/loyalty_cards/" + loyaltyCardId + "?authentication_token=" + context.getAuthenticationToken(), RequestMethod.GET, ResultType.GET_LOYALTY_CARD_BY_ID, listener, context);
    }

}
