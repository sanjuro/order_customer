package com.vosto.customer.stores.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import com.vosto.customer.R;
import com.vosto.customer.products.activities.TaxonsActivity;
import com.vosto.customer.stores.vos.StoreVo;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/10/06
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapDialog extends Dialog implements android.view.View.OnClickListener {

    private TaxonsActivity taxonsActvity;
    private StoreVo store;
    private GoogleMap map;

    public MapDialog(TaxonsActivity taxonsActvity, StoreVo store){
        super(taxonsActvity, R.style.DialogSlideAnim);
        this.taxonsActvity = taxonsActvity;
        this.store = store;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(taxonsActvity).inflate(R.layout.dialog_map, null);
        setContentView(dialogLayout);

        LatLng storeLatLong = new LatLng(this.store.getLatitude(), this.store.getLongitude());

        map = ((MapFragment) this.taxonsActvity.getFragmentManager().findFragmentById(R.id.map))
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
    public void onClick(View v){
        switch (v.getId()){
            case R.id.backButton:
                this.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnim;
                dismiss();
                break;
        }
    }
}