package com.vosto.customer.orders;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.vosto.customer.R;
import com.vosto.customer.orders.vos.OrderVo;

import static com.vosto.customer.utils.CommonUtilities.STORE_IMAGE_SERVER_URL;

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
            holder.lblOrderStatusBadge = (ImageView)row.findViewById(R.id.lblStatus);
            row.setTag(holder);
        }
        else
        {
            holder = (OrderHolder)row.getTag();
        }
        
        OrderVo order = orders[position];

        if (order.getStoreOrderNumber() == null){
            holder.lblOrderNumber.setText(order.getNumber());
        }else{
            holder.lblOrderNumber.setText(order.getStoreOrderNumber());
        }
        
        SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
        
        holder.lblDateOrdered.setText("Ordered at: " + format.format(order.getCreatedAt()));
        holder.lblDateOrdered.setTypeface(null, Typeface.ITALIC);

        //Show the correct status badge based on the order state:
        if(order.getState().toLowerCase(Locale.getDefault()).equals("ready")){
            holder.lblOrderStatusBadge.setImageResource(R.drawable.ready_badge);
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("collected")){
            holder.lblOrderStatusBadge.setImageResource(R.drawable.collected_badge);
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("in_progress")){
            holder.lblOrderStatusBadge.setImageResource(R.drawable.in_progress_badge);
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("cancelled")){
            holder.lblOrderStatusBadge.setImageResource(R.drawable.cancelled_badge);
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("not_collected")){
            holder.lblOrderStatusBadge.setImageResource(R.drawable.not_collected_badge);
        }else{
            holder.lblOrderStatusBadge.setImageResource(R.drawable.in_progress_badge);
        }
      
        return row;
    }
    
    static class OrderHolder
    {
        TextView lblOrderNumber;
        TextView lblDateOrdered;
        ImageView lblOrderStatusBadge;
    }
}