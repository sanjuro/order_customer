package com.vosto.customer.orders;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.cart.vos.LineItemVo;
import com.vosto.customer.utils.MoneyUtils;

public class CurrentOrderItemAdapter extends ArrayAdapter<LineItemVo>{

    Context context; 
    int layoutResourceId;    
    LineItemVo[] lineItems = null;
    
    public CurrentOrderItemAdapter(Context context, int layoutResourceId, LineItemVo[] lineItems) {
        super(context, layoutResourceId, lineItems);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.lineItems = lineItems;
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
            holder.lblPrice = (TextView)row.findViewById(R.id.lblProductPrice);
            holder.lblProductQuantity = (TextView)row.findViewById(R.id.lblProductQuantity);
            
            row.setTag(holder);
        }
        else
        {
            holder = (CartItemHolder)row.getTag();
        }
        
        LineItemVo item = lineItems[position];

        holder.lblProductName.setText(item.getName());
        holder.lblPrice.setText(MoneyUtils.getRandString(item.getPrice()));
        holder.lblProductQuantity.setText(item.getQuantity() + " x ");

        return row;
    }
    
    static class CartItemHolder
    {
        TextView lblProductName;
        TextView lblPrice;
        TextView lblProductQuantity;
    }
}