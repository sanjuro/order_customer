package com.vosto.customer.cart;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.cart.vos.CartItem;
import com.vosto.customer.utils.MoneyUtils;

public class CartItemAdapter extends ArrayAdapter<CartItem>{

    Context context; 
    int layoutResourceId;    
    ArrayList<CartItem> cartItems = null;
    
    public CartItemAdapter(Context context, int layoutResourceId, ArrayList<CartItem> cartItems) {
        super(context, layoutResourceId, cartItems);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CartItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new CartItemHolder();
            holder.lblProductName = (TextView)row.findViewById(R.id.lblProductName);
            holder.lblPrice = (TextView)row.findViewById(R.id.lblPrice);
            holder.lblVariant = (TextView)row.findViewById(R.id.lblVariant);
            holder.removeButton = (ImageButton)row.findViewById(R.id.remove_button);
            holder.editButton = (ImageButton)row.findViewById(R.id.edit_button);
            
            
            row.setTag(holder);
        }
        else
        {
            holder = (CartItemHolder)row.getTag();
        }
        
        CartItem item = cartItems.get(position);
        
       
        holder.lblProductName.setText(item.getProduct().getName());
        holder.lblPrice.setText(MoneyUtils.getRandString(item.getSubtotal()));
        if(item.getVariant() != null && item.getVariant().getOptionValues().length > 0){
        	holder.lblVariant.setText(item.getVariant().getOptionValues()[0].getName());
        }
        Log.d("CRT", "Returning row.");
        
        holder.editButton.setTag(item);
        holder.removeButton.setTag(item);
        
        return row;
    }
    
    static class CartItemHolder
    {
        TextView lblProductName;
        TextView lblVariant;
        // TextView lblQuantity;
        TextView lblPrice;
        ImageButton removeButton;
        ImageButton editButton;
        
    }
}