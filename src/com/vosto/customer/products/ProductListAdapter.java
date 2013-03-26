package com.vosto.customer.products;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.utils.MoneyUtils;

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
        ProductListItemHolder holder = null;
        
        if(row == null || !(row.getTag() instanceof ProductListItemHolder))
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ProductListItemHolder();
            holder.lblProductName = (TextView)row.findViewById(R.id.lblProductName);
            holder.lblPrice = (TextView)row.findViewById(R.id.lblPrice);
            holder.addToCartButton = (Button)row.findViewById(R.id.add_to_cart_button);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ProductListItemHolder)row.getTag();
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
        
        row.setTag(product);
        return row;
    }
    
    static class ProductListItemHolder
    {
        TextView lblProductName;
        TextView lblPrice;
        Button addToCartButton;
    }
}