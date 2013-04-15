package com.vosto.customer.favourites.activities;

import java.util.Arrays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.TextView;
import com.agimind.widget.SlideHolder;
import com.vosto.customer.R;
import com.vosto.customer.VostoBaseActivity;
import com.vosto.customer.favourites.services.GetStoreFavouriteResult;
import com.vosto.customer.favourites.services.GetStoreFavouriteService;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.services.OnRestReturn;
import com.vosto.customer.services.RestResult;
import com.vosto.customer.stores.StoreListAdapter;
import com.vosto.customer.stores.vos.StoreVo;
import com.vosto.customer.utils.Constants;
import com.vosto.customer.utils.NetworkUtils;

public class StoreFavouritesActivity extends VostoBaseActivity implements OnRestReturn, AdapterView.OnItemClickListener {

    private StoreVo[] stores;
    private SlideHolder mSlideHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_favourites);

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
            this.stores = ((GetStoreFavouriteResult) result).getStores();
            Log.d(Constants.TAG, Arrays.asList(this.stores).toString());

            //update stores and update listview
            ListView list = (ListView)findViewById(R.id.lstStores);
            list.setOnItemClickListener(this);
            list.setAdapter(new StoreListAdapter(this, R.layout.store_item_row, this.stores));

        }

    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        if(!NetworkUtils.isNetworkAvailable(this)){
            this.showAlertDialog("Connection Error", "Please connect to the internet.");
            return;
        }
        Log.d("STO", "Passing store to TaxonActivity: " + this.stores[position].getId());
        Intent intent = new Intent(this, TaxonsActivity.class);
        intent.putExtra("store", this.stores[position]);
        intent.putExtra("storeName", this.stores[position].getName());
        intent.putExtra("storeTel", this.stores[position].getManagerContact());
        intent.putExtra("storeAddress", this.stores[position].getAddress());
        intent.putExtra("callingActivity", "StoresActivity");
        startActivity(intent);
        // finish();
    }

    public void productFavouritesClicked(View v) {
        Intent intent = new Intent(this, ProductFavouritesActivity.class);
        startActivity(intent);
    }

    public void storeFavouritesClicked(View v) {
        Intent intent = new Intent(this, StoreFavouritesActivity.class);
        startActivity(intent);
    }
}
