package com.vosto.customer.orders;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.vosto.customer.R;
import com.vosto.customer.orders.vos.OrderVo;
import com.vosto.customer.utils.MoneyUtils;

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
            holder.orderState = (TextView)row.findViewById(R.id.orderState);
            holder.lblStoreName = (TextView)row.findViewById(R.id.lblStoreName);
            holder.deliveryType = (TextView)row.findViewById(R.id.deliveryType);
            holder.orderStateBackground =  (LinearLayout)row.findViewById(R.id.orderStateBackground);
            holder.orderDeliveryImage = (ImageView)row.findViewById(R.id.orderDeliveryImage);
            row.setTag(holder);
        }
        else
        {
            holder = (OrderHolder)row.getTag();
        }
        
        OrderVo order = this.orders[position];

        Log.d("ORD", "Adapter Current Order: " + order.getNumber());
        Log.d("ORD", "Adapter Current Order: " + order.getStoreOrderNumber());
        Log.d("ORD", "Adapter Current Order: " + order.getState());

        holder.lblOrderNumber.setText(replaceIfNull(order.getStoreOrderNumber(), order.getNumber()));

        SimpleDateFormat format = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        
        holder.lblDateOrdered.setText("Ordered at: " + format.format(order.getCreatedAt()));
        holder.lblDateOrdered.setTypeface(null, Typeface.ITALIC);

        holder.lblStoreName.setText(order.getStoreName());

        //Show the correct status badge based on the order state:
        if(order.getState().toLowerCase(Locale.getDefault()).equals("ready")){
            holder.orderState.setText("READY");
            holder.orderStateBackground.setBackgroundColor(Color.parseColor("#92d050"));
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("collected")){
            holder.orderState.setText("COLLECTED");
            holder.orderStateBackground.setBackgroundColor(Color.parseColor("#92d050"));
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("in_progress")){
            holder.orderState.setText("IN PROGRESS");
            holder.orderStateBackground.setBackgroundColor(Color.parseColor("#ffc000"));
        }else if(order.getState().toLowerCase(Locale.getDefault()).equals("cancelled")){
            holder.orderState.setText("CANCELLED");
            holder.orderStateBackground.setBackgroundColor(Color.parseColor("#f67846"));
        }else{
            holder.orderState.setText("UNCONFIRMED");
            holder.orderStateBackground.setBackgroundColor(Color.parseColor("#c00000"));
        }

        if(order.getDeliveryAddress() != null && !order.getDeliveryAddress().isEmpty() && order.getAdjustmentTotal() != null){
            holder.deliveryType.setText("Delivery");
            holder.orderDeliveryImage.setImageResource(R.drawable.delivery_icon);
        }else{
            holder.deliveryType.setText("Collect");
            holder.orderDeliveryImage.setImageResource(R.drawable.collection_icon);
        }
      
        return row;
    }
    
    static class OrderHolder
    {
        TextView lblOrderNumber;
        TextView lblDateOrdered;
        TextView orderState;
        TextView lblStoreName;
        TextView deliveryType;
        LinearLayout orderStateBackground;
        ImageView orderDeliveryImage;
    }

    public static <T> T replaceIfNull(T objectToCheck, T defaultValue) {
        return objectToCheck==null ? defaultValue : objectToCheck;
    }
}