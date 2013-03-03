package com.vosto.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.vosto.customer.orders.Cart;
import com.vosto.customer.orders.CartItem;
import com.vosto.customer.orders.CartItemAdapter;
import com.vosto.customer.services.GetStoresResult;
import com.vosto.customer.services.GetStoresService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.PlaceOrderResult;
import com.vosto.customer.services.PlaceOrderService;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.utils.MoneyUtils;
/**
 * @author flippiescholtz
 *
 */
public class CartActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {
	
	private ListView list;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		
		this.list = (ListView)findViewById(R.id.lstCartItems);
		list.setOnItemClickListener(this);
		
		
	
		
	
		
		list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, getCart().getItems()));
		Log.d("CRT", "Cart Adapter set.");
		
		updateTotals();
		
		
		
	}
	
	private void updateTotals(){
		Cart cart = getCart();
		TextView lblTotal = (TextView)findViewById(R.id.lblTotal);
		lblTotal.setText("Total: " + MoneyUtils.getRandString(cart.getTotalPrice()));
		
		TextView lblSubtotal = (TextView)findViewById(R.id.lblSubtotal);
		lblSubtotal.setText("Subtotal: " + MoneyUtils.getRandString(cart.getTotalPrice()));
	}
	
	public void removeButtonClicked(View v){
		CartItem item = (CartItem)((ImageButton)v).getTag();
		Cart cart = getCart();
		Log.d("REM", "Calling removeitem");
		cart.removeItem(item);
		this.list.setAdapter(new CartItemAdapter(this, R.layout.cart_item_row, cart.getItems()));
		updateTotals();
	}
	
	public void placeOrderClicked(View v){
		this.pleaseWaitDialog = ProgressDialog.show(this, "Sending Order", "Please wait...", true);
		PlaceOrderService service = new PlaceOrderService(this, this);
		service.setCart(this.getCart());
		service.execute();
	}
	
	public void onResume(){
		super.onResume();
	}

	
	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			return;
		}
		if(result instanceof PlaceOrderResult){
			PlaceOrderResult orderResult = (PlaceOrderResult)result;
			Toast.makeText(this, "Order " + orderResult.getOrderNumber(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 
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
