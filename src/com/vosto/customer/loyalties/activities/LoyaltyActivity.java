package com.vosto.customer.loyalties.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.agimind.widget.SlideHolder;

import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.loyalties.services.GetLoyaltyCardsService;
import com.vosto.customer.loyalties.services.GetLoyaltyCardsResult;
import com.vosto.customer.loyalties.LoyaltyCardListAdapter;
import com.vosto.customer.loyalties.vos.LoyaltyCardVo;
import com.vosto.customer.utils.NetworkUtils;


/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/09/08
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoyaltyActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {

    private LoyaltyCardVo[] loyaltyCards;
    private SlideHolder mSlideHolder;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalties);

        GetLoyaltyCardsService service = new GetLoyaltyCardsService(this, this);
        service.execute();

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
            //User logged in:
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

        }else{
            //User not logged in:
        }

    }

    /**
     * Called from within the base RestService after a rest call completes.
     * @param result Can be any result type. This function should check the type and handle accordingly.
     */
    @Override
    public void onRestReturn(RestResult result) {
        if(result == null){
            return;
        }

        Log.d("LOYALTY", "Loyalty Rest Return");

        if(result instanceof GetLoyaltyCardsResult){

            GetLoyaltyCardsResult getLoyaltyCardsResult = (GetLoyaltyCardsResult)result;
            this.loyaltyCards = getLoyaltyCardsResult.getLoyaltyCards();

            Log.d("LOYALTY", "Got Loyalty Card Count:" + this.loyaltyCards.length);

            LinearLayout storesResults = (LinearLayout)findViewById(R.id.loyaltyCardResults);

            if(this.loyaltyCards.length > 0){
                storesResults.setVisibility(View.GONE);
            }else{
                storesResults.setVisibility(View.VISIBLE);
            }

            ListView list = (ListView)findViewById(R.id.lstLoyaltyCards);
            list.setAdapter(new LoyaltyCardListAdapter(this, R.layout.loyaltycard_item_row, this.loyaltyCards));

            list.setOnItemClickListener(this);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        if(!NetworkUtils.isNetworkAvailable(this)){
            this.showAlertDialog("Connection Error", "Please connect to the internet.");
            return;
        }
        Log.d("STO", "Passing store to LoyaltyActivity: " + this.loyaltyCards[position].getId());
        Intent intent = new Intent(this, ViewLoyaltyActivity.class);
        intent.putExtra("loyaltyCard", this.loyaltyCards[position]);
        intent.putExtra("callingActivity", "ViewLoyaltyActivity");
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
