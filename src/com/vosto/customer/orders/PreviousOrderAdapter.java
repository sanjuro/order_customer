package com.vosto.customer.orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.services.vos.OrderVo;
import com.vosto.customer.utils.MoneyUtils;

public class PreviousOrderAdapter extends ArrayAdapter<OrderVo>{

    Context context; 
    int layoutResourceId;    
    OrderVo[] orders = null;
    
    public PreviousOrderAdapter(Context context, int layoutResourceId, OrderVo[] orders) {
        super(context, layoutResourceId, orders);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OrderHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new OrderHolder();
            holder.lblOrderNumber = (TextView)row.findViewById(R.id.lblOrderNumber);
            holder.lblDateOrdered = (TextView)row.findViewById(R.id.lblDateOrdered);
            
            row.setTag(holder);
        }
        else
        {
            holder = (OrderHolder)row.getTag();
        }
        
        OrderVo order = orders[position];

        holder.lblOrderNumber.setText(order.getNumber());
        
        SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
        
        holder.lblDateOrdered.setText("Ordered at: " + format.format(order.getCreatedAt()));
        holder.lblDateOrdered.setTypeface(null, Typeface.ITALIC);
      
        return row;
    }
    
    static class OrderHolder
    {
        TextView lblOrderNumber;
        TextView lblDateOrdered;
    }
}