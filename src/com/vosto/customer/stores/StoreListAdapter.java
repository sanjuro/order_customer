package com.vosto.customer.stores;

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
        View row = convertView;
        StoreListItemHolder holder = null;
        ag = new AQuery(convertView);

        if(row == null)
        {
        	holder = new StoreListItemHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }else{
        	holder =  (StoreListItemHolder)row.getTag();
        }
        
        StoreVo store = stores[position];
        
        if(holder == null || store == null){
        	return row;
        }
        if(holder.lblStoreImage == null || holder.lblStoreName == null || holder.lblDistance == null || holder.lblAddress == null || holder.lblSelectionArrow == null || holder.lblStatus == null){
        	 holder.lblStoreImage = (ImageView)row.findViewById(R.id.lblStoreImage);
             holder.lblStoreName = (TextView)row.findViewById(R.id.lblStoreName);
             holder.lblDistance = (TextView)row.findViewById(R.id.lblDistance);
             holder.lblAddress = (TextView)row.findViewById(R.id.lblAddress);
             holder.lblSelectionArrow = (ImageView)row.findViewById(R.id.lblSelectionArrow);
             holder.lblStatus = (ImageView)row.findViewById(R.id.lblStatus);
        }
        
        row.setTag(holder);      
       
        holder.lblStoreName.setText(store.getName());
        holder.lblAddress.setText(store.getAddress());
        
        if(store.getDistance() > -1){
        	holder.lblDistance.setText(store.getDistance() + " km");
        	holder.lblDistance.setVisibility(View.VISIBLE);
        }else{
        	holder.lblDistance.setVisibility(View.INVISIBLE);
        } 

        String imageUrl = IMAGE_SERVER_URL + store.getStoreImage();
        ag.id(R.id.lblStoreImage).image(imageUrl, false, false, 0, 0, null, AQuery.FADE_IN);

        Log.d("STO", "Stores name: " + store.getName() + " online: " + store.getIsOnline());
        // Set stores to unclickable when offline
        if (!store.getIsOnline()){
            holder.lblSelectionArrow.setVisibility(View.INVISIBLE);
            holder.lblStatus.setImageResource(R.drawable.store_status_closed);
        }else{
            holder.lblSelectionArrow.setVisibility(View.VISIBLE);
            holder.lblStatus.setImageResource(R.drawable.store_status_open);
        }
        
        row.setFocusable(store.getIsOnline());
        row.setClickable(store.getIsOnline());
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