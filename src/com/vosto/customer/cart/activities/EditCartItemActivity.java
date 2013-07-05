package com.vosto.customer.cart.activities;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import android.content.SharedPreferences;
import com.agimind.widget.SlideHolder;
import org.joda.money.Money;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
	private ConcurrentHashMap<String, String> selectedOptionValues; // Maps option values to option types.
	private int quantity;
	private Money total;
    private SlideHolder mSlideHolder;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_cart_item);

		this.selectedOptionValues = new ConcurrentHashMap<String, String>();
		
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
		
		TextView lblSpecialInstructions = (TextView)findViewById(R.id.lblSpecialInstructions);
		lblSpecialInstructions.setText(Html.fromHtml("<u>Special Instructions</u>"));
		
		this.cartItemIndex = getIntent().getIntExtra("cartItemIndex", 0);
		this.cartItem = getCart().getItems().get(this.cartItemIndex);
		this.product = this.cartItem.getProduct();
		this.chosenVariant = this.cartItem.getVariant();
		this.quantity = this.cartItem.getQuantity();
		this.total = this.cartItem.getSubtotal();
		
		  
	    if(this.chosenVariant != null){
	    	this.selectedOptionValues = new ConcurrentHashMap<String, String>(this.chosenVariant.getOptionValueMap());
	    }
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
        this.chosenVariant = new VariantVo(variant);
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
	    	//Outer block in which all the option type rows will appear below each other:
	        LinearLayout variantsBlock = (LinearLayout)findViewById(R.id.variants_block);
	        variantsBlock.removeAllViews();

	        LayoutInflater inflater = getLayoutInflater();

	        ArrayList<String> optionTypes = this.product.getOptionTypes();
	        
	        
	        for(int i = 0; i<optionTypes.size(); i++){
	        	
	        	// Create a label for the option type name:
	        	TextView optionTypeLabel = (TextView)inflater.inflate(R.layout.option_type_label, (ViewGroup)variantsBlock, false);
	        	optionTypeLabel.setText(optionTypes.get(i));
	        	variantsBlock.addView(optionTypeLabel);
	        	
	        	//Horizontal layout for this option type. We will put all the buttons inside this layout.
	        	LinearLayout optionTypeBlock = (LinearLayout)inflater.inflate(R.layout.option_type_block, (ViewGroup)variantsBlock, false);
	        	
	        	// Add all the buttons for this option type:
	        	ArrayList<String> optionValues = this.product.getPossibleOptionValues(optionTypes.get(i));
	        	
	        	for(int j=0; j<optionValues.size(); j++){
	        		 LinearLayout variantButton = (LinearLayout)inflater.inflate(R.layout.variant_button, (ViewGroup)variantsBlock, false);
	        		 TextView lblVariantName = (TextView)variantButton.findViewById(R.id.lblVariantName);
	                 lblVariantName.setText(optionValues.get(j));

	        		 if(this.chosenVariant.hasOptionValue(optionTypes.get(i), optionValues.get(j))){
	                     // Highlight the selected value:
	                     variantButton.setBackgroundResource(R.drawable.variant_button_background_highlighted);
	                 }
	                 
	        		 
	        		 variantButton.setTag(optionTypes.get(i) + ":" + optionValues.get(j));
	        		 optionTypeBlock.addView(variantButton);
	        	}
	        	
	        	variantsBlock.addView(optionTypeBlock);

	        }
	}
	
	public void variantChanged(View v){
	    	//The button tag is in the format "portion:full"
	    	String selectedOption = (String)v.getTag();
	    	String[] selectedOptionSplit = selectedOption.split(":");
	    	
	    	//Set the newly selected option value in the HashMap:
	    	 this.selectedOptionValues.put(selectedOptionSplit[0].trim().toLowerCase(Locale.US), selectedOptionSplit[1].trim().toLowerCase(Locale.US));
	    	
	    	// Get the variant for this combination of options:
	    	VariantVo variant = this.product.getVariantForOptionCombinations(this.selectedOptionValues);
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
