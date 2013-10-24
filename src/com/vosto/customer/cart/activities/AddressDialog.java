package com.vosto.customer.cart.activities;


import com.vosto.customer.R;
import com.vosto.customer.cart.SuburbListAdapter;
import com.vosto.customer.orders.vos.AddressVo;
import com.vosto.customer.stores.vos.SuburbVo;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AddressDialog extends Dialog implements android.view.View.OnClickListener {
	
	private CartActivity cartActivity;
	private SuburbVo[] suburbs;
	
	public AddressDialog(CartActivity cartActivity, SuburbVo[] suburbs){
		super(cartActivity, R.style.DialogSlideAnim);
		this.cartActivity = cartActivity;
		this.suburbs = suburbs;
	}
	
	 @Override
	 protected void onCreate(final Bundle savedInstanceState)
	 {
	   super.onCreate(savedInstanceState);
	   LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(cartActivity).inflate(R.layout.dialog_address, null);
	   setContentView(dialogLayout);
	  
	   Button updateButton = (Button)dialogLayout.findViewById(R.id.updateButton);
	   updateButton.setOnClickListener(this);
	   
	   Button backButton = (Button)dialogLayout.findViewById(R.id.backButton);
	   backButton.setOnClickListener(this);
	   
	   // Populate the suburbs drop-down:
	   SuburbListAdapter suburbListAdapter = new SuburbListAdapter(this.cartActivity, android.R.layout.simple_spinner_item, this.suburbs);
	   suburbListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	   Spinner cboAddressSuburb = (Spinner)findViewById(R.id.cboAddressSuburb);
	   cboAddressSuburb.setAdapter(suburbListAdapter);
	   
	   //Add click handler for get from location:
	   // Button getFromLocationButton = (Button)dialogLayout.findViewById(R.id.btnGetAddressFromLocation);
	   // getFromLocationButton.setOnClickListener(this);
	   
	 }
	 
	 @Override
	 public void onClick(View v){
		 switch (v.getId()){
		 	case R.id.updateButton:
		 		this.cartActivity.updateAddress(this.getAddress());
		 		break;
		 	case R.id.backButton:
		 		this.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnim;
		 		dismiss();
		 		break;
//		 	case R.id.btnGetAddressFromLocation:
//		 		this.getAddressFromLocation();
//		 		break;
		 }
	 }
	 
	 /**
	  * Fetches the address based on the current GPS location, then auto-populates the form.
	  */
	 private void getAddressFromLocation(){
		 this.cartActivity.fetchAddressFromLocation();
	 }
	 
	 /**
	  * Populates all the address fields from a given address object.
	  * @param address  The address to show in the fields
	  */
	 public void setAddress(AddressVo address){
		 TextView txtAddressLine1 = (TextView)this.findViewById(R.id.txtAddressLine1);
	     TextView txtAddressLine2 = (TextView)this.findViewById(R.id.txtAddressLine2);
		 Spinner cboAddressSuburb = (Spinner)this.findViewById(R.id.cboAddressSuburb);
		 TextView txtAddressCity = (TextView)this.findViewById(R.id.txtAddressCity);
		 TextView txtAddressPostalCode = (TextView)this.findViewById(R.id.txtAddressPostalCode);
		 
		 txtAddressLine1.setText(address != null && address.getAddress1() != null ? address.getAddress1().trim() : "");
		 txtAddressLine2.setText(address != null && address.getAddress2() != null ? address.getAddress2().trim() : "");
		 txtAddressCity.setText(address != null && address.getCity() != null ? address.getCity().trim() : "");
		 txtAddressPostalCode.setText(address != null && address.getZipcode() != null ? address.getZipcode().trim() : "");
	 
		 Log.d("ADR", "suburb_id populating: " + address.getSuburb_id());
		 // Select the correct suburb in the drop-down:
		 if(address != null && address.getSuburb_id() != null){
			 for(int i = 0; i < this.suburbs.length; i++){
				 if(this.suburbs[i].getId() == address.getSuburb_id().intValue()){
					 Log.d("ADR", "FOUND suburb in drop down! Selecting...");
					 cboAddressSuburb.setSelection(i);
				 }
			 }
		 }
	 
	 }
	 
	 public AddressVo getAddress(){
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
				address.setSuburb(((SuburbVo)cboAddressSuburb.getSelectedItem()).getName());
			}
			
			address.setCountry("South Africa");
			
			return address;
	 }
	
}