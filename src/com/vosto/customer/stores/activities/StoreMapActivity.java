package com.vosto.customer.stores.activities;


import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.vosto.customer.R;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vosto.customer.stores.vos.StoreVo;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/05
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */

public class StoreMapActivity extends Activity
{
    private StoreVo store;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map);

        this.store = (StoreVo) this.getIntent().getSerializableExtra("store");

        LatLng storeLatLong = new LatLng(this.store.getLatitude(), this.store.getLongitude());

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();

        Marker kiel = map.addMarker(new MarkerOptions()
                    .position(storeLatLong)
                    .title(this.store.getName()));
//                    .icon(BitmapDescriptorFactory
//                            .fromResource(R.drawable.ic_launcher)));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLatLong, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_to_bottom);
    }

}