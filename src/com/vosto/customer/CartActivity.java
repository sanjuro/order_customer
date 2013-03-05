package com.vosto.customer;

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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.vosto.customer.accounts.SignInActivity;
import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItem;
import com.vosto.customer.orders.CartItemAdapter;
import com.vosto.customer.services.GetStoresResult;
import com.vosto.customer.services.GetStoresService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.PlaceOrderResult;
import com.vosto.customer.services.PlaceOrderService;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class CartActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener {
	
	private ListView list;
	private boolean orderFinished;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		
		this.list = (ListView)findViewById(R.id.lstCartItems);
		list.setOnItemClickListener(this);
		this.orderFinished = false;
		
		
	
		
	
		
		list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, getCart().getItems()));
		Log.d("CRT", "Cart Adapter set.");
		
		updateTotals();
		
		
		
	}
	
	private void updateTotals(){
		Cart cart = getCart();
		TextView lblTotal = (TextView)findViewById(R.id.lblTotal);
		lblTotal.setText("Total: " + MoneyUtils.getRandString(cart.getTotalPrice()));
		
		TextView lblSubtotal = (TextView)findViewById(R.id.lblSubtotal);
		lblSubtotal.setText("Subtotal: " + MoneyUtils.getRandString(cart.getTotalPrice()));
	}
	
	public void removeButtonClicked(View v){
		CartItem item = (CartItem)((ImageButton)v).getTag();
		Cart cart = getCart();
		Log.d("REM", "Calling removeitem");
		cart.removeItem(item);
		this.list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, cart.getItems()));
		updateTotals();
	}
	
	public void promptForPin(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter Pin");
		alert.setMessage("Pin:");

		// Set an EditText view to get user input 
		final EditText pinInput = new EditText(this);
		pinInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		pinInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(5);
		pinInput.setFilters(FilterArray);
		
		alert.setView(pinInput);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = pinInput.getText().toString().trim();
		  	SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
			String storedPin = settings.getString("userPin", "").trim();
			if(storedPin.equals(value)){
				sendOrder();
			}else{
				// Invalid pin:
				showAlertDialog("Invalid Pin", "Please enter a valid pin.");
			}
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		
	}
	
	public void sendOrder(){
		this.pleaseWaitDialog = ProgressDialog.show(this, "Sending Order", "Please wait...", true);
		PlaceOrderService service = new PlaceOrderService(this, this);
		service.setCart(this.getCart());
		service.execute();
	}
	
	public void placeOrderClicked(View v){
		promptForPin();
	}
	
	public void onResume(){
		super.onResume();
	}

	
	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			return;
		}
		if(result instanceof PlaceOrderResult){
			PlaceOrderResult orderResult = (PlaceOrderResult)result;
			if(orderResult.wasOrderCreated()){
				getContext().closeCart();
				this.orderFinished = true;
				this.showAlertDialog("Thank you", "Your order has been placed.");
				saveCurrentOrder(orderResult.getOrder());
				Toast.makeText(this, "Order " + orderResult.getOrder().getNumber(), Toast.LENGTH_LONG).show();
			}else{
				this.showAlertDialog("Could not place order", orderResult.getErrorMessage());
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
	


	

	

	
	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
	}

	

	@Override
	public void onDismiss(DialogInterface dialog) {
		Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	} 
}