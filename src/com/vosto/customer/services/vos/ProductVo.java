package com.vosto.customer.services.vos;

public class ProductVo {
	
	private int id;
	private int store_id;
	private String name;
	private String description;
	private double price;
	
	
	public ProductVo(){
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getStoreId() {
		return store_id;
	}


	public void setStoreId(int store_id) {
		this.store_id = store_id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}

	
}