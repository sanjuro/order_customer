package com.vosto.customer.products;

import java.util.ArrayList;

import com.vosto.customer.R;
import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.TaxonVo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TaxonListAdapter extends ArrayAdapter<TaxonVo>{

    Context context; 
    int layoutResourceId;    
    ArrayList<TaxonVo> taxons = null;
    
    public TaxonListAdapter(Context context, int layoutResourceId, ArrayList<TaxonVo> taxons) {
        super(context, layoutResourceId, taxons);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.taxons = taxons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.d("TaxonsList", "Getting view at pos " + position);
        View row = convertView;
        TaxonListItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new TaxonListItemHolder();
            holder.txtTaxonName = (TextView)row.findViewById(R.id.txtTaxonName);
          
            
            row.setTag(holder);
        }
        else
        {
            holder = (TaxonListItemHolder)row.getTag();
        }
        Log.d("POS", "position: " + position);
        TaxonVo taxon = taxons.get(position);
        if(holder == null){
        	Log.d("ERROR", "holder is null");
        }
        
        Log.d("LEN", "Taxons length: " + taxons.size());
        holder.txtTaxonName.setText(taxon.getName());
       
        
        return row;
    }
    
    static class TaxonListItemHolder
    {
        TextView txtTaxonName;
    }
}