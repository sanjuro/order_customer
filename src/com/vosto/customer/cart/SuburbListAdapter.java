package com.vosto.customer.cart;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vosto.customer.stores.vos.SuburbVo;

public class SuburbListAdapter extends ArrayAdapter<SuburbVo> {

    private Context context;
    private SuburbVo[] suburbs;
    
    public SuburbListAdapter(Context context, int textViewResourceId, SuburbVo[] suburbs) {
        super(context, textViewResourceId, suburbs);
        this.context = context;
        this.suburbs = suburbs;
    }

    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setText(this.suburbs[position].getName());
        return v;
    }

    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setText(this.suburbs[position].getName());
        v.setTextSize(20);
        return v;
    }

}