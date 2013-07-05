package com.vosto.customer.stores.vos;

import java.io.Serializable;

import com.google.common.base.Objects;

public class SuburbVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public SuburbVo(){
    }
    
    public int getId(){
    	return this.id;
    }
    
    public void setId(int id){
    	this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name).toString();
    }
}