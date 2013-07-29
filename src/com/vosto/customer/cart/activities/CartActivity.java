package com.vosto.customer.cart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.vosto.customer.orders.activities.OrderConfirmationActivity;
import com.vosto.customer.orders.services.GetAddressResult;
import com.vosto.customer.orders.services.GetAddressService;
import com.vosto.customer.orders.services.GetDeliveryPriceResult;
import com.vosto.customer.orders.services.GetDeliveryPriceService;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.orders.services.PlaceOrderService;
import com.vosto.customer.orders.vos.AddressVo;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetSuburbsResult;
import com.vosto.customer.stores.services.GetSuburbsService;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.stores.vos.SuburbVo;
import com.vosto.customer.utils.GCMUtils;
import com.vosto.customer.utils.MoneyUtils;
import com.vosto.customer.utils.NetworkUtils;


public class CartActivity extends VostoBaseActivity implements OnRestReturn {
	
	private ListView list;
    private StoreVo store;
    private SlideHolder mSlideHolder;
    private AddressDialog addressDialog;
    private SuburbVo[] suburbs;
    private AddressVo deliverToAddress; // Set to null for in-store collection.
    
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.listenForGpsUpdates = true;
		
		setContentView(R.layout.activity_cart);

        Cart cart = getCart();
        this.store = cart.getStore();

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

        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);

        View toggleView = findViewById(R.id.menuButton);
        toggleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSlideHolder.toggle();
            }
        });

        SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
        if(!settings.getString("userToken", "").equals("") &&  settings.getString("userName", "user") != "user"){
            //User logged in:
            TextView nameOfUser = (TextView)findViewById(R.id.nameOfUser);
            nameOfUser.setText(settings.getString("userName", "user"));
        }
        
        // Fetch the suburbs:
        GetSuburbsService suburbsService = new GetSuburbsService(this, this, this.store.getId());
        suburbsService.execute();

		refreshCart();
	}
	
	private void refreshCart(){
		this.list = (ListView)findViewById(R.id.lstCartItems);
		list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, getCart().getItems()));
		updatePriceLabels();
	}
	
	public void removeButtonClicked(View v){
		CartItem item = (CartItem)((ImageButton)v).getTag();
		Cart cart = getCart();
		cart.removeItem(item);
		this.list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, cart.getItems()));
		updatePriceLabels();
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
		  service.setEmail(email);
		  service.setPin(enteredPin.trim());
		  service.execute();
	}
	
	public void sendOrder(){
		Cart cart = getCart();
		if(cart.getNumberOfItems() == 0){
			this.showAlertDialog("Cart empty", "Please add some items to your cart first.");
			return;
		}
		
		
		cart.setDeliveryAddress(this.deliverToAddress);
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
			
		
			if(this.deliverToAddress != null && !this.validateAddress(this.deliverToAddress)){
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
		if(result == null){
			return;
		}
		if(result instanceof PlaceOrderResult){
			PlaceOrderResult orderResult = (PlaceOrderResult)result;
			if(orderResult.wasOrderCreated()){
				getContext().closeCart();
				this.showAlertDialog("Thank you", "Your order has been placed.");
				saveCurrentOrder(orderResult.getOrder());
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
		}else if(result instanceof GetSuburbsResult){
			this.suburbs = ((GetSuburbsResult) result).getSuburbs();
		}else if(result instanceof GetDeliveryPriceResult){
			processDeliveryPriceResult((GetDeliveryPriceResult)result);
		}else if(result instanceof GetAddressResult){
			processGetAddressResult((GetAddressResult)result);
		}
	}
	
	private void processGetAddressResult(GetAddressResult result){
		if(!result.wasSuccessful() || result.getAddress().isEmpty()){
			this.showAlertDialog("Sorry", "We could not determine your current address. Please type it in or try again later.");
			return;
		}
		this.addressDialog.setAddress(result.getAddress());
	}
	
	private void processDeliveryPriceResult(GetDeliveryPriceResult result){
		TextView deliveryCostLabel = (TextView)findViewById(R.id.deliveryCost);
		deliveryCostLabel.setText(MoneyUtils.getRandString(result.getDeliveryPrice()));
		
		Cart cart = getCart();
		cart.setDeliveryCost(result.getDeliveryPrice());
		saveCart(cart);
		
		updatePriceLabels();
	}
	
	private void updatePriceLabels(){
		Cart cart = getCart();
		TextView subtotalLabel = (TextView)findViewById(R.id.subtotal);
		subtotalLabel.setText("Subtotal: " + MoneyUtils.getRandString(cart.getSubtotalBeforeDelivery()));
		
		TextView deliveryCostLabel = (TextView)findViewById(R.id.deliveryCost);
		deliveryCostLabel.setText(cart.getDeliveryCost() != null ? MoneyUtils.getRandString(cart.getDeliveryCost()) : "R0.00");
	
		TextView grandTotalLabel = (TextView)findViewById(R.id.lblTotal);
		grandTotalLabel.setText("Total: " + MoneyUtils.getRandString(cart.getTotalPrice()));
	}

	public void updateAddress(AddressVo address) {
		if(!address.isEmpty()){
			if(!this.validateAddress(address)){
				return;
			}
			if(this.addressDialog != null){
				this.addressDialog.dismiss();
			}
			this.hideDeliveryButtons();
			((TextView)findViewById(R.id.deliveryAddress)).setText(address.toString());
			this.showDeliveryDetails();
			this.deliverToAddress = address;
			
			// Address seems valid, let's query the delivery price:
			GetDeliveryPriceService getPriceService = new GetDeliveryPriceService(this, this, this.store.getId(), address);
			getPriceService.execute();
			
			// Save this address on the device so we can auto-complete it next time:
			 SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
			 SharedPreferences.Editor editor = settings.edit();
		     editor.putString("latestDeliveryAddressJson", address.toJson());
		     editor.commit();
			
		    // Show the place order button:
			Button btnPlaceOrder = (Button)findViewById(R.id.place_order_button);
			btnPlaceOrder.setVisibility(View.VISIBLE);
		}else{
			this.showAlertDialog("Invalid Address", "Please make sure that you have entered all the fields.");
			this.deliverToAddress = null;
			this.hideDeliveryDetails();
			this.showDeliveryButtons();
			
			 // Hide the place order button:
			Button btnPlaceOrder = (Button)findViewById(R.id.place_order_button);
			btnPlaceOrder.setVisibility(View.GONE);
		}
		
		
	}
	
	public void deliverButtonClicked(View v){
		Log.d("DEL", "deliverButtonClicked");
		if(this.suburbs == null){
			Log.d("DEL", "Suburbs is null. Returning.");
			return;
		}
		this.addressDialog = new AddressDialog(this, this.suburbs);
		
		this.addressDialog.setContentView(R.layout.dialog_address);
		
		Window window = this.addressDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.BOTTOM;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		wlp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		
		this.addressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		this.addressDialog.show();
		
		// If we have an address saved on the device, auto-populate the dialog's form:
		  SharedPreferences settings = getSharedPreferences("VostoPreferences", 0);
		 if(settings.contains("latestDeliveryAddressJson") && !settings.getString("latestDeliveryAddressJson", "").equals("")){
			  this.addressDialog.setAddress(new AddressVo(settings.getString("latestDeliveryAddressJson", "")));
		  }
		
	}
	
	public void collectButtonClicked(View v){
		this.hideDeliveryButtons();
		((TextView)findViewById(R.id.lblDeliveryMethod)).setText("Collect");
		((TextView)findViewById(R.id.deliveryAddress)).setText("");
		this.showDeliveryDetails();
		this.deliverToAddress = null;
		Cart cart = getCart();
		cart.setDeliveryCost(null);
		saveCart(cart);
		this.updatePriceLabels();
		
		// Show the place order button:
		Button btnPlaceOrder = (Button)findViewById(R.id.place_order_button);
		btnPlaceOrder.setVisibility(View.VISIBLE);
	}
	
	public void changeButtonClicked(View v){
		// Hide the place order button:
		Button btnPlaceOrder = (Button)findViewById(R.id.place_order_button);
		btnPlaceOrder.setVisibility(View.GONE);
		
		hideDeliveryDetails();
		showDeliveryButtons();
	}
	
	private void showDeliveryButtons(){
		((LinearLayout)findViewById(R.id.deliveryButtons)).setVisibility(View.VISIBLE);
	}
	
	private void hideDeliveryButtons(){
		((LinearLayout)findViewById(R.id.deliveryButtons)).setVisibility(View.GONE);
	}
	
	private void showDeliveryDetails(){
		((LinearLayout)findViewById(R.id.deliveryDetails)).setVisibility(View.VISIBLE);
	}
	
	private void hideDeliveryDetails(){
		((LinearLayout)findViewById(R.id.deliveryDetails)).setVisibility(View.GONE);
	}
	
	/**
	 * Checks the presence and format of address fields in an AddressVo object, and displays error messages if invalid.
	 * The validation basically checks if an order could be placed with this address.
	 * @param address - The address object to check
	 * @return - boolean indicating whether the address is valid or not
	 */
	private boolean validateAddress(AddressVo address){
		if(address.getAddress1() == null || address.getAddress1().trim().equals("")){
			this.showAlertDialog("Invalid Address", "Please enter the address line 1");
			return false;
		}
		if(address.getSuburb_id() == null || address.getSuburb_id().intValue() < 1){
			this.showAlertDialog("Invalid Address", "Please select a suburb");
			return false;
		}
		if(address.getZipcode() == null || address.getZipcode().trim().equals("")){
			this.showAlertDialog("Invalid Address", "Please enter a postal code");
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Makes the service call to fetch an address by location so we can auto-populate the address form.
	 * @return The address for the current location.
	 */
	public void fetchAddressFromLocation(){
		Location bestLocation = this.getBestLocation(true);
		if(bestLocation == null){
			return;
		}
		
		GetAddressService service = new GetAddressService(this, this, bestLocation.getLatitude(), bestLocation.getLongitude());
		service.execute();
	}

    public void continueButtonPressed(View v){
        if(!NetworkUtils.isNetworkAvailable(this)){
            this.showAlertDialog("Connection Error", "Please connect to the internet.");
            return;
        }

        Intent intent = new Intent(this, TaxonsActivity.class);
        intent.putExtra("store", this.store);
        startActivity(intent);
    }
    
	
}
