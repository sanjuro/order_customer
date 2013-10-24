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
import com.androidquery.AQuery;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.CurrentOrderItemAdapter;
import com.vosto.customer.orders.PreviousOrderAdapter;
import com.vosto.customer.orders.services.GetOrderByIdResult;
import com.vosto.customer.orders.services.GetOrderByIdService;
import com.vosto.customer.orders.services.GetPreviousOrdersResult;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.services.GetStoresService;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;

import static com.vosto.customer.utils.CommonUtilities.STORE_IMAGE_SERVER_URL;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/10
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyOrderActivity extends VostoBaseActivity implements OnRestReturn {

    private ListView currentOrderItemsList;
    private OrderVo currentOrder;
    private TextView lblOrderNumber;
    private TextView lblOrderDate;
    private TextView lblOrderReady;
    private TextView lblStoreName;
    private TextView lblStoreTelephone;
    private TextView lblStoreAddress;

    private LinearLayout orderHistorySection;
    private LinearLayout currentOrderSection;
    private Button currentOrderButton;
    private SlideHolder mSlideHolder;
    private ImageView mOrderStatusBadge;
    AQuery ag = null;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        this.currentOrder = (OrderVo) this.getIntent().getSerializableExtra("order");
        showCurrentOrder();
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

        if(getIntent().hasExtra("order_id") && getIntent().getIntExtra("order_id", -1) > 0){
            // User has clicked a notification. Load the specified order:
            if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
                this.pleaseWaitDialog.dismiss();
            }
            GetOrderByIdService service = new GetOrderByIdService(this, this, getIntent().getIntExtra("order_id", -1));
            service.execute();
        }else if(result instanceof GetOrderByIdResult){
            // We received a specific order that we fetched by id:
            this.currentOrder = ((GetOrderByIdResult)result).getOrder();

            showCurrentOrder();
        }else if(result instanceof GetStoresResult){
            //We received the store details of the order we want to display:
            Log.d("PREV","Get Store Data ");
            GetStoresResult storesResult = (GetStoresResult)result;
            if(storesResult.getStores().length > 0){
                updateStoreDetails(storesResult.getStores()[0]);
            }
        }
    }

    /**
     * Takes the order info from the currentOrder member variable and populates the UI,
     * and then shows the current order section. Also fetches the store details associated with the order.
     */
    private void showCurrentOrder(){
        if(this.currentOrder == null){
            return;
        }

        // Show the amounts and delivery details:
        TextView lblSubtotal = (TextView)findViewById(R.id.subtotal);
        TextView lblDeliveryCost = (TextView)findViewById(R.id.deliveryCost);
        TextView lblDeliveryAddress = (TextView)findViewById(R.id.deliveryAddress);
        TextView lblDeliveryMethod = (TextView)findViewById(R.id.lblDeliveryMethod);
        TextView lblOrderMins = (TextView)findViewById(R.id.lblOrderMins);
        TextView lblGrandTotal = (TextView)findViewById(R.id.lblTotal);
        TextView orderState = (TextView)findViewById(R.id.orderState);
        TextView deliveryType = (TextView)findViewById(R.id.deliveryType);
        TextView lblStoreName = (TextView)findViewById(R.id.lblStoreName);
        LinearLayout orderStateBackground =  (LinearLayout)findViewById(R.id.orderStateBackground);
        ImageView orderDeliveryImage = (ImageView)findViewById(R.id.orderDeliveryImage);

        lblSubtotal.setText("Subtotal: " + MoneyUtils.getRandString(currentOrder.getSubtotalBeforeDelivery()));

        if(currentOrder.getDeliveryAddress() != null && !currentOrder.getDeliveryAddress().isEmpty() && currentOrder.getAdjustmentTotal() != null){
            lblDeliveryCost.setText(MoneyUtils.getRandString(currentOrder.getAdjustmentTotal()));
            lblDeliveryAddress.setText(currentOrder.getDeliveryAddress().toString());
            lblDeliveryMethod.setText("Delivery");
            deliveryType.setText("Delivery");
            orderDeliveryImage.setImageResource(R.drawable.delivery_icon);
        }else{
            lblDeliveryCost.setText("R0.00");
            lblDeliveryMethod.setText("Collect");
            deliveryType.setText("Collect");
            orderDeliveryImage.setImageResource(R.drawable.collection_icon);
        }

        lblGrandTotal.setText("Total: " + MoneyUtils.getRandString(currentOrder.getTotal()));

        ListView list = (ListView)findViewById(R.id.current_order_items_list);
        list.setAdapter(new CurrentOrderItemAdapter(this, R.layout.current_order_item_row, currentOrder.getLineItems()));

        this.lblOrderNumber = (TextView)findViewById(R.id.lblOrderNumber);

        if (currentOrder.getStoreOrderNumber() == null){
            this.lblOrderNumber.setText(currentOrder.getNumber());
        }else{
            this.lblOrderNumber.setText(currentOrder.getStoreOrderNumber());
        }

        this.lblOrderReady = (TextView)findViewById(R.id.lblOrderReady);
        if (currentOrder.getTimeToReady() == null){
            this.lblOrderReady.setText("Store to confirm");
        }else {
            this.lblOrderReady.setText(currentOrder.getTimeToReady());
        }

        this.lblOrderDate = (TextView)findViewById(R.id.lblOrderDate);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        this.lblOrderDate.setText("Ordered At: " + format.format(currentOrder.getCreatedAt()));

        lblStoreName.setText(currentOrder.getStoreName());

        if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("ready")){
            orderState.setText("READY");
            orderStateBackground.setBackgroundColor(Color.parseColor("#92d050"));
        }else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("collected")){
            orderState.setText("COLLECTED");
            orderStateBackground.setBackgroundColor(Color.parseColor("#92d050"));
        }else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("in_progress")){
            orderState.setText("IN PROGRESS");
            orderStateBackground.setBackgroundColor(Color.parseColor("#ffc000"));
        }else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("cancelled")){
            orderState.setText("CANCELLED");
            orderStateBackground.setBackgroundColor(Color.parseColor("#f67846"));
        }else{
            lblOrderMins.setVisibility(View.GONE);
            orderState.setText("UNCONFIRMED");
            orderStateBackground.setBackgroundColor(Color.parseColor("#c00000"));
        }

//        if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
//            this.pleaseWaitDialog.dismiss();
//        }
//        GetStoresService storesService = new GetStoresService(this, this, currentOrder.getStore_id());
//        storesService.execute();
    }

    private void updateStoreDetails(StoreVo store){
        ag = new AQuery(this.findViewById(android.R.id.content));

        String imageUrl = STORE_IMAGE_SERVER_URL + store.getStoreImage();
        ag.id(R.id.lblStoreImage).image(imageUrl, false, false, 0, 0, null, AQuery.FADE_IN);

        this.lblStoreName = (TextView)findViewById(R.id.lblStoreName);
        this.lblStoreName.setText(store.getName());

        this.lblStoreTelephone = (TextView)findViewById(R.id.lblStoreTelephone);
        this.lblStoreTelephone.setText(store.getManagerContact());

        this.lblStoreAddress = (TextView)findViewById(R.id.lblStoreAddress);
        this.lblStoreAddress.setText(store.getAddress());
    }

}
