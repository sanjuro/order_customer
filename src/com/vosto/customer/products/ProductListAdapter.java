package com.vosto.customer.products;

import java.util.ArrayList;

import com.vosto.customer.R;
import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.utils.MoneyUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProductListAdapter extends ArrayAdapter<ProductVo>{

    Context context; 
    int layoutResourceId;    
    ProductVo[] products = null;
    
    public ProductListAdapter(Context context, int layoutResourceId, ProductVo[] products) {
        super(context, layoutResourceId, products);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.products = products;
        Log.d("PRD", "First product has variants: " + this.products[0].getVariants().length);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.d("StoresList", "Getting view at pos " + position);
        View row = convertView;
        StoreListItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new StoreListItemHolder();
            holder.lblProductName = (TextView)row.findViewById(R.id.lblProductName);
            holder.lblPrice = (TextView)row.findViewById(R.id.lblPrice);
            holder.addToCartButton = (ImageButton)row.findViewById(R.id.add_to_cart_button);
            
            row.setTag(holder);
        }
        else
        {
            holder = (StoreListItemHolder)row.getTag();
        }
        Log.d("POS", "position: " + position);
        ProductVo product = products[position];
        if(holder == null){
        	Log.d("ERROR", "holder is null");
        }
        if(product == null){
        	Log.d("ERROR", "store is null");
        }
        Log.d("LEN", "Products length: " + products.length);
        holder.lblProductName.setText(product.getName());
        holder.lblPrice.setText(MoneyUtils.getRandString(product.getPrice()));
        holder.addToCartButton.setTag(product);
        return row;
    }
    
    static class StoreListItemHolder
    {
        TextView lblProductName;
        TextView lblPrice;
        ImageButton addToCartButton;
    }
}