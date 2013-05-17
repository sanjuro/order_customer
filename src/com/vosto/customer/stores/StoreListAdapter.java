package com.vosto.customer.stores;

import android.widget.ImageButton;
import android.widget.ImageView;
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

import static com.vosto.customer.utils.CommonUtilities.IMAGE_SERVER_URL;

import com.androidquery.AQuery;

public class StoreListAdapter extends ArrayAdapter<StoreVo>{

    Context context; 
    int layoutResourceId;    
    StoreVo stores[] = null;
    AQuery ag = null;

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
        ag = new AQuery(convertView);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new StoreListItemHolder();
            holder.lblStoreImage = (ImageView)row.findViewById(R.id.lblStoreImage);
            holder.lblStoreName = (TextView)row.findViewById(R.id.lblStoreName);
            holder.lblDistance = (TextView)row.findViewById(R.id.lblDistance);
            holder.lblAddress = (TextView)row.findViewById(R.id.lblAddress);
            holder.lblSelectionArrow = (ImageView)row.findViewById(R.id.lblSelectionArrow);
            holder.lblStatus = (ImageView)row.findViewById(R.id.lblStatus);
            
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
        // Log.d("LEN", "Stores length: " + stores.length);
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

        String imageUrl = IMAGE_SERVER_URL + store.getStoreImage();
        ag.id(R.id.lblStoreImage).image(imageUrl);


        Log.d("STO", "Stores name: " + store.getName() + " online: " + store.getIsOnline());
        // Set stores to unclickable when offline
        if (!store.getIsOnline()){
            Log.d("STO", "Stores name: " + store.getName() + " offline ");
            holder.lblDistance.setVisibility(View.INVISIBLE);
            holder.lblSelectionArrow.setVisibility(View.INVISIBLE);

            holder.lblStatus.setImageResource(R.drawable.store_status_closed);

            row.setFocusable(true);
            row.setClickable(true);
        }else{
            Log.d("STO", "Stores name: " + store.getName() + " online ");
            holder.lblDistance.setVisibility(View.VISIBLE);
            holder.lblSelectionArrow.setVisibility(View.VISIBLE);

            holder.lblStatus.setImageResource(R.drawable.store_status_open);

            row.setFocusable(false);
            row.setClickable(false);
        }
        return row;

    }

    
    static class StoreListItemHolder
    {
        ImageView lblStoreImage;
        TextView lblStoreName;
        TextView lblDistance;
        TextView lblAddress;
        ImageView lblStatus;
        ImageView lblSelectionArrow;
    }
}