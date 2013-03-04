package com.vosto.customer;

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

import com.vosto.customer.products.TaxonListAdapter;
import com.vosto.customer.services.GetProductsResult;
import com.vosto.customer.services.GetProductsService;
import com.vosto.customer.services.GetTaxonsResult;
import com.vosto.customer.services.GetTaxonsService;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.StoreVo;
import com.vosto.customer.services.vos.TaxonVo;

public class TaxonsActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener {
	private StoreVo store;
	private ArrayList<TaxonVo> taxons;
	
	private ProgressDialog pleaseWaitDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_menu);
		this.store = (StoreVo) this.getIntent().getSerializableExtra("store");
		
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading Categories", "Please wait...", true);
		GetTaxonsService service = new GetTaxonsService(this, this.store.getId());
		service.execute();
	}

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
		
		TextView headerLabel = (TextView)findViewById(R.id.categories_header_label);
		headerLabel.setText(this.taxons.size() > 0 ? "Categories:" : "No categories for this store.");
		
		list.setAdapter(new TaxonListAdapter(this, R.layout.taxon_item_row, this.taxons));
		list.setOnItemClickListener(this);
	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		this.pleaseWaitDialog = ProgressDialog.show(this, "Loading Products", "Please wait...", true);
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
	
	
	
		
}
