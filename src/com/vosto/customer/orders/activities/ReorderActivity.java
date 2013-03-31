package com.vosto.customer.orders.activities;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.services.AuthenticateResult;
import com.vosto.customer.accounts.services.AuthenticationService;
import com.vosto.customer.orders.CurrentOrderItemAdapter;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.orders.services.PlaceOrderService;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.services.GetStoresService;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.GCMUtils;
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
	private ImageView mOrderStatusBadge;
	
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
			
		this.mOrderStatusBadge = (ImageView)findViewById(R.id.order_status_badge);
		//Show the correct status badge based on the order state:
		if(this.order.getState().toLowerCase(Locale.getDefault()).equals("ready")){
			this.mOrderStatusBadge.setImageResource(R.drawable.ready_badge);
		}else if(this.order.getState().toLowerCase(Locale.getDefault()).equals("collected")){
			this.mOrderStatusBadge.setImageResource(R.drawable.collected_badge);
		}else if(this.order.getState().toLowerCase(Locale.getDefault()).equals("in_progress")){
			this.mOrderStatusBadge.setImageResource(R.drawable.in_progress_badge);
		}else if(this.order.getState().toLowerCase(Locale.getDefault()).equals("cancelled")){
			this.mOrderStatusBadge.setImageResource(R.drawable.cancelled_badge);
		}else if(this.order.getState().toLowerCase(Locale.getDefault()).equals("not_collected")){
			this.mOrderStatusBadge.setImageResource(R.drawable.not_collected_badge);
		}else{
			this.mOrderStatusBadge.setImageResource(R.drawable.in_progress_badge);
		}
		
		GetStoresService storesService = new GetStoresService(this, this, order.getStore_id());
		storesService.execute();
	}
	
	
	public void onResume(){
		super.onResume();
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
		
		
		if(result instanceof GetStoresResult){
			GetStoresResult storesResult = (GetStoresResult)result;
			if(storesResult.getStores().length > 0){
				updateStoreDetails(storesResult.getStores()[0]);
			}
		}else if(result instanceof PlaceOrderResult){
			PlaceOrderResult orderResult = (PlaceOrderResult)result;
			if(orderResult.wasOrderCreated()){
				getContext().closeCart();
				this.showAlertDialog("Thank you", "Your order has been placed.");
				saveCurrentOrder(orderResult.getOrder());
			
				Intent intent = new Intent(this, MyOrdersActivity.class);
				startActivity(intent);
				finish();
			}else{
				this.showAlertDialog("Could not place order", orderResult.getErrorMessage());
			}
		}else if(result instanceof AuthenticateResult){
			AuthenticateResult authResult = (AuthenticateResult)result;
			if(authResult.wasAuthenticationSuccessful()){
				sendOrder();
			}else{
				this.showAlertDialog("Invalid PIN", "Please check your PIN and try again.");
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 
	}
	
	private void updateStoreDetails(StoreVo store){
		this.store = store;

        TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
        TextView txtStoreAddress = (TextView)findViewById(R.id.txtStoreAddress);
        TextView txtStoreTelephone = (TextView)findViewById(R.id.txtStoreTelephone);

        txtStoreName.setText(this.store.getName());
        txtStoreAddress.setText(this.store.getAddress());
        txtStoreTelephone.setText(this.store.getManagerContact());
	}
	
	public void reorderClicked(View v){
		if(!GCMUtils.checkGCMAndAlert(this, true)){
			return;
		}
		promptForPin();
	}
	
	public void sendOrder(){
		if(this.order == null || this.store == null){
			return;
		}
		PlaceOrderService service = new PlaceOrderService(this, this);
		service.setOrder(this.order);
		service.setStore(this.store);
		service.execute();
	}
	
	public void promptForPin(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter Pin");
		alert.setMessage("Pin:");

		final EditText pinInput = new EditText(this);
		pinInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		pinInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(5);
		pinInput.setFilters(FilterArray);
			
		alert.setView(pinInput);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String enteredPin = pinInput.getText().toString().trim();
			  	if(enteredPin.equals("")){
			  		return;
			  	}
			
			  //Authenticate with Vosto using the entered pin and stored e-mail adress:
			  authenticateWithVosto(enteredPin);
			  
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();	
	}
	
	/**
	 * Authenticate with Vosto using the entered pin and the stored e-mail address.
	 */
	public void authenticateWithVosto(String enteredPin){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		  AuthenticationService service = new AuthenticationService(this, this);
		  String email = settings.getString("userEmail", "").trim();
		  if(email.equals("")){
			  showAlertDialog("Error", "Could not determine your e-mail address. Please log out and log in again.");
			  return;
		  }
		  service.setEmail(email);
		  service.setPin(enteredPin.trim());
		  service.execute();
	}
	
		
	
	
	
	public void ordersPressed() {
		// TODO Auto-generated method stub
	}

	

	@Override
	public void onDismiss(DialogInterface dialog) {
		
	}

}
