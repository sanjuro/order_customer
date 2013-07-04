package com.vosto.customer.cart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.accounts.activities.SignInActivity;
import com.vosto.customer.accounts.services.AuthenticateResult;
import com.vosto.customer.accounts.services.AuthenticationService;
import com.vosto.customer.cart.SuburbListAdapter;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.orders.activities.OrderConfirmationActivity;
import com.vosto.customer.orders.services.GetAddressResult;
import com.vosto.customer.orders.services.GetAddressService;
import com.vosto.customer.orders.services.GetDeliveryPriceResult;
import com.vosto.customer.orders.services.GetDeliveryPriceService;
import com.vosto.customer.orders.services.PlaceOrderResult;
import com.vosto.customer.orders.services.PlaceOrderService;
import com.vosto.customer.orders.vos.AddressVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetSuburbsResult;
import com.vosto.customer.stores.services.GetSuburbsService;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.stores.vos.SuburbVo;
import com.vosto.customer.utils.GCMUtils;
import com.vosto.customer.utils.MoneyUtils;


public class DeliveryActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnDismissListener, LocationListener {
	
    private StoreVo store;
    private SlideHolder mSlideHolder;
    private boolean mustDeliver; // Indicates whether the user has chosen delivery.
    private AddressVo currentAddress; // The last address fetched from the text fields. Used to check if the address has changed before the order is placed.
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delivery);
		
		this.mustDeliver = false; // Collect in-store by default.
		
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
        
        // Default to collect in-store.
        LinearLayout collectButton = (LinearLayout)findViewById(R.id.collectButton); 
        this.collectButtonClicked(collectButton);
        
        // Fetch the suburbs:
        GetSuburbsService suburbsService = new GetSuburbsService(this, this, this.store.getId());
        suburbsService.execute();
        
        //Start listening for gps updates:
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 5, // min 5 seconds between location updates (can't be too frequent...battery life)
				10, // will only update for every 20 meters the device moves
				this);
        Log.d("GPS", "Listening for GPS updates...");
        
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
		// Check the address one last time just to be sure:
		AddressVo address = this.getAddressVo();
		if(this.mustDeliver && !address.equals(this.currentAddress)){
			this.showAlertDialog("Address changed", "Please press 'Get price' again before placing your order.");
			return;
		}
		if(this.mustDeliver && !this.validateAddress(address)){
			return;
		}
		
		
		Cart cart = getCart();
		if(cart.getNumberOfItems() == 0){
			this.showAlertDialog("Cart empty", "Please add some items to your cart first.");
			return;
		}
		
		if(this.mustDeliver){
			//Address is valid, attach it to the cart:
			cart.setDeliveryAddress(this.currentAddress);
		}else{
			cart.setDeliveryAddress(null);
		}
		
		PlaceOrderService service = new PlaceOrderService(this, this);
		service.setCart(cart);
		service.execute();
	}
	
	public void getPriceClicked(View v){
		AddressVo address = this.getAddressVo();
		if(!this.validateAddress(address)){
			return;
		}
		this.currentAddress = address;
		
		// Address seems valid, let's query the delivery price:
		GetDeliveryPriceService getPriceService = new GetDeliveryPriceService(this, this, this.store.getId(), address);
		getPriceService.execute();
	}
	
	/**
	 * Reads the address form and builds an AddressVo object with the data.
	 * @return An AddressVo object with the address fields populated
	 */
	private AddressVo getAddressVo(){
		TextView txtAddressLine1 = (TextView)this.findViewById(R.id.txtAddressLine1);
		TextView txtAddressLine2 = (TextView)this.findViewById(R.id.txtAddressLine2);
		Spinner cboAddressSuburb = (Spinner)this.findViewById(R.id.cboAddressSuburb);
		TextView txtAddressCity = (TextView)this.findViewById(R.id.txtAddressCity);
		TextView txtAddressPostalCode = (TextView)this.findViewById(R.id.txtAddressPostalCode);
		
		AddressVo address = new AddressVo();
		if(!txtAddressLine1.getText().toString().trim().equals("")){
			address.setAddress1(txtAddressLine1.getText().toString().trim());
		}
		
		if(!txtAddressLine2.getText().toString().trim().equals("")){
			address.setAddress2(txtAddressLine2.getText().toString().trim());
		}
		
		if(!txtAddressCity.getText().toString().trim().equals("")){
			address.setCity(txtAddressCity.getText().toString().trim());
		}
		
		if(!txtAddressPostalCode.getText().toString().trim().equals("")){
			address.setZipcode(txtAddressPostalCode.getText().toString().trim());
		}
		
		if(cboAddressSuburb.getSelectedItem() != null){
			address.setSuburb_id(((SuburbVo)cboAddressSuburb.getSelectedItem()).getId());
		}
		
		address.setCountry("South Africa");
		
		return address;
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
			this.processPlaceOrderResult((PlaceOrderResult)result);
		}else if(result instanceof AuthenticateResult){
			this.processAuthenticateResult((AuthenticateResult)result);
		}else if(result instanceof GetSuburbsResult){
			this.processGetSuburbsResult((GetSuburbsResult)result);
		}else if(result instanceof GetDeliveryPriceResult){
			this.processDeliveryPriceResult((GetDeliveryPriceResult)result);
		}else if(result instanceof GetAddressResult){
			this.processGetAddressResult((GetAddressResult)result);
		}
	}
	
	private void processDeliveryPriceResult(GetDeliveryPriceResult result){
		TextView priceLabel = (TextView)findViewById(R.id.priceLabel);
		priceLabel.setText("Price: " + MoneyUtils.getRandString(result.getDeliveryPrice()));
	}
	
	private void processAuthenticateResult(AuthenticateResult authResult){
		if(authResult.wasAuthenticationSuccessful()){
			sendOrder();
		}else{
			this.showAlertDialog("Invalid PIN", "Please check your PIN and try again.");
		}
	}
	
	private void processPlaceOrderResult(PlaceOrderResult orderResult){
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
	}
	
	private void processGetSuburbsResult(GetSuburbsResult suburbResult){
		 // Populate the suburbs drop down:
		 SuburbListAdapter suburbListAdapter = new SuburbListAdapter(this, android.R.layout.simple_spinner_item, suburbResult.getSuburbs());
		 suburbListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 Spinner cboAddressSuburb = (Spinner)findViewById(R.id.cboAddressSuburb);
		 cboAddressSuburb.setAdapter(suburbListAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 
	}
	
	/**
	 * Click handler for the button that chooses the in-store collection option.
	 * Hide the address form, show the collection details, and set some internal state.
	 * @param v
	 */
	public void collectButtonClicked(View v){
		LinearLayout addressForm = (LinearLayout)findViewById(R.id.addressForm);
		addressForm.setVisibility(View.GONE);
		
		LinearLayout collectionDetailsSection = (LinearLayout)findViewById(R.id.collectionDetailsSection);
		collectionDetailsSection.setVisibility(View.VISIBLE);
		
		TextView storeCollectionAddress = (TextView)findViewById(R.id.storeCollectionAddress);
		storeCollectionAddress.setText(this.store.getAddress());
		
		this.mustDeliver = false;
		
		LinearLayout collectButton = (LinearLayout)findViewById(R.id.collectButton); 
		LinearLayout deliveryButton = (LinearLayout)findViewById(R.id.deliveryButton); 
		
		collectButton.setBackgroundResource(R.drawable.collect_button_with_text_highlighted);
		deliveryButton.setBackgroundResource(R.drawable.delivery_button_with_text);
	}
	
	/**
	 * Click handler for the button that chooses the delivery option.
	 * Hide the collection details, show the address form, and set some internal state
	 * @param v
	 */
	public void deliveryButtonClicked(View v){
		LinearLayout collectionDetailsSection = (LinearLayout)findViewById(R.id.collectionDetailsSection);
		collectionDetailsSection.setVisibility(View.GONE);
		
		LinearLayout addressForm = (LinearLayout)findViewById(R.id.addressForm);
		addressForm.setVisibility(View.VISIBLE);
		
		this.mustDeliver = true;
		
		LinearLayout collectButton = (LinearLayout)findViewById(R.id.collectButton); 
		LinearLayout deliveryButton = (LinearLayout)findViewById(R.id.deliveryButton); 
		
		collectButton.setBackgroundResource(R.drawable.collect_button_with_text);
		deliveryButton.setBackgroundResource(R.drawable.delivery_button_with_text_highlighted);
	}
	
	
	public void getAddressClicked(View v){
		this.populateAddressFromLocation();
	}
	
	/**
	 * Gets the current location from the GPS and make a service call to fetch the address.
	 */
	private void populateAddressFromLocation(){
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			this.showAlertDialog("Enable GPS", "Please turn on your GPS.");
			return;
		}
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		
		if(location == null){
			this.showAlertDialog("Location problem", "Could not determine your current location. Please wait a moment and try again.");
			return;
		}
		
		GetAddressService service = new GetAddressService(this, this, location.getLatitude(), location.getLongitude());
		service.execute();
	}
	
	private void processGetAddressResult(GetAddressResult result){
		
	}
	
	public void acceptClicked(View v){
		if(!isUserSignedIn()){
			Intent intent = new Intent(this, SignInActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		if(!GCMUtils.checkGCMAndAlert(this, true)){
			return;
		}
		
	
		if(this.mustDeliver){
			AddressVo address = this.getAddressVo();
			if(!this.validateAddress(address)){
				return;
			}
		
			if(!address.equals(this.currentAddress)){
				// Address has changed since we last checked the price. Ask the user to check price again.
				this.showAlertDialog("Address changed", "Please press 'Get price' again before ordering.");
				return;
			}
			this.currentAddress = address;
		}
		
		Cart cart = getCart();
		if(cart.getNumberOfItems() > 0){
			promptForPin();
		}else{
			this.showAlertDialog("Cart Empty", "Please add some items to your cart.");
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
	}



	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	} 
}
