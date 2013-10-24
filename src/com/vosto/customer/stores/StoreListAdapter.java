package com.vosto.customer.stores;

import android.widget.ImageView;
import com.vosto.customer.R;
import com.vosto.customer.stores.vos.StoreVo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import static com.vosto.customer.utils.CommonUtilities.STORE_IMAGE_SERVER_URL;

import com.androidquery.AQuery;

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
        View row = convertView;
        StoreListItemHolder holder = null;
        AQuery ag = new AQuery(convertView);

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
        if(holder.lblStoreImage == null || holder.lblStoreName == null || holder.lblDistance == null || holder.lblAddress == null || holder.lblSelectionArrow == null ){
        	 holder.lblStoreImage = (ImageView)row.findViewById(R.id.lblStoreImage);
             holder.lblStoreName = (TextView)row.findViewById(R.id.lblStoreName);
             holder.lblDistance = (TextView)row.findViewById(R.id.lblDistance);
             holder.lblAddress = (TextView)row.findViewById(R.id.lblAddress);
             holder.lblStatusOpen = (TextView)row.findViewById(R.id.lblStatusOpen);
             holder.lblStatusClosed = (TextView)row.findViewById(R.id.lblStatusClosed);
             holder.lblStatusDelivery = (TextView)row.findViewById(R.id.lblStatusDelivery);
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

        String imageUrl = STORE_IMAGE_SERVER_URL + store.getStoreImage();
        Bitmap preset = ag.getCachedImage(imageUrl);

        ag.id(R.id.lblStoreImage).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN);

        // Hide the arrow if the store is offline, and show the appropriate status banner:
        if (store.getIsOnline()){
            holder.lblStatusOpen.setVisibility(View.VISIBLE);
            holder.lblStatusClosed.setVisibility(View.GONE);
        }else{
            holder.lblStatusClosed.setVisibility(View.VISIBLE);
            holder.lblStatusOpen.setVisibility(View.GONE);
        }

        if (store.canDeliver()){
            holder.lblStatusDelivery.setVisibility(View.VISIBLE);
        }else{
            holder.lblStatusDelivery.setVisibility(View.GONE);
        }
      
        return row;
    }
    
    /**
     * This is called by Android to determine whether a certain list item should be clickable.
     * If this method returns false, the list item is not clickable.
     * The list item should only be clickable if the store is currently online.
     */
    public boolean isEnabled(int position){
    	return this.stores.length > position && this.stores[position] != null && this.stores[position].getIsOnline();
    }

    
    static class StoreListItemHolder
    {
        ImageView lblStoreImage;
        TextView lblStoreName;
        TextView lblDistance;
        TextView lblAddress;
        TextView lblStatusOpen;
        TextView lblStatusClosed;
        TextView lblStatusDelivery;
        ImageView lblSelectionArrow;
    }
}