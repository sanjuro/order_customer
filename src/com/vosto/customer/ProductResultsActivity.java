package com.vosto.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItem;
import com.vosto.customer.products.ProductListAdapter;
import com.vosto.customer.services.GetProductsResult;
import com.vosto.customer.services.GetStoresResult;
import com.vosto.customer.services.GetStoresService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class ProductResultsActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {
	
	private ProductVo[] products;
	private StoreVo store;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("RSM", "Products Activity creating");
		setContentView(R.layout.activity_product_results);
		ListView list = (ListView)findViewById(R.id.lstProducts);
		list.setOnItemClickListener(this);
		
		this.store = (StoreVo) this.getIntent().getSerializableExtra("store");
	

		Object[] objects = (Object[]) this.getIntent().getSerializableExtra("products");
		this.products = new ProductVo[objects.length];
		for(int i = 0; i<objects.length; i++){
			this.products[i] = (ProductVo)objects[i];
		}
		
		list.setAdapter(new ProductListAdapter(this, R.layout.product_item_row, this.products));
		updateBuyButton();
	}
	
	public void onResume(){
		super.onResume();
		Log.d("RSM", "Products Activity resuming");
		updateBuyButton();
		//GetStoresService service = new GetStoresService(this);
		//service.execute();
	}

	
	@Override
	public void onRestReturn(RestResult result) {
		if(result == null){
			return;
		}
		ListView list = (ListView)findViewById(R.id.lstStores);
		
		
		this.products = ((GetProductsResult)result).getProducts();
		list.setAdapter(new ProductListAdapter(this, R.layout.product_item_row, this.products));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 
	}
	
	public void addToCartClicked(View v){
		ImageButton button = (ImageButton)v;
		ProductVo product = (ProductVo)button.getTag();
		Cart cart = getCart();
		
		// Check if a cart is open with another store, and block this item:
		if(cart.isOpen() && cart.getStore().getId() != product.getStore_id()){
			this.showAlertDialog("Error", "You can only order from one store at a time.");
			return;
		}
		
		cart.setStore(this.store);
		cart.addItem(new CartItem(product, 1));
		saveCart(cart);
		updateBuyButton();
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	  } 
	
	private void updateBuyButton(){
		TextView txtAmount = (TextView)findViewById(R.id.buy_button_price);
		TextView txtQuantity = (TextView)findViewById(R.id.buy_button_quantity);
		Cart cart = getCart();
		if(cart == null){
			Log.d("Cart", "Base activity returned a null cart!");
		}
		txtAmount.setText(MoneyUtils.getRandString(cart.getTotalPrice()));
		txtQuantity.setText(cart.getNumberOfItems() + " items");
	}
	
	public void buyButtonClicked(View v){
		Intent intent = new Intent(this, CartActivity.class);
    	startActivity(intent);
	}
	
	
	public void homeClicked(){
		Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.itemStores:
	      break;
	    
	    default:
	      break;
	    }

	    return true;
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
	
	@Override
	public void storesPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cartPressed() {
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
}
