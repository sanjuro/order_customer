package com.vosto.customer.cart.activities;

import org.joda.money.Money;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.R.id;
import com.vosto.customer.R.layout;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.cart.vos.CartItem;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.products.vos.VariantVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class EditCartItemActivity extends VostoBaseActivity implements OnRestReturn, OnSeekBarChangeListener {
	
	private ProductVo product;
	private int cartItemIndex;
	private CartItem cartItem;
	private VariantVo chosenVariant;
	private int quantity;
	private Money total;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_cart_item);
		
		TextView lblSpecialInstructions = (TextView)findViewById(R.id.lblSpecialInstructions);
		lblSpecialInstructions.setText(Html.fromHtml("<u>Special Instructions</u>"));
		
		this.cartItemIndex = getIntent().getIntExtra("cartItemIndex", 0);
		this.cartItem = getCart().getItems().get(this.cartItemIndex);
		this.product = this.cartItem.getProduct();
		this.chosenVariant = this.cartItem.getVariant();
		this.quantity = this.cartItem.getQuantity();
		this.total = this.cartItem.getSubtotal();
		
		SeekBar quantitySlider = (SeekBar)findViewById(R.id.quantity_slider);
		quantitySlider.setProgress(this.quantity);
		quantitySlider.setOnSeekBarChangeListener(this);
		
		TextView lblProductName = (TextView)findViewById(R.id.product_name);
		lblProductName.setText(this.product.getName());
		
		TextView lblProductDescription = (TextView)findViewById(R.id.lblProductDescription);
		lblProductDescription.setText(this.product.getDescription());
		
		TextView lblProductPrice = (TextView)findViewById(R.id.product_price);
		lblProductPrice.setText(MoneyUtils.getRandString(this.total));
		
		EditText txtSpecialInstructions = (EditText)findViewById(R.id.txtSpecialInstructions);
		txtSpecialInstructions.setText(this.cartItem.getSpecialInstructions());
		
		updateDisplay(this.quantity);
		drawVariants();
	}
	
	
	public void saveClicked(View v){
		Cart cart = getCart();
		
		this.cartItem.setQuantity(this.quantity);
		this.cartItem.setVariant(this.chosenVariant);
		this.cartItem.setProduct(this.product);
		
		
		EditText txtSpecialInstructions = (EditText)findViewById(R.id.txtSpecialInstructions);
		if(!txtSpecialInstructions.getText().toString().trim().equals("")){
			this.cartItem.setSpecialInstructions(txtSpecialInstructions.getText().toString().trim());
		}else{
			this.cartItem.setSpecialInstructions("");
		}
		
		cart.editItem(this.cartItemIndex, this.cartItem);
		saveCart(cart);
		
		finish();
	}
	
	public void updateDisplay(){
		this.updateDisplay(this.quantity, this.chosenVariant);
	}
	
	public void updateDisplay(int quantity){
		this.updateDisplay(quantity, this.chosenVariant);
	}
	
	public void updateDisplay(VariantVo variant){
		this.updateDisplay(this.quantity, variant);
	}
	
	public void updateDisplay(int quantity, VariantVo variant){
		this.quantity = quantity;
		this.chosenVariant = variant;
		TextView lblQuantity = (TextView)findViewById(R.id.lblQuantity);
		lblQuantity.setText(Integer.toString(quantity));
		
		Money unitPrice = this.chosenVariant != null ? this.chosenVariant.getPrice() : this.product.getPrice();
		
		TextView lblProductPrice = (TextView)findViewById(R.id.product_price);
		lblProductPrice.setText(MoneyUtils.getRandString(unitPrice));
		
		this.total = unitPrice.multipliedBy(quantity);
		
		TextView lblTotal = (TextView)findViewById(R.id.lblTotal);
		lblTotal.setText(MoneyUtils.getRandString(this.total));
		
		Cart cart = getCart();
		TextView lblBuyButtonPrice = (TextView)findViewById(R.id.buy_button_price);
		TextView lblBuyButtonQuantity = (TextView)findViewById(R.id.buy_button_quantity);
		
		lblBuyButtonPrice.setText(MoneyUtils.getRandString(cart.getTotalPrice()));
		lblBuyButtonQuantity.setText(cart.getNumberOfItems() + " items");
		
	}
	
	public void drawVariants(){
		VariantVo[] variants = this.product.getVariants();
		if(variants.length < 2){
			return;
		}
		
		LinearLayout variantsBlock = (LinearLayout)findViewById(R.id.variants_block);
		
		 LayoutInflater inflater = getLayoutInflater();
		 
		for(int i = 0; i<variants.length; i++){
			Log.d("DRAW", "Drawing variant: " + variants[i].getOptionValues()[0].getName());
	        LinearLayout variantButton = (LinearLayout)inflater.inflate(R.layout.variant_button, (ViewGroup)variantsBlock, false);
			TextView lblVariantName = (TextView)variantButton.findViewById(R.id.lblVariantName);
			lblVariantName.setText(variants[i].getOptionValues()[0].getName());
			variantButton.setTag(variants[i]);
			variantsBlock.addView(variantButton);
		}
	}
	
	public void variantChanged(View v){
		Log.d("VAR", "Variant change clicked.");
		VariantVo variant = (VariantVo)v.getTag();
		this.updateDisplay(variant);
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
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			return;
		}
	
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
        alert.show();
	}
	
	public void buyButtonClicked(View v){
		Intent intent = new Intent(this, CartActivity.class);
		startActivity(intent);
		finish();
	}
	
	
	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		progress += 1;
		updateDisplay(progress);
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
