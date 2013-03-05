package com.vosto.customer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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

import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItemAdapter;
import com.vosto.customer.orders.CurrentOrderItemAdapter;
import com.vosto.customer.orders.PreviousOrderAdapter;
import com.vosto.customer.services.GetPreviousOrdersResult;
import com.vosto.customer.services.GetPreviousOrdersService;
import com.vosto.customer.services.GetStoresResult;
import com.vosto.customer.services.GetStoresService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.OrderVo;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class MyOrdersActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener, OnClickListener {
	
	private ListView currentOrderItemsList;
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
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);
		
		this.lstPreviousOrders = (ListView)findViewById(R.id.lstPreviousOrders);
		this.orderHistorySection = (LinearLayout)findViewById(R.id.order_history_section);
		this.currentOrderSection = (LinearLayout)findViewById(R.id.current_order_section);
		
		assignModeButtonHandlers();
		
		this.currentOrderItemsList = (ListView)findViewById(R.id.current_order_items_list);
		this.currentOrderItemsList.setOnItemClickListener(this);
		
		OrderVo currentOrder = getCurrentOrder();
		Button currentOrderButton = (Button)findViewById(R.id.current_order_button);
		if(currentOrder == null){
			currentOrderButton.setVisibility(View.GONE);
			showOrderHistorySection();
		}else{
			this.lblOrderTotal = (TextView)findViewById(R.id.lblOrderTotal);
			this.lblOrderTotal.setText("Total: " + MoneyUtils.getRandString(currentOrder.getTotal()));
			this.currentOrderItemsList.setAdapter(new CurrentOrderItemAdapter(this, R.layout.current_order_item_row, currentOrder.getLineItems()));
			this.lblOrderNumber = (TextView)findViewById(R.id.lblOrderNumber);
			this.lblOrderNumber.setText(currentOrder.getNumber());
			this.lblOrderDate = (TextView)findViewById(R.id.lblOrderDate);
			 SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
			this.lblOrderDate.setText(format.format(currentOrder.getCreatedAt()));
			currentOrderButton.setVisibility(View.VISIBLE);
			showCurrentOrderSection();
			GetStoresService storesService = new GetStoresService(this, currentOrder.getStore_id());
			storesService.execute();
		}
		
	
		
		
		
	

		this.previousOrders = new OrderVo[0];
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Fetching Orders", "Please wait...", true);
		GetPreviousOrdersService service = new GetPreviousOrdersService(this, this);
		service.execute();
	}
	
	
	public void onResume(){
		super.onResume();
	}

	
	@Override
	public void onRestReturn(RestResult result) {
		if(this.pleaseWaitDialog != null){
			this.pleaseWaitDialog.dismiss();
		}
		if(result == null){
			return;
		}
		
		if(result instanceof GetPreviousOrdersResult){
			GetPreviousOrdersResult ordersResult = (GetPreviousOrdersResult)result;
			this.previousOrders = ordersResult.getOrders();
			Log.d("PREV","Num previous orders: " + this.previousOrders.length);
			this.lstPreviousOrders.setAdapter(new PreviousOrderAdapter(this, R.layout.previous_order_row, this.previousOrders));
		}else if(result instanceof GetStoresResult){
			GetStoresResult storesResult = (GetStoresResult)result;
			if(storesResult.getStores().length > 0){
				updateStoreDetails(storesResult.getStores()[0]);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 
	}
	
	public void showAlertDialog(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setOnDismissListener(this);
        alert.show();
	}
	
	public void currentOrderClicked(View v){
		Log.d("ORD", "Current Order Clicked");
		showCurrentOrderSection();
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
			orderHistoryClicked(v);
		}
	} 
}
