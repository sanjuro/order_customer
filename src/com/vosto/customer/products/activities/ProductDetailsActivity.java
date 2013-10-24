package com.vosto.customer.products.activities;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;
import org.joda.money.Money;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.agimind.widget.SlideHolder;
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
import com.vosto.customer.utils.ProductFavouritesManager;

import static com.vosto.customer.utils.CommonUtilities.SERVER_URL;

public class ProductDetailsActivity extends VostoBaseActivity implements OnRestReturn, OnSeekBarChangeListener, OnCheckedChangeListener {

    private StoreVo store;
    private ProductVo product;
    private VariantVo chosenVariant;
    private ConcurrentHashMap<String, String> selectedOptionValues; // Maps option values to option types.
    private int quantity;
    private Money total;
    private ProductFavouritesManager favourites;
    private SlideHolder mSlideHolder;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        
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

        }else{
            //User not logged in:
        }

        this.store = (StoreVo)getIntent().getSerializableExtra("store");

        TextView lblSpecialInstructions = (TextView)findViewById(R.id.lblSpecialInstructions);
        lblSpecialInstructions.setText(Html.fromHtml("<u>Special Instructions</u>"));

        this.quantity = 1;

        this.product = (ProductVo)getIntent().getSerializableExtra("product");

        this.total = this.product.getPrice();

        this.chosenVariant = this.product.getVariants().length > 0 ? new VariantVo(this.product.getVariants()[0]) : null;
        if(this.chosenVariant != null){
        	this.selectedOptionValues = new ConcurrentHashMap<String, String>(this.chosenVariant.getOptionValueMap());
        }

        TextView lblProductName = (TextView)findViewById(R.id.product_name);
        lblProductName.setText(this.product.getName());

        TextView lblProductDescription = (TextView)findViewById(R.id.lblProductDescription);
        lblProductDescription.setText(this.product.getDescription());

        TextView lblProductPrice = (TextView)findViewById(R.id.product_price);

        // lblProductPrice.setText(MoneyUtils.getRandString(this.total));
        favourites = new ProductFavouritesManager(this);


        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //here, after we introduced something in the EditText we get the string from it

                EditText lblQuantity = (EditText)findViewById(R.id.lblQuantity);
                if(!lblQuantity.getText().toString().equals("") && lblQuantity.getText().toString().matches("^\\d+$")){
                    updateDisplay(Integer.valueOf(lblQuantity.getText().toString().trim()));
                }

            }
        };

        EditText lblQuantity = (EditText)findViewById(R.id.lblQuantity);
        lblQuantity.addTextChangedListener(textWatcher);

        updateDisplay(this.quantity);
        drawVariants();
    }


    public void addToCartClicked(View v){
        Cart cart = getCart();

        // Check if a cart is open with another store, and block this item:
        if(cart.isOpen() && cart.getStore() != null && cart.getStore().getId() != this.product.getStore_id()){
            this.showAlertDialog("Error", "You can only order from one store at a time.");
            return;
        }

        cart.setStore(this.store);

        EditText lblQuantity = (EditText)findViewById(R.id.lblQuantity);
        this.quantity = Integer.valueOf(lblQuantity.getText().toString().trim());

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
        this.chosenVariant = new VariantVo(variant);

        Money unitPrice = this.chosenVariant != null ? this.chosenVariant.getPrice() : this.product.getPrice();

        this.total = unitPrice.multipliedBy(quantity);

        TextView lblTotal = (TextView)findViewById(R.id.lblTotal);
        lblTotal.setText("Total: " + MoneyUtils.getRandString(this.total));

        Cart cart = getCart();
        TextView lblBuyButtonPrice = (TextView)findViewById(R.id.buy_button_price);
        TextView lblBuyButtonQuantity = (TextView)findViewById(R.id.buy_button_quantity);

        lblBuyButtonPrice.setText(MoneyUtils.getRandString(cart.getTotalPrice()));
        lblBuyButtonQuantity.setText(cart.getNumberOfItems() + " items");
    }

    /**
     * Adds the option value buttons to the view and highlights the choices according to the current variant.
     */
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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.star) {
            String id = Integer.toString(product.getId());
            if (isChecked) {
                favourites.add(id);
            } else {
                favourites.remove(id);
            }
        }

    }


}
