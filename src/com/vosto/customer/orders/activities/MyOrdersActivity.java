package com.vosto.customer.orders.activities;

import java.text.SimpleDateFormat;
import java.util.Locale;

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


public class MyOrdersActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener, OnClickListener {
	
	private ListView currentOrderItemsList;
	private OrderVo currentOrder;
	private TextView lblOrderNumber;
	private TextView lblOrderDate;
	private TextView lblOrderTotal;
	private TextView lblStoreName;
	private TextView lblStoreTelephone;
	private TextView lblStoreAddress;
	private OrderVo[] previousOrders;
	private ListView lstPreviousOrders;
	private LinearLayout orderHistorySection;
	private LinearLayout currentOrderSection;
	private Button currentOrderButton;
    private SlideHolder mSlideHolder;
    private ImageView mOrderStatusBadge;
	
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
		
		this.lstPreviousOrders = (ListView)findViewById(R.id.lstPreviousOrders);
		this.orderHistorySection = (LinearLayout)findViewById(R.id.order_history_section);
		this.currentOrderSection = (LinearLayout)findViewById(R.id.current_order_section);
		this.currentOrderButton = (Button)findViewById(R.id.current_order_button);
		this.mOrderStatusBadge = (ImageView)findViewById(R.id.order_status_badge);
		
		assignModeButtonHandlers();		
		this.currentOrderItemsList = (ListView)findViewById(R.id.current_order_items_list);

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
		if(getIntent().hasExtra("order_id") && getIntent().getIntExtra("order_id", -1) > 0){
			// User has clicked a notification. Load the specified order:
			if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
				this.pleaseWaitDialog.dismiss();
			}
			GetOrderByIdService service = new GetOrderByIdService(this, this, getIntent().getIntExtra("order_id", -1));
			service.execute();
		}else if(getCurrentOrder() != null){
			// We have an order stored on the device. Reload and display:
			reloadStoredOrder();
		}else{
			// We have no specific order to display. Just show the previous orders list:
			currentOrderButton.setVisibility(View.GONE);
			showOrderHistorySection();
			this.previousOrders = new OrderVo[0];
			if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
				//If the dialog is already showing, dismiss it otherwise we will have a duplicate dialog.
				this.pleaseWaitDialog.dismiss();
			}
			fetchPreviousOrders();
		}
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
	
	/**
	 * Takes the order info from the currentOrder member variable and populates the UI,
	 * and then shows the current order section. Also fetches the store details associated with the order.
	 */
	private void showCurrentOrder(){
		if(this.currentOrder == null){
			return;
		}
		this.lblOrderTotal = (TextView)findViewById(R.id.lblOrderTotal);
		this.lblOrderTotal.setText("Total: " + MoneyUtils.getRandString(currentOrder.getTotal()));
		this.currentOrderItemsList.setAdapter(new CurrentOrderItemAdapter(this, R.layout.current_order_item_row, currentOrder.getLineItems()));
		this.lblOrderNumber = (TextView)findViewById(R.id.lblOrderNumber);

        if (currentOrder.getStoreOrderNumber() == null){
            this.lblOrderNumber.setText(currentOrder.getNumber());
        }else{
            this.lblOrderNumber.setText(currentOrder.getStoreOrderNumber());
        }

		this.lblOrderDate = (TextView)findViewById(R.id.lblOrderDate);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
		this.lblOrderDate.setText(format.format(currentOrder.getCreatedAt()));
		this.currentOrderButton.setVisibility(View.VISIBLE);
		
		Log.d("STATE", "Order state: " + this.currentOrder.getState().toLowerCase(Locale.getDefault()));
		
		//Show the correct status badge based on the order state:
		if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("ready")){
			this.mOrderStatusBadge.setImageResource(R.drawable.ready_badge);
		}else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("collected")){
			this.mOrderStatusBadge.setImageResource(R.drawable.collected_badge);
		}else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("in_progress")){
			this.mOrderStatusBadge.setImageResource(R.drawable.in_progress_badge);
		}else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("cancelled")){
			this.mOrderStatusBadge.setImageResource(R.drawable.cancelled_badge);
		}else if(this.currentOrder.getState().toLowerCase(Locale.getDefault()).equals("not_collected")){
			this.mOrderStatusBadge.setImageResource(R.drawable.not_collected_badge);
		}else{
			this.mOrderStatusBadge.setImageResource(R.drawable.in_progress_badge);
		}
		
		showCurrentOrderSection();
		if(this.pleaseWaitDialog != null && this.pleaseWaitDialog.isShowing()){
			this.pleaseWaitDialog.dismiss();
		}
		GetStoresService storesService = new GetStoresService(this, this, currentOrder.getStore_id());
		storesService.execute();
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
		}else if(result instanceof GetOrderByIdResult){
			// We received a specific order that we fetched by id:
            Log.d("PREV","Specific Order");
			this.currentOrder = ((GetOrderByIdResult)result).getOrder();
			
			/*
			 * If this order id is the same as the one stored on the device, 
			 * update the stored order with this latest data.
			 * Otherwise if the order was not found (is null) we just erase the stored order (save it as null)
			 */
			if(this.currentOrder == null || (getCurrentOrder() != null && getCurrentOrder().getId() == this.currentOrder.getId())){
				saveCurrentOrder(this.currentOrder);
			}
			
			if(this.currentOrder == null){
				showOrderHistorySection();
			}
			
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 Intent intent = new Intent(this, ReorderActivity.class);
		 intent.putExtra("order", this.previousOrders[position]);
		 startActivity(intent);
	}
	
	public void currentOrderClicked(View v){
		Log.d("ORD", "Current Order Clicked");
		showCurrentOrder();
	}
	
	public void orderHistoryClicked(View v){
		Log.d("ORD", "Order History Clicked");
		showOrderHistorySection();
	}
	
	private void showCurrentOrderSection(){
		this.orderHistorySection.setVisibility(View.GONE);
		this.currentOrderSection.setVisibility(View.VISIBLE);
		assignModeButtonHandlers();
	}
	
	private void showOrderHistorySection(){
		this.currentOrderSection.setVisibility(View.GONE);
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
		Button btnOrderHistory = (Button)findViewById(R.id.previous_orders_button);
		
		btnCurrentOrder.setOnClickListener(this);
		btnOrderHistory.setOnClickListener(this);
		
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
			currentOrderClicked(v);
		}else if(v.getId() == R.id.previous_orders_button){
            Intent intent = new Intent(this, PreviousOrdersActivity.class);
            startActivity(intent);
		}
	}
}
