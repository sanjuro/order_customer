package com.vosto.customer.services.vos;

import java.util.ArrayList;

public class VariantVo {
	/*
	 * 
	 * {
            "variant": {
                "deleted_at": null,
                "id": 7,
                "is_master": false,
                "position": 10,
                "price": "76.0",
                "product_id": 1,
                "sku": "260"
            }
        },
	 * 
	 */
	
	private int id;
	private String sku;
	private int product_id;
	private boolean isMaster;
	private double price;
	private int position;
	private ArrayList<OptionValueVo> optionValues;
	
	
	
	public VariantVo(){
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getSku() {
		return sku;
	}



	public void setSku(String sku) {
		this.sku = sku;
	}



	public int getProduct_id() {
		return product_id;
	}



	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}



	public boolean isMaster() {
		return isMaster;
	}



	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}



	public double getPrice() {
		return price;
	}



	public void setPrice(double price) {
		this.price = price;
	}



	public int getPosition() {
		return position;
	}



	public void setPosition(int position) {
		this.position = position;
	}



	public ArrayList<OptionValueVo> getOptionValues() {
		return optionValues;
	}



	public void setOptionValues(ArrayList<OptionValueVo> optionValues) {
		this.optionValues = optionValues;
	}

	

	
	
}