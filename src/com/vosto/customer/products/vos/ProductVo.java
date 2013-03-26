package com.vosto.customer.products.vos;

import java.io.Serializable;

import org.joda.money.Money;
import com.vosto.customer.utils.MoneyUtils;

public class ProductVo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int store_id;
	private String name;
	private String description;
	private Money price;
	private VariantVo[] variants;
	
	
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


	public Money getPrice() {
		return price;
	}
	
	public String getPriceString(){
		return MoneyUtils.getRandString(this.price);
	}

	public void setPrice(double price){
		Money money = Money.parse("ZAR " + price);
		this.price = money.withAmount(price);	
	}

	public void setPrice(Money price) {
		this.price = price;
	}


	public int getStore_id() {
		return store_id;
	}


	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}


	public VariantVo[] getVariants() {
		return variants;
	}


	public void setVariants(VariantVo[] variants) {
		this.variants = variants;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

	
}