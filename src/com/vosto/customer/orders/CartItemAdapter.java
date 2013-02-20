package com.vosto.customer.orders;

import java.util.ArrayList;

import com.vosto.customer.R;
import com.vosto.customer.services.vos.StoreVo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            holder.lblOptionValue = (TextView)row.findViewById(R.id.lblOptionValue);
            
            row.setTag(holder);
        }
        else
        {
            holder = (CartItemHolder)row.getTag();
        }
        
        CartItem item = cartItems.get(position);
        
       
        holder.lblProductName.setText(item.getQuantity() + " x " + item.getProduct().getName());
        holder.lblPrice.setText("R " + item.getSubtotal());
        if(item.getVariant() != null && item.getVariant().getOptionValues().size() > 0){
        	holder.lblOptionValue.setText(item.getVariant().getOptionValues().get(0).getName());
        }
        Log.d("CRT", "Returning row.");
        return row;
    }
    
    static class CartItemHolder
    {
        TextView lblProductName;
        TextView lblOptionValue;
       // TextView lblQuantity;
        TextView lblPrice;
        
    }
}