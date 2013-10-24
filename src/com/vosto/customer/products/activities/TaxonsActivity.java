package com.vosto.customer.products.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;

import com.agimind.widget.SlideHolder;
import com.androidquery.AQuery;
import com.vosto.customer.HomeActivity;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.orders.activities.MyOrdersActivity;
import com.vosto.customer.stores.activities.StoreMapActivity;
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
import com.vosto.customer.utils.StoreFavouritesManager;

import static com.vosto.customer.utils.CommonUtilities.STORE_IMAGE_SERVER_URL;

import com.androidquery.AQuery;

public class TaxonsActivity extends VostoBaseActivity implements OnRestReturn, OnItemClickListener, OnCheckedChangeListener {

    private StoreVo store;
    private ArrayList<TaxonVo> taxons;
    private TaxonVo selectedTaxon;
    private StoreFavouritesManager favourites;
    private SlideHolder mSlideHolder;
    AQuery ag = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxons);
        ag = new AQuery(this.findViewById(android.R.id.content));

        this.store = (StoreVo) this.getIntent().getSerializableExtra("store");

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

        TextView txtStoreName = (TextView)findViewById(R.id.txtStoreName);
        TextView txtStoreAddress = (TextView)findViewById(R.id.txtStoreAddress);

        txtStoreName.setText(this.store.getName());
        txtStoreAddress.setText(this.store.getAddress());

        String imageUrl = STORE_IMAGE_SERVER_URL + store.getStoreImage();
        ag.id(R.id.lblStoreImage).image(imageUrl, false, false, 0, 0, null, AQuery.FADE_IN);

        Log.d("STO", "Loading taxons for store: " + this.store.getId());
        GetTaxonsService service = new GetTaxonsService(this, this, this.store.getId());
        service.execute();

        favourites = new StoreFavouritesManager(this);
        CheckBox star = (CheckBox)findViewById(R.id.star);
        star.setOnCheckedChangeListener(this);
        star.setChecked(favourites.isFavourite(store.getUniqueId()));
    }

    /**
     * Called from within the base RestService after a rest call completes.
     * @param result Can be any result type. This function should check the type and handle accordingly.
     */
    @Override
    public void onRestReturn(RestResult result) {

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

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        this.selectedTaxon = this.taxons.get(position);
        GetProductsService service = new GetProductsService(this, this, this.taxons.get(position).getId());
        service.execute();
    }

    public void mapPressed(View v){
        Intent intent = new Intent(this, StoreMapActivity.class);
        intent.putExtra("store", this.store);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.nothing);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.star) {
            String id = store.getUniqueId();
            if (isChecked) {
                favourites.add(id);
            } else {
                favourites.remove(id);
            }
        }

    }

}
