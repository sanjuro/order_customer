package com.vosto.customer.cart.activities;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.accounts.services.AuthenticateResult;
import com.vosto.customer.accounts.services.AuthenticationService;
import com.vosto.customer.cart.CartItemAdapter;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.cart.vos.CartItem;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.orders.services.PlaceOrderService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.GCMUtils;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class CartActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener {
	
	private ListView list;
    private SlideHolder mSlideHolder;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
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
		refreshCart();
	}
	
	private void refreshCart(){
		this.list = (ListView)findViewById(R.id.lstCartItems);
		list.setOnItemClickListener(this);
		list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, getCart().getItems()));
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
		if(cart.getNumberOfItems() == 0){
			getContext().closeCart();
			finish();
		}
	}
	
	public void editButtonClicked(View v){
		Intent intent = new Intent(this, EditCartItemActivity.class);
		intent.putExtra("cartItemIndex", getCart().getIndexForItem((CartItem)v.getTag()));
		startActivity(intent);
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
	 * Authenticate with vosto using the entered pin and the stored e-mail address.
	 */
	public void authenticateWithVosto(String enteredPin){
		SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		  AuthenticationService service = new AuthenticationService(this, this);
		  String email = settings.getString("userEmail", "").trim();
		  if(email.equals("")){
			  showAlertDialog("Error", "Could not determine your e-mail address. Please log out and log in again.");
			  return;
		  }
		  this.pleaseWaitDialog = ProgressDialog.show(this, "Authenticating", "Please wait...", true);
		  service.setEmail(email);
		  service.setPin(enteredPin.trim());
		  service.execute();
	}
	
	public void sendOrder(){
		Cart cart = getCart();
		if(cart.getNumberOfItems() == 0){
			return;
		}
		this.pleaseWaitDialog = ProgressDialog.show(this, "Sending Order", "Please wait...", true);
		PlaceOrderService service = new PlaceOrderService(this, this);
		service.setCart(cart);
		service.execute();
	}
	
	public void placeOrderClicked(View v){
		if(!isUserSignedIn()){
			Intent intent = new Intent(this, SignInActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		if(!GCMUtils.checkGCMAndAlert(this, true)){
			return;
		}
		
		Cart cart = getCart();
		if(cart.getNumberOfItems() > 0){
			promptForPin();
		}else{
			this.showAlertDialog("Cart Empty", "Please add some items to your cart.");
		}
	}
	
	public void onResume(){
		super.onResume();
		refreshCart();
	}

	
	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
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
	

	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
	}

	

	@Override
	public void onDismiss(DialogInterface dialog) {
	} 
}
