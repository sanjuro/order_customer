package com.vosto.customer.orders.activities;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.*;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.CurrentOrderItemAdapter;
import com.vosto.customer.orders.PreviousOrderAdapter;
import com.vosto.customer.orders.services.GetOrderByIdResult;
import com.vosto.customer.orders.services.GetOrderByIdService;
import com.vosto.customer.orders.services.GetPreviousOrdersResult;
import com.vosto.customer.orders.services.GetPreviousOrdersService;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.services.GetStoresService;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;

import static com.vosto.customer.utils.CommonUtilities.STORE_IMAGE_SERVER_URL;

import com.androidquery.AQuery;

public class MyOrdersActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener {

    private OrderVo[] previousOrders;
    private ListView lstPreviousOrders;
    private SlideHolder mSlideHolder;
    AQuery ag = null;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        // Display either a sign in button or the user's name depending if someone is logged in:
        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
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

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        this.lstPreviousOrders = (ListView) findViewById(R.id.lstPreviousOrders);
        fetchPreviousOrders();
	}
	

	/**
	 * Reload the list of previous orders from Vosto.
	 */
	private void fetchPreviousOrders(){
		if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
			this.pleaseWaitDialog.dismiss();
		}
		GetPreviousOrdersService service = new GetPreviousOrdersService(this, this);
		service.execute();
	}
	
	/**
	 * Re-fetches the stored order's data from the service, so that we have the updated state.
	 */
	private void reloadStoredOrder(){
		if(getCurrentOrder() != null){
			if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
				this.pleaseWaitDialog.dismiss();
			}
			GetOrderByIdService service = new GetOrderByIdService(this, this, getCurrentOrder().getId());
			service.execute();
		}
	}
	
       	public void onResume(){
		super.onResume();
		
		// Determine what order / order list we must show, and show it:
        fetchPreviousOrders();
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

		if(result instanceof GetPreviousOrdersResult){
			// We received a list of previous orders:
			GetPreviousOrdersResult ordersResult = (GetPreviousOrdersResult)result;
			this.previousOrders = ordersResult.getOrders();
			if(this.previousOrders == null){
				this.previousOrders = new OrderVo[0];
			}
            Log.d("PREV","Num previous orders: " + this.previousOrders.length);

            ListView list = (ListView)findViewById(R.id.ordersList);
            list.setAdapter(new PreviousOrderAdapter(this, R.layout.previous_order_row, this.previousOrders));
            list.setOnItemClickListener(this);

//			this.lstPreviousOrders.setAdapter(new PreviousOrderAdapter(this, R.layout.previous_order_row, this.previousOrders));
//			this.lstPreviousOrders.setOnItemClickListener(this);

            LinearLayout ordersResults = (LinearLayout)findViewById(R.id.ordersResults);

            if(this.previousOrders.length > 0){
                ordersResults.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }else{
                ordersResults.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
            }
		}

	}


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        Log.d("STO", "Passing store to MyOrderActivity: " + this.previousOrders[position].getId());
        Intent intent = new Intent(this, MyOrderActivity.class);
        intent.putExtra("order", this.previousOrders[position]);
        intent.putExtra("callingActivity", "MyOrderActivity");
        startActivity(intent);
        // finish();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


	@Override
	public void onDismiss(DialogInterface dialog) {
		
	}

}
