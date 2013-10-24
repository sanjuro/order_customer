package com.vosto.customer.loyalties.activities;

import android.content.SharedPreferences;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.loyalties.services.GetLoyaltyCardByIdService;
import com.vosto.customer.loyalties.services.GetLoyaltyCardByIdResult;
import com.vosto.customer.loyalties.vos.LoyaltyCardVo;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.stores.vos.StoreVo;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/06
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewLoyaltyActivity extends VostoBaseActivity implements OnRestReturn {

    private SlideHolder mSlideHolder;
    private LoyaltyCardVo loyaltyCard;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);

        this.loyaltyCard = (LoyaltyCardVo) this.getIntent().getSerializableExtra("loyaltyCard");

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

        GetLoyaltyCardByIdService service = new GetLoyaltyCardByIdService(this, this, this.loyaltyCard.getId());
        service.execute();
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

        if(result instanceof GetLoyaltyCardByIdResult){

            this.loyaltyCard = ((GetLoyaltyCardByIdResult)result).getLoyaltyCard();

            TextView lblLoyaltyName = (TextView)findViewById(R.id.lblLoyaltyName);
            TextView lblLoyaltyDescription = (TextView)findViewById(R.id.lblLoyaltyDescription);
            TextView lblLoyaltyCount = (TextView)findViewById(R.id.lblLoyaltyCount);
            TextView lblLoyaltyPrize = (TextView)findViewById(R.id.lblLoyaltyPrize);
            ImageView viewButton = (ImageView)findViewById(R.id.viewButton);
            RelativeLayout lblLoyaltyCountHolder = (RelativeLayout)findViewById(R.id.lblLoyaltyCountHolder);
            RelativeLayout lblLoyaltyIsWonHolder = (RelativeLayout)findViewById(R.id.lblLoyaltyIsWonHolder);


            lblLoyaltyName.setText(this.loyaltyCard.getLoyalty().getName());
            lblLoyaltyDescription.setText(this.loyaltyCard.getLoyalty().getDescription());

            if (this.loyaltyCard.getIsWon()){
                lblLoyaltyCount.setText("Go get your prize");
                lblLoyaltyCountHolder.setBackgroundColor(Color.parseColor("#27ae60"));
                lblLoyaltyIsWonHolder.setBackgroundColor(Color.parseColor("#2ecc71"));
                // viewButton.setVisibility(View.VISIBLE);
            }else{
                Integer total_left =  Integer.parseInt(this.loyaltyCard.getLoyalty().getWinCount()) -  Integer.parseInt(this.loyaltyCard.getCount()) ;
                lblLoyaltyCount.setText(total_left + " more");
                lblLoyaltyCountHolder.setBackgroundColor(Color.parseColor("#7f8c8d"));
                lblLoyaltyIsWonHolder.setBackgroundColor(Color.parseColor("#95a5a6"));
                // viewButton.setVisibility(View.INVISIBLE);
            }

            // holder.lblLoyaltyWinCount.setText(loyaltyCard.getLoyalty().getWinCount())
            lblLoyaltyPrize.setText(this.loyaltyCard.getLoyalty().getPrize());

            ListView list = (ListView)findViewById(R.id.lstStores);

            StoreListAdapter storeListAdapter = new StoreListAdapter(this, R.layout.store_item_row, this.loyaltyCard.getStoreItems());
            list.setAdapter(storeListAdapter);

        }

    }

}
