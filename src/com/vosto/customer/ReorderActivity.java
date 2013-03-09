package com.vosto.customer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.vosto.customer.orders.CurrentOrderItemAdapter;
import com.vosto.customer.services.GetStoresResult;
import com.vosto.customer.services.GetStoresService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.PlaceOrderResult;
import com.vosto.customer.services.PlaceOrderService;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.OrderVo;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class ReorderActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener {
	
	private OrderVo order;
	private StoreVo store;
	private ListView orderItemsList;
	private TextView lblOrderNumber;
	private TextView lblOrderDate;
	private TextView lblOrderTotal;
	private TextView lblStoreName;
	private TextView lblStoreTelephone;
	private TextView lblStoreAddress;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reorder);
		
		this.orderItemsList = (ListView)findViewById(R.id.order_items_list);
		this.orderItemsList.setOnItemClickListener(this);
		
		this.order = (OrderVo)getIntent().getSerializableExtra("order");
		
		this.lblOrderTotal = (TextView)findViewById(R.id.lblOrderTotal);
		this.lblOrderTotal.setText("Total: " + MoneyUtils.getRandString(order.getTotal()));
		this.orderItemsList.setAdapter(new CurrentOrderItemAdapter(this, R.layout.current_order_item_row, order.getLineItems()));
		this.lblOrderNumber = (TextView)findViewById(R.id.lblOrderNumber);
		this.lblOrderNumber.setText(order.getNumber());
		this.lblOrderDate = (TextView)findViewById(R.id.lblOrderDate);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
		this.lblOrderDate.setText(format.format(order.getCreatedAt()));
				
		GetStoresService storesService = new GetStoresService(this, order.getStore_id());
		storesService.execute();
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
		
		
		if(result instanceof GetStoresResult){
			GetStoresResult storesResult = (GetStoresResult)result;
			if(storesResult.getStores().length > 0){
				updateStoreDetails(storesResult.getStores()[0]);
			}
		}else if(result instanceof PlaceOrderResult){
			showAlertDialog("Order Placed", ((PlaceOrderResult)result).getOrder().getNumber());
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
	
	private void updateStoreDetails(StoreVo store){
		this.store = store;
		this.lblStoreName = (TextView)findViewById(R.id.lblStoreName);
		this.lblStoreName.setText(store.getName());
		
		this.lblStoreTelephone = (TextView)findViewById(R.id.lblStoreTelephone);
		this.lblStoreTelephone.setText(store.getManagerContact());
		
		this.lblStoreAddress = (TextView)findViewById(R.id.lblStoreAddress);
		this.lblStoreAddress.setText(store.getAddress());
	}
	
	public void reorderClicked(View v){
		if(this.order == null || this.store == null){
			return;
		}
		this.pleaseWaitDialog = ProgressDialog.show(this, "Sending Order", "Please wait...", true);
		PlaceOrderService service = new PlaceOrderService(this, this);
		service.setOrder(this.order);
		service.setStore(this.store);
		service.execute();
	}
	
	
	public void ordersPressed() {
		// TODO Auto-generated method stub
	}

	

	@Override
	public void onDismiss(DialogInterface dialog) {
		
	}

}
