package com.vosto.customer.services.vos;

import java.io.Serializable;

import org.joda.money.Money;

import com.vosto.customer.utils.MoneyUtils;

public class VariantVo implements Serializable {
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
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String sku;
	private int product_id;
	private boolean isMaster;
	private Money price;
	private int position;
	private OptionValueVo[] optionValues;
	
	
	
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



	public Money getPrice() {
		return this.price;
	}
	
	public String getPriceString(){
		return MoneyUtils.getRandString(this.price);
	}


	public void setPrice(double priceDouble){
		this.price = Money.parse("ZAR " + priceDouble);
		this.price = this.price.withAmount(priceDouble);
	}

	public void setPrice(Money price) {
		this.price = price;
	}



	public int getPosition() {
		return position;
	}



	public void setPosition(int position) {
		this.position = position;
	}



	public OptionValueVo[] getOptionValues() {
		return optionValues;
	}



	public void setOptionValues(OptionValueVo[] optionValues) {
		this.optionValues = optionValues;
	}

	

	
	
}