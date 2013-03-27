package com.vosto.customer.products.activities;

import org.joda.money.Money;

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
import com.vosto.customer.cart.activities.CartActivity;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.cart.vos.CartItem;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.products.vos.VariantVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class ProductDetailsActivity extends VostoBaseActivity implements OnRestReturn, OnSeekBarChangeListener {
	
	private StoreVo store;
	private ProductVo product;
	private VariantVo chosenVariant;
	private int quantity;
	private Money total;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_details);

		this.store = (StoreVo)getIntent().getSerializableExtra("store");
		
		TextView lblSpecialInstructions = (TextView)findViewById(R.id.lblSpecialInstructions);
		lblSpecialInstructions.setText(Html.fromHtml("<u>Special Instructions</u>"));
		
		String categoryName = this.getIntent().getStringExtra("categoryName");
		if(categoryName == null){
			categoryName = "";
		}
//		TextView lblCategoryName = (TextView)findViewById(R.id.lblCategory);
//		lblCategoryName.setText(categoryName);
		
		this.quantity = 1;
		
		SeekBar quantitySlider = (SeekBar)findViewById(R.id.quantity_slider);
		quantitySlider.setOnSeekBarChangeListener(this);
		
		this.product = (ProductVo)getIntent().getSerializableExtra("product");
		this.total = this.product.getPrice();
		
		this.chosenVariant = this.product.getVariants().length > 0 ? this.product.getVariants()[0] : null;
		
		TextView lblProductName = (TextView)findViewById(R.id.product_name);
		lblProductName.setText(this.product.getName());
		
		TextView lblProductDescription = (TextView)findViewById(R.id.lblProductDescription);
		lblProductDescription.setText(this.product.getDescription());
		
		TextView lblProductPrice = (TextView)findViewById(R.id.product_price);
		lblProductPrice.setText(MoneyUtils.getRandString(this.total));
		
		updateDisplay(1);
		drawVariants();
	}
	
	
	public void addToCartClicked(View v){
		Cart cart = getCart();
		
		// Check if a cart is open with another store, and block this item:
		if(cart.isOpen() && cart.getStore() != null && cart.getStore().getId() != this.product.getStore_id()){
				Log.d("STO", "cart store: " + cart.getStore().getId() + ", product store: " + product.getStore_id());
				this.showAlertDialog("Error", "You can only order from one store at a time.");
				return;
		}
		
		cart.setStore(this.store);

		CartItem item;
		if(this.chosenVariant != null){
			item = new CartItem(this.product, this.chosenVariant, this.quantity);
		}else{
			item = new CartItem(this.product, this.quantity);
		}

		EditText txtSpecialInstructions = (EditText)findViewById(R.id.txtSpecialInstructions);
		if(!txtSpecialInstructions.getText().toString().trim().equals("")){
			item.setSpecialInstructions(txtSpecialInstructions.getText().toString().trim());
		}

		cart.addItem(item);
		saveCart(cart);
		
		Intent intent = new Intent(this, CartActivity.class);
		startActivity(intent);
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
		variantsBlock.removeAllViews();
		
		 LayoutInflater inflater = getLayoutInflater();
		 
		for(int i = 0; i<variants.length; i++){
			Log.d("DRAW", "Drawing variant: " + variants[i].getOptionValues()[0].getName());
	        LinearLayout variantButton = (LinearLayout)inflater.inflate(R.layout.variant_button, (ViewGroup)variantsBlock, false);
			if(this.chosenVariant != null && this.chosenVariant.getId() == variants[i].getId()){
				// Highlight the selected variant:
				variantButton.setBackgroundResource(R.drawable.variant_button_background_highlighted);
			}
	        
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
		this.drawVariants();
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
	
	}
	
	
	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
	}
	
	public void buyButtonClicked(View v){
		Intent intent = new Intent(this, CartActivity.class);
		startActivity(intent);
		finish();
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
