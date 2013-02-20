package com.vosto.customer.products;

import java.util.ArrayList;

import com.vosto.customer.R;
import com.vosto.customer.services.vos.ProductVo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductListAdapter extends ArrayAdapter<ProductVo>{

    Context context; 
    int layoutResourceId;    
    ArrayList<ProductVo> products = null;
    
    public ProductListAdapter(Context context, int layoutResourceId, ArrayList<ProductVo> products) {
        super(context, layoutResourceId, products);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.products = products;
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
            holder.txtProductName = (TextView)row.findViewById(R.id.txtProductName);
          
            
            row.setTag(holder);
        }
        else
        {
            holder = (StoreListItemHolder)row.getTag();
        }
        Log.d("POS", "position: " + position);
        ProductVo product = products.get(position);
        if(holder == null){
        	Log.d("ERROR", "holder is null");
        }
        if(product == null){
        	Log.d("ERROR", "store is null");
        }
        Log.d("LEN", "Products length: " + products.size());
        holder.txtProductName.setText(product.getName());
       
        
        return row;
    }
    
    static class StoreListItemHolder
    {
        TextView txtProductName;
    }
}