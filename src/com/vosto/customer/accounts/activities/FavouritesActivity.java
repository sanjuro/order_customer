package com.vosto.customer.accounts.activities;

import java.util.Arrays;

import com.vosto.customer.products.services.GetFavouriteProductsService;
import com.vosto.customer.products.services.GetProductsResult;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.services.GetStoreFavouriteService;
import com.vosto.customer.stores.services.GetStoresResult;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.Constants;

import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FavouritesActivity extends VostoBaseActivity implements OnCheckedChangeListener, OnRestReturn {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        CheckBox products = (CheckBox)findViewById(R.id.products);
        CheckBox stores = (CheckBox)findViewById(R.id.stores);
        products.setOnCheckedChangeListener(this);
        stores.setOnCheckedChangeListener(this);

        if (products.isChecked()) {
            fetchProducts();
        }

        if (stores.isChecked()) {
            fetchStores();
        }
    }

    private void fetchStores() {
        new GetStoreFavouriteService(this, this).execute();
    }

    private void fetchProducts() {
        new GetFavouriteProductsService(this, this).execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.products:
                if (isChecked) {
                    fetchProducts();
                } else {
                    //remove products from listview
                }
                break;
            case R.id.stores:
                if (isChecked) {
                    fetchStores();
                } else {
                    //remove stores from listview
                }
                break;
        }
    }

    @Override
    public void onRestReturn(RestResult result) {
        if (result instanceof GetProductsResult) {
            ProductVo[] products = ((GetProductsResult) result).getProducts();
            Log.d(Constants.TAG, Arrays.asList(products).toString());
            //update products and update listview
        }

        if (result instanceof GetStoresResult) {
            StoreVo[] stores = ((GetStoresResult) result).getStores();
            Log.d(Constants.TAG, Arrays.asList(stores).toString());
            //update stores and update listview
        }

    }
}
