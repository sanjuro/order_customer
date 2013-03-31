package com.vosto.customer.stores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vosto.customer.R;
import com.vosto.customer.stores.vos.StoreTagVo;

public class TagsListAdapter extends ArrayAdapter<StoreTagVo>{

    Context context; 
    int layoutResourceId;    
    StoreTagVo tags[] = null;
    
    public TagsListAdapter(Context context, int layoutResourceId, StoreTagVo[] tags) {
        super(context, layoutResourceId, tags);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.tags = tags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TagsListItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new TagsListItemHolder();
            holder.lblTagTitle = (TextView)row.findViewById(R.id.lblTagTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (TagsListItemHolder)row.getTag();
        }
        StoreTagVo tag = tags[position];
        
        if(holder != null && holder.lblTagTitle != null && tag != null){
        	holder.lblTagTitle.setText(tag.getName());
        }
        return row;
    }
    
    static class TagsListItemHolder
    {
        TextView lblTagTitle;
    }
}