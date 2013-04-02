package com.vosto.customer.favourites.activities;

import java.util.Arrays;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.favourites.services.GetStoreFavouriteService;
import com.vosto.customer.favourites.services.GetStoreFavouriteResult;
import com.vosto.customer.products.activities.ProductDetailsActivity;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.utils.Constants;

import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;

import android.os.Bundle;
import android.util.Log;

public class StoreFavouriteActivity extends VostoBaseActivity implements OnRestReturn {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_favourites);

        fetchStores();
    }

    private void fetchStores() {
        new GetStoreFavouriteService(this, this).execute();
    }


    @Override
    public void onRestReturn(RestResult result) {
        if(result == null){
            return;
        }

        if (result instanceof GetStoreFavouriteResult) {
            StoreVo[] stores = ((GetStoreFavouriteResult) result).getStores();
            Log.d(Constants.TAG, Arrays.asList(stores).toString());

            //update stores and update listview
            ListView list = (ListView)findViewById(R.id.lstStores);
            list.setAdapter(new StoreListAdapter(this, R.layout.store_item_row, stores));
        }

    }

    public void productSelected(View v){
        ProductVo selectedProduct = (ProductVo)v.getTag();
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product", selectedProduct);

        TextView lblCategoryName = (TextView)findViewById(R.id.lblCategoryName);
        intent.putExtra("categoryName", lblCategoryName.getText().toString());
        // intent.putExtra("store", this.store);
        startActivity(intent);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.storeFavouritesbutton){
            Intent intent = new Intent(this, StoreFavouriteActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.productFavouritesbutton){
            Intent intent = new Intent(this, ProductFavouritesActivity.class);
            startActivity(intent);
        }
    }
}
