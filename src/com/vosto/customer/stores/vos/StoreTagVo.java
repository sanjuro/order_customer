package com.vosto.customer.stores.vos;

import java.io.Serializable;

public class StoreTagVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String title;
	
	public StoreTagVo(){
		this.id = -1;
		this.title = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String toString(){
		return this.title;
	}
	
}