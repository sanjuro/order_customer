package com.vosto.customer.loyalties;

import android.widget.*;
import com.vosto.customer.R;
import com.vosto.customer.loyalties.vos.LoyaltyCardVo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

import java.util.Locale;


/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/09/08
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoyaltyCardListAdapter extends ArrayAdapter<LoyaltyCardVo> {

    Context context;
    int layoutResourceId;
    LoyaltyCardVo[] loyaltyCards = null;

    public LoyaltyCardListAdapter(Context context, int layoutResourceId, LoyaltyCardVo[] loyaltyCards) {
        super(context, layoutResourceId, loyaltyCards);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.loyaltyCards = loyaltyCards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LoyaltyCarListItemHolder holder = null;

        if(row == null)
        {
            holder = new LoyaltyCarListItemHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }else{
            holder =  (LoyaltyCarListItemHolder)row.getTag();
        }

        LoyaltyCardVo loyaltyCard = loyaltyCards[position];

        if(holder == null || loyaltyCards == null){
            return row;
        }

        if(holder.lblLoyaltyName == null || holder.lblLoyaltyIsWon == null  || holder.lblLoyaltyCount == null || holder.lblLoyaltyDescription == null){
            holder.lblLoyaltyName = (TextView)row.findViewById(R.id.lblLoyaltyName);
            holder.lblLoyaltyDescription = (TextView)row.findViewById(R.id.lblLoyaltyDescription);
            holder.lblLoyaltyCount = (TextView)row.findViewById(R.id.lblLoyaltyCount);
            holder.lblLoyaltyPrize = (TextView)row.findViewById(R.id.lblLoyaltyPrize);
            holder.viewButton = (ImageButton)row.findViewById(R.id.viewButton);
            holder.lblLoyaltyIsWonHolder = (RelativeLayout)row.findViewById(R.id.lblLoyaltyIsWonHolder);
            holder.lblLoyaltyCountHolder = (RelativeLayout)row.findViewById(R.id.lblLoyaltyCountHolder);

        }

        row.setTag(holder);

        holder.lblLoyaltyName.setText(loyaltyCard.getLoyalty().getName());
        holder.lblLoyaltyDescription.setText(loyaltyCard.getLoyalty().getDescription());

        if (loyaltyCard.getIsWon()){
            holder.lblLoyaltyCount.setText("Go get your prize");
            holder.viewButton.setVisibility(View.VISIBLE);
            holder.lblLoyaltyCountHolder.setBackgroundColor(Color.parseColor("#27ae60"));
            holder.lblLoyaltyIsWonHolder.setBackgroundColor(Color.parseColor("#2ecc71"));
        }else{
            Integer total_left =  Integer.parseInt(loyaltyCard.getLoyalty().getWinCount()) -  Integer.parseInt(loyaltyCard.getCount()) ;
            holder.lblLoyaltyCount.setText(total_left + " more");
            holder.viewButton.setVisibility(View.INVISIBLE);
            holder.lblLoyaltyCountHolder.setBackgroundColor(Color.parseColor("#7f8c8d"));
            holder.lblLoyaltyIsWonHolder.setBackgroundColor(Color.parseColor("#95a5a6"));
        }


        // holder.lblLoyaltyWinCount.setText(loyaltyCard.getLoyalty().getWinCount())
        holder.lblLoyaltyPrize.setText(loyaltyCard.getLoyalty().getPrize());

        return row;
    }

    /**
     * This is called by Android to determine whether a certain list item should be clickable.
     * If this method returns false, the list item is not clickable.
     * The list item should only be clickable if the store is currently online.
     */
    public boolean isEnabled(int position){
        return this.loyaltyCards.length > position && this.loyaltyCards[position] != null;
    }


    static class LoyaltyCarListItemHolder
    {
        TextView lblLoyaltyName;
        TextView lblLoyaltyDescription;
        TextView lblLoyaltyIsWon;
        ImageView lblLoyaltyGift;
        TextView lblLoyaltyCount;
        TextView lblLoyaltyPrize;
        ImageButton viewButton;
        RelativeLayout lblLoyaltyIsWonHolder;
        RelativeLayout lblLoyaltyCountHolder;
    }
}
