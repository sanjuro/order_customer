package com.vosto.customer.stores;

import com.vosto.customer.R;
import com.vosto.customer.stores.vos.StoreVo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StoreListAdapter extends ArrayAdapter<StoreVo>{

    Context context; 
    int layoutResourceId;    
    StoreVo stores[] = null;
    
    public StoreListAdapter(Context context, int layoutResourceId, StoreVo[] stores) {
        super(context, layoutResourceId, stores);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.stores = stores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.d("StoresList", "Getting view at pos " + position);
        View row = convertView;
        StoreListItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new StoreListItemHolder();
            holder.lblStoreName = (TextView)row.findViewById(R.id.lblStoreName);
            holder.lblDistance = (TextView)row.findViewById(R.id.lblDistance);
            holder.lblAddress = (TextView)row.findViewById(R.id.lblAddress);
            
            row.setTag(holder);
        }
        else
        {
            holder = (StoreListItemHolder)row.getTag();
        }
        Log.d("POS", "position: " + position);
        StoreVo store = stores[position];
        if(holder == null){
        	Log.d("ERROR", "holder is null");
        }
        if(store == null){
        	Log.d("ERROR", "store is null");
        }
        Log.d("LEN", "Stores length: " + stores.length);
        if(holder != null && holder.lblStoreName != null && store != null){
        	holder.lblStoreName.setText(store.getName());
        }
        if(holder != null && holder.lblDistance != null && store != null && store.getDistance() >= 0){
        	holder.lblDistance.setText(store.getDistance() + "km");
        	holder.lblDistance.setVisibility(View.VISIBLE);
        }else{
        	holder.lblDistance.setVisibility(View.INVISIBLE);
        }
        if(holder != null && holder.lblAddress != null && store != null){
        	holder.lblAddress.setText(store.getAddress());
        }
        return row;
    }
    
    static class StoreListItemHolder
    {
        TextView lblStoreName;
        TextView lblDistance;
        TextView lblAddress;
    }
}