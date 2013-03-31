package com.vosto.customer.products.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.cart.activities.CartActivity;
import com.vosto.customer.cart.vos.Cart;
import com.vosto.customer.cart.vos.CartItem;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.products.ProductListAdapter;
import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class ProductResultsActivity extends VostoBaseActivity implements OnRestReturn {
	
	private ProductVo[] products;
	private StoreVo store;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("RSM", "Products Activity creating");
		setContentView(R.layout.activity_product_results);
		ListView list = (ListView)findViewById(R.id.lstProducts);
		
		this.store = (StoreVo) this.getIntent().getSerializableExtra("store");
		String categoryName = this.getIntent().getStringExtra("categoryName");
		if(categoryName == null){
			categoryName = "";
		}
		TextView lblCategoryName = (TextView)findViewById(R.id.lblCategoryName);
		lblCategoryName.setText(categoryName);
	

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

	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
		if(result == null){
			return;
		}
		ListView list = (ListView)findViewById(R.id.lstStores);
		
		
		this.products = ((GetProductsResult)result).getProducts();
		list.setAdapter(new ProductListAdapter(this, R.layout.product_item_row, this.products));
	}
	
	public void productSelected(View v){
		ProductVo selectedProduct = (ProductVo)v.getTag();
		Intent intent = new Intent(this, ProductDetailsActivity.class);
		intent.putExtra("product", selectedProduct);
		
		TextView lblCategoryName = (TextView)findViewById(R.id.lblCategoryName);
		intent.putExtra("categoryName", lblCategoryName.getText().toString());
		intent.putExtra("store", this.store);
    	startActivity(intent);
	}
	
	public void addToCartClicked(View v){
		Button button = (Button)v;
		ProductVo product = (ProductVo)button.getTag();
		Cart cart = getCart();
		
		// Check if a cart is open with another store, and block this item:
		if(cart.isOpen() && cart.getStore() != null && cart.getStore().getId() != product.getStore_id()){
			Log.d("STO", "cart store: " + cart.getStore().getId() + ", product store: " + product.getStore_id());
			this.showAlertDialog("Error", "You can only order from one store at a time.");
			return;
		}
		
		cart.setStore(this.store);
		cart.addItem(new CartItem(product, 1));
		saveCart(cart);

        // send toast message
        CharSequence text = "Product was added to your cart.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

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
		if(getCart().getNumberOfItems() == 0){
			showAlertDialog("Cart Empty", "Please add some items to your cart.");
			return;
		}
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
	
	
	public void ordersPressed() {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
	}

	
}
