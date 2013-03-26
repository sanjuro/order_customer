package com.vosto.customer.products.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.products.TaxonListAdapter;
import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.services.GetProductsService;
import com.vosto.customer.products.services.GetTaxonsResult;
import com.vosto.customer.products.services.GetTaxonsService;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.products.vos.TaxonVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.vos.StoreVo;

public class TaxonsActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {
	private StoreVo store;
	private ArrayList<TaxonVo> taxons;
	private TaxonVo selectedTaxon;
	
	private ProgressDialog pleaseWaitDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taxons);
		this.store = (StoreVo) this.getIntent().getSerializableExtra("store");

        TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
        TextView txtStoreAddress = (TextView)findViewById(R.id.txtStoreAddress);
        TextView txtStoreTelephone = (TextView)findViewById(R.id.txtStoreTelephone);

        txtStoreName.setText(this.store.getName());
        txtStoreAddress.setText(this.store.getAddress());
        txtStoreTelephone.setText(this.store.getManagerContact());
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading Categories", "Please wait...", true);
		Log.d("STO", "Loading taxons for store: " + this.store.getId());
		GetTaxonsService service = new GetTaxonsService(this, this.store.getId());
		service.execute();
	}

	/**
	 * Called from within the base RestService after a rest call completes.
	 * @param result Can be any result type. This function should check the type and handle accordingly. 
	 */
	@Override
	public void onRestReturn(RestResult result) {
		this.pleaseWaitDialog.dismiss();
		if(result == null){
			Log.d("ERROR", "Result is null");
		}
		
		
		if(result instanceof GetTaxonsResult){
			processTaxonsResult((GetTaxonsResult)result);
		}else if(result instanceof GetProductsResult){
			processProductsResult((GetProductsResult)result);
		}
		
	}
	
	private void processProductsResult(GetProductsResult result){
		result.setStoreId(this.store.getId());
		ListView list = (ListView)findViewById(R.id.menuItems);
		if(list == null){
			Log.d("ERROR", "List is null");
		}
	
		Intent intent = new Intent(this, ProductResultsActivity.class);
		ProductVo[] products =  result.getProducts();
		intent.putExtra("store", this.store);
		intent.putExtra("categoryName", this.selectedTaxon.getName());
		intent.putExtra("products", products);
    	startActivity(intent);
	}
	
	private void processTaxonsResult(GetTaxonsResult result){
		result.setStoreId(this.store.getId());
		ListView list = (ListView)findViewById(R.id.menuItems);
		if(list == null){
			Log.d("ERROR", "List is null");
		}
		this.taxons = result.getTaxons();

		list.setAdapter(new TaxonListAdapter(this, R.layout.taxon_item_row, this.taxons));
		list.setOnItemClickListener(this);
	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading Products", "Please wait...", true);
		this.selectedTaxon = this.taxons.get(position);
		GetProductsService service = new GetProductsService(this, this.taxons.get(position).getId());
		service.execute();
	}
	

	@Override
	public void onBackPressed() {
		finish();
	}

	public void homeClicked(){
		Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	}
	
	
	public void ordersPressed(View v) {
		Intent intent = new Intent(this, MyOrdersActivity.class);
		startActivity(intent);
		finish();
	}

}
