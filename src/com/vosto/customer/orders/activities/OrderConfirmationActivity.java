package com.vosto.customer.orders.activities;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.CurrentOrderItemAdapter;
import com.vosto.customer.orders.services.GetOrderByIdResult;
import com.vosto.customer.orders.services.GetOrderByIdService;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.HomeActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;
import com.vosto.customer.utils.NetworkUtils;

import static com.vosto.customer.utils.CommonUtilities.IMAGE_SERVER_URL;

import com.androidquery.AQuery;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/04/21
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderConfirmationActivity extends VostoBaseActivity implements OnRestReturn, DialogInterface.OnDismissListener {

    private OrderVo currentOrder;
    private ListView currentOrderItemsList;
    private StoreVo store;
    private String orderNumber;
    private TextView lblOrderNumber;
    private TextView lblOrderDate;
    private TextView lblOrderTotal;
    private TextView lblStoreName;
    private TextView lblStoreTelephone;
    private TextView lblStoreAddress;
    private SlideHolder mSlideHolder;
    AQuery ag = null;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        ag = new AQuery(this.findViewById(android.R.id.content));

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

        this.currentOrder = (OrderVo) this.getIntent().getSerializableExtra("order");
        this.store = (StoreVo) this.getIntent().getSerializableExtra("store");
        this.orderNumber = (String) this.getIntent().getSerializableExtra("order_number");
        showCurrentOrder();
    }

    private void initialize(){
        if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
            this.pleaseWaitDialog.dismiss();
        }
        GetOrderByIdService service = new GetOrderByIdService(this, this, getIntent().getIntExtra("order_id", -1));
        service.execute();
    }

    private void showCurrentOrder(){
        if(this.currentOrder == null){
            return;
        }
        this.currentOrderItemsList = (ListView)findViewById(R.id.current_order_items_list);

        // Show the amounts and delivery details:
     	TextView lblSubtotal = (TextView)findViewById(R.id.subtotal);
     	TextView lblDeliveryCost = (TextView)findViewById(R.id.deliveryCost);
     	TextView lblDeliveryAddress = (TextView)findViewById(R.id.deliveryAddress);
     	TextView lblDeliveryMethod = (TextView)findViewById(R.id.lblDeliveryMethod);
     	TextView lblGrandTotal = (TextView)findViewById(R.id.lblTotal);
     				
     	lblSubtotal.setText("Subtotal: " + MoneyUtils.getRandString(this.currentOrder.getSubtotalBeforeDelivery()));
     				
     	if(this.currentOrder.getDeliveryAddress() != null && !this.currentOrder.getDeliveryAddress().isEmpty() && this.currentOrder.getAdjustmentTotal() != null){
     		lblDeliveryCost.setText(MoneyUtils.getRandString(this.currentOrder.getAdjustmentTotal()));
     		lblDeliveryMethod.setText("Delivery");
     		lblDeliveryAddress.setText(this.currentOrder.getDeliveryAddress().toString());
     	}else{
     		lblDeliveryCost.setText("R0.00");
     		lblDeliveryMethod.setText("Collected in-store");
     	}
     				
     	lblGrandTotal.setText("Total: " + MoneyUtils.getRandString(this.currentOrder.getTotal()));
     		
        this.currentOrderItemsList.setAdapter(new CurrentOrderItemAdapter(this, R.layout.current_order_item_row, this.currentOrder.getLineItems()));

        this.lblOrderNumber = (TextView)findViewById(R.id.lblOrderNumber);
        this.lblOrderNumber.setText("Your order: " + this.orderNumber + " has been confirmed.");

        String imageUrl = IMAGE_SERVER_URL + store.getStoreImage();
        ag.id(R.id.lblStoreImage).image(imageUrl, false, false, 0, 0, null, AQuery.FADE_IN);

        this.lblStoreName = (TextView)findViewById(R.id.lblStoreName);
        this.lblStoreName.setText(this.store.getName());

        this.lblStoreTelephone = (TextView)findViewById(R.id.lblStoreTelephone);
        this.lblStoreTelephone.setText(this.store.getManagerContact());

        this.lblStoreAddress = (TextView)findViewById(R.id.lblStoreAddress);
        this.lblStoreAddress.setText(this.store.getAddress());
    }

    @Override
    public void onRestReturn(RestResult result) {

    }


    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    public void continueButtonPressed(View v){
        if(!NetworkUtils.isNetworkAvailable(this)){
            this.showAlertDialog("Connection Error", "Please connect to the internet.");
            return;
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void viewOrdersButtonPressed(View v){
        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);
    }
}
