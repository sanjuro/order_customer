package com.vosto.customer.orders.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.PreviousOrderAdapter;
import com.vosto.customer.orders.services.GetPreviousOrdersResult;
import com.vosto.customer.orders.services.GetPreviousOrdersService;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.vos.StoreVo;


public class PreviousOrdersActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener, OnClickListener {

    private TextView lblStoreName;
    private TextView lblStoreTelephone;
    private TextView lblStoreAddress;
    private OrderVo[] previousOrders;
    private ListView lstPreviousOrders;
    private LinearLayout orderHistorySection;
    private Button currentOrderButton;
    private SlideHolder mSlideHolder;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);

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

        this.lstPreviousOrders = (ListView)findViewById(R.id.lstPreviousOrders);
        this.orderHistorySection = (LinearLayout)findViewById(R.id.order_history_section);
        this.currentOrderButton = (Button)findViewById(R.id.current_order_button);

        assignModeButtonHandlers();

        // Determine what order / order list we must show, and show it:
        initialize();
    }


    /**
     * If an order_id has been passed through the intent, it means we are coming from a notification,
     * so we must fetch and display that specific order.
     * Otherwise, if we have an order stored on the device, we display that order.
     * Otherwise we have no order to display, so we just display the previous orders list.
     */
    private void initialize(){
        // We have no specific order to display. Just show the previous orders list:
        showOrderHistorySection();
        this.previousOrders = new OrderVo[0];
        if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
            //If the dialog is already showing, dismiss it otherwise we will have a duplicate dialog.
            this.pleaseWaitDialog.dismiss();
        }
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


    public void onResume(){
        super.onResume();

        // Determine what order / order list we must show, and show it:
        initialize();
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
            this.lstPreviousOrders.setAdapter(new PreviousOrderAdapter(this, R.layout.previous_order_row, this.previousOrders));
            this.lstPreviousOrders.setOnItemClickListener(this);
        }else if(result instanceof GetStoresResult){
            //We received the store details of the order we want to display:
            Log.d("PREV","Get Store Data ");
            GetStoresResult storesResult = (GetStoresResult)result;
            if(storesResult.getStores().length > 0){
                updateStoreDetails(storesResult.getStores()[0]);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

    }

    public void orderHistoryClicked(View v){
        Log.d("ORD", "Order History Clicked");
        showOrderHistorySection();
    }
    private void showOrderHistorySection(){

        this.orderHistorySection.setVisibility(View.VISIBLE);
        this.lstPreviousOrders.setVisibility(View.VISIBLE);

        assignModeButtonHandlers();
        fetchPreviousOrders();
    }

    private void updateStoreDetails(StoreVo store){
        this.lblStoreName = (TextView)findViewById(R.id.lblStoreName);
        this.lblStoreName.setText(store.getName());

        this.lblStoreTelephone = (TextView)findViewById(R.id.lblStoreTelephone);
        this.lblStoreTelephone.setText(store.getManagerContact());

        this.lblStoreAddress = (TextView)findViewById(R.id.lblStoreAddress);
        this.lblStoreAddress.setText(store.getAddress());
    }

    private void assignModeButtonHandlers(){
        Button btnCurrentOrder = (Button)findViewById(R.id.current_order_button);
        btnCurrentOrder.setOnClickListener(this);
    }


    public void ordersPressed() {
        // TODO Auto-generated method stub
    }



    @Override
    public void onDismiss(DialogInterface dialog) {

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.current_order_button){
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
            finish();
        }else if(v.getId() == R.id.previous_orders_button){
            orderHistoryClicked(v);
        }
    }
}
