package com.vosto.customer;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItem;
import com.vosto.customer.products.ProductListAdapter;
import com.vosto.customer.products.TaxonListAdapter;
import com.vosto.customer.services.GetProductsResult;
import com.vosto.customer.services.GetProductsService;
import com.vosto.customer.services.GetTaxonsResult;
import com.vosto.customer.services.GetTaxonsService;
import com.vosto.customer.services.GetVariantsResult;
import com.vosto.customer.services.GetVariantsService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.OptionValueVo;
import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.TaxonVo;
import com.vosto.customer.services.vos.VariantVo;
import com.vosto.customer.utils.InputFilterMinMax;

public class StoreMenuActivity extends VostoActivity implements OnRestReturn, OnItemClickListener, TextWatcher {

	private int storeId;
	private String storeName;
	private String storeTel;
	private String storeAddress;
	private ArrayList<ProductVo> products;
	private ProductVo currentProduct;
	private ArrayList<TaxonVo> taxons;
	private TaxonVo currentTaxon;
	private ArrayList<VariantVo> variants;
	private VariantVo currentVariant;
	private int currentQuantity;
	private boolean showingTaxons;
	private Dialog dialog;
	
	private String callingActivity;
	private ProgressDialog pleaseWaitDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.showingTaxons = true;
		setContentView(R.layout.activity_store_menu);
		this.storeId = getIntent().getIntExtra("storeId", -1); 
		this.storeName = getIntent().getStringExtra("storeName");
		this.storeTel = getIntent().getStringExtra("storeTel");
		this.storeAddress = getIntent().getStringExtra("storeAddress");
		this.callingActivity = getIntent().getStringExtra("callingActivity");
		
		
		
		currentQuantity = 1;
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading Categories", "Please wait...", true);
		GetTaxonsService service = new GetTaxonsService(this, this.storeId);
		service.execute();
	}
	
	private void loadTaxons(){
		GetTaxonsService service = new GetTaxonsService(this, this.storeId);
		service.execute();
	}
	
	private void loadProducts(int taxonId){
		Log.d("TAX", "Getting products for taxon: " + taxonId);
		GetProductsService service = new GetProductsService(this, taxonId);
		service.execute();
	}

	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			Log.d("ERROR", "Result is null");
		}
		
		if(result instanceof GetProductsResult){
			processProductsResult((GetProductsResult)result);
		}else if(result instanceof GetTaxonsResult){
			processTaxonsResult((GetTaxonsResult)result);
		}else if(result instanceof GetVariantsResult){
			processVariantsResult((GetVariantsResult)result);
		}
		
	}
	
	private void processProductsResult(GetProductsResult result){
		result.setStoreId(this.storeId);
		ListView list = (ListView)findViewById(R.id.menuItems);
		if(list == null){
			Log.d("ERROR", "List is null");
		}
		this.products = result.getProducts();
		Log.d("PROD", "Products: " + this.products.size());
		list.setAdapter(new ProductListAdapter(this, R.layout.product_item_row, this.products));
		list.setOnItemClickListener(this);
		
		this.showingTaxons = false;
	}
	
	private void processTaxonsResult(GetTaxonsResult result){
		result.setStoreId(this.storeId);
		ListView list = (ListView)findViewById(R.id.menuItems);
		if(list == null){
			Log.d("ERROR", "List is null");
		}
		this.taxons = result.getTaxons();
		Log.d("PROD", "Taxons: " + this.taxons.size());
		
		TextView headerLabel = (TextView)findViewById(R.id.categories_header_label);
		headerLabel.setText(this.taxons.size() > 0 ? "Categories:" : "No categories for this store.");
		
		list.setAdapter(new TaxonListAdapter(this, R.layout.taxon_item_row, this.taxons));
		list.setOnItemClickListener(this);
		this.showingTaxons = true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(this.showingTaxons){
			this.currentTaxon = this.taxons.get(position);
			this.loadProducts(this.currentTaxon.getId());
		}else{
			this.currentProduct = this.products.get(position);
			this.choseProduct(this.currentProduct.getId());
		}
	}
	
	private void choseProduct(int productId){
		GetVariantsService service = new GetVariantsService(this, productId);
		service.execute();
	}
	
	
	private void processVariantsResult(GetVariantsResult result){
		// custom dialog
					this.variants = result.getVariants();
					if(dialog == null){
						dialog = new Dialog(this);
					}
					dialog.setContentView(R.layout.dialog_add_to_cart);
					dialog.setTitle("Add to Cart...");
		 
					// set the custom dialog components - text, image and button
				
				
					TextView addToCartPrice = (TextView)dialog.findViewById(R.id.addToCartPrice);
					RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.variantsRadioGroup);
					
			
					for(int i = 0; i<this.variants.size(); i++){
						VariantVo variant = this.variants.get(i);
						if(variant.getOptionValues().size() == 0){
							continue;
						}
						OptionValueVo optionValue = variant.getOptionValues().get(0);
						
						RadioButton radioButton = new RadioButton(this);
						radioButton.setText(optionValue.getPresentation() + " (R " + variant.getPrice() + " )");
						radioButton.setTextColor(Color.BLUE);
						
						OnClickListener radioListener = new OnClickListener(){

							@Override
							public void onClick(View v) {
								RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.variantsRadioGroup);
								int idx = radioGroup.indexOfChild(v);
								currentVariant = variants.get(idx);
								updateDialogPrice();
							}
							
							
						};
						
						radioButton.setOnClickListener(radioListener);
						
					//	addToCartPrice.setText("Price: R " + variant.getPrice());
						addToCartPrice.setTextColor(Color.BLUE);
						
					
						
						radioGroup.addView(radioButton);
					}
					
					if(radioGroup.getChildCount() > 0){
						radioGroup.check(radioGroup.getChildAt(0).getId());
						if(this.variants.size() > 0){
							this.currentVariant = this.variants.get(0);
						}
					}
					
					EditText txtQuantity = (EditText)dialog.findViewById(R.id.txtQuantity);
					txtQuantity.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
					txtQuantity.setText("1");
					
					
					txtQuantity.addTextChangedListener(this);
					
					Button okButton = (Button)dialog.findViewById(R.id.addToCartButtonOK);
					okButton.setOnClickListener(new OnClickListener(){
					
						@Override
						public void onClick(View v) {
							
							VostoCustomerApp context = (VostoCustomerApp)getApplicationContext();
							Cart cart;
							if(!context.hasOpenCart()){
								cart = new Cart();
							}else{
								cart = context.getLatestCart();
							}
							
							RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.variantsRadioGroup);
							int radioButtonID = radioGroup.getCheckedRadioButtonId();
					    	View radioButton = radioGroup.findViewById(radioButtonID);
					    	int idx = radioGroup.indexOfChild(radioButton);
					    	EditText txtQuantity = (EditText)dialog.findViewById(R.id.txtQuantity);
					    	int quantity = 1;
					    	try{
					    		quantity = Integer.parseInt(txtQuantity.getText().toString());
					    	}catch(NumberFormatException e){
					    		quantity = 1;
					    	}
					    	EditText txtComments = (EditText)dialog.findViewById(R.id.txtComments);
					    	CartItem item = null;
					    	if(idx >= 0){
					    		currentVariant = variants.get(idx);
					    		item = new CartItem(currentProduct, currentVariant, quantity);
					    	}else{
					    		item = new CartItem(currentProduct, quantity);
					    	}
					    	if(!txtComments.getText().toString().trim().equals("")){
					    		item.setComments(txtComments.getText().toString().trim());
					    	}
							cart.addItem(item);
							context.setLatestCart(cart);
							dialog.dismiss();
							
							mainMenu.findItem(R.id.itemCart).setTitle(getResources().getString(R.string.cart) + " (" + cart.getNumberOfItems() + ")");
						
							//Toast.makeText(getApplicationContext(), "Cart Total: " + cart.getTotalPrice(), Toast.LENGTH_LONG).show();
						}	
					});
					
					
				
					Button cancelButton = (Button)dialog.findViewById(R.id.addToCartButtonCancel);
					cancelButton.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}	
					});
					dialog.show();
	}
	
	private void updateDialogPrice(){
		TextView addToCartPrice = (TextView)dialog.findViewById(R.id.addToCartPrice);
		double price = currentVariant.getPrice() * currentQuantity;
		addToCartPrice.setText("Price: R" + price);
	}
	
	public void callStore(View view){
		String url = "tel:" + this.storeTel;
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		if(!this.showingTaxons){
			this.loadTaxons();
		}else{
			finish();
		}
	}

	public void homeClicked(){
		Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	@Override
	public void storesPressed() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void ordersPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void settingsPressed() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		try{
			this.currentQuantity = Integer.parseInt(s.toString());
		}catch(NumberFormatException e){
			this.currentQuantity = 1;
		}
		updateDialogPrice();
		
	}
	
	
	
	
		
}
