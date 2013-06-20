package com.vosto.customer.cart.activities;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.vosto.customer.orders.activities.OrderConfirmationActivity;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.orders.services.PlaceOrderService;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.GCMUtils;
import com.vosto.customer.utils.MoneyUtils;
import com.vosto.customer.utils.NetworkUtils;


public class DeliveryActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener {
	
	private ListView list;
    private StoreVo store;
    private SlideHolder mSlideHolder;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delivery);

        Cart cart = getCart();
        this.store = cart.getStore();

        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
            //User logged in:
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));

        }else{
            //User not logged in:
        }

        LinearLayout store_details_block = (LinearLayout)findViewById(R.id.store_details_block);
        TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
        TextView txtStoreAddress = (TextView)findViewById(R.id.txtStoreAddress);
        TextView txtStoreTelephone = (TextView)findViewById(R.id.txtStoreTelephone);

        if ( this.store != null )  {
            txtStoreName.setText(this.store.getName());
            txtStoreAddress.setText(this.store.getAddress());
            txtStoreTelephone.setText(this.store.getManagerContact());
        } else {
            store_details_block.setVisibility(View.GONE);
            txtStoreAddress.setVisibility(View.GONE);
        }
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
		  service.setEmail(email);
		  service.setPin(enteredPin.trim());
		  service.execute();
	}
	
	public void sendOrder(){
		Cart cart = getCart();
		if(cart.getNumberOfItems() == 0){
			return;
		}
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
		if(result instanceof PlaceOrderResult){
			PlaceOrderResult orderResult = (PlaceOrderResult)result;
			if(orderResult.wasOrderCreated()){
				getContext().closeCart();
				this.showAlertDialog("Thank you", "Your order has been placed.");
				saveCurrentOrder(orderResult.getOrder());
                Log.d("ORD", "Order Number " + orderResult.getOrder().getNumber());
				Intent intent = new Intent(this, OrderConfirmationActivity.class);
                intent.putExtra("order", orderResult.getOrder());
                intent.putExtra("store", this.store);
                intent.putExtra("order_number", orderResult.getOrder().getNumber());
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

	@Override
	public void onDismiss(DialogInterface dialog) {
	} 
}
