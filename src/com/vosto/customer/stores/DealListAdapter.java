package com.vosto.customer.stores;

import android.widget.ImageView;
import com.vosto.customer.R;
import com.vosto.customer.stores.vos.DealVo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import static com.vosto.customer.utils.CommonUtilities.DEAL_IMAGE_SERVER_URL;

import com.androidquery.AQuery;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/08/04
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DealListAdapter extends ArrayAdapter<DealVo>{

    Context context;
    int layoutResourceId;
    DealVo deals[] = null;
    AQuery ag = null;

    public DealListAdapter(Context context, int layoutResourceId, DealVo[] deals) {
        super(context, layoutResourceId, deals);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.deals = deals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DealListItemHolder holder = null;
        ag = new AQuery(convertView);

        if(row == null)
        {
            holder = new DealListItemHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }else{
            holder =  (DealListItemHolder)row.getTag();
        }

        DealVo deal = this.deals[position];

        if(holder == null || deal == null){
            return row;
        }
        if(holder.lblDealImage == null || holder.lblDealDescription == null){
            holder.lblDealImage = (ImageView)row.findViewById(R.id.lblDealImage);
            holder.lblDealName = (TextView)row.findViewById(R.id.lblDealName);
            holder.lblDealDescription = (TextView)row.findViewById(R.id.lblDealDescription);
        }

        row.setTag(holder);

        Log.d("DEAL", "Deal Name " + deal.getName());
        Log.d("DEAL", "Deal Description " + deal.getDescription());

        holder.lblDealName.setText(deal.getName());
        holder.lblDealDescription.setText(deal.getDescription());

        String imageUrl = DEAL_IMAGE_SERVER_URL + deal.getDealImage();

        ag.id(R.id.lblDealImage).image(imageUrl, false, true, 0, 0, null, AQuery.FADE_IN);

        return row;
    }

    /**
     * This is called by Android to determine whether a certain list item should be clickable.
     * If this method returns false, the list item is not clickable.
     * The list item should only be clickable if the store is currently online.
     */
    public boolean isEnabled(int position){
        return this.deals.length > position && this.deals[position] != null && this.deals[position].getIsActive();
    }

    static class DealListItemHolder
    {
        ImageView lblDealImage;
        TextView lblDealName;
        TextView lblDealDescription;
    }

}
