package com.vosto.customer.orders;

import org.joda.money.Money;

import android.util.Log;

import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.VariantVo;


public class CartItem {
	
	private int id;
	private ProductVo product;
	private VariantVo variant;
	private int quantity;
	private String specialInstructions;
	
	public CartItem(){
		this.quantity = 1;
		this.specialInstructions = "";
	}
	
	public CartItem(ProductVo product, VariantVo variant){
		this.product = product;
		this.variant = variant;
		this.quantity = 1;
		this.specialInstructions = "";
	}
	
	public CartItem(ProductVo product, VariantVo variant, int quantity){
		this.product = product;
		this.variant = variant;
		this.quantity = quantity;
		this.specialInstructions = "";
	}
	
	public CartItem(ProductVo product, int quantity){
		this.product = product;
		
		// Default to first variant:
		if(product.getVariants().length > 0){
			Log.d("CRT", "Defaulting to first variant.");
			this.variant = product.getVariants()[0];
		}else{
			Log.d("CRT", "NO variants for the product. Can't default.");
		}
		this.quantity = quantity;
		this.specialInstructions = "";
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProductVo getProduct() {
		return product;
	}

	public void setProduct(ProductVo product) {
		this.product = product;
	}

	public VariantVo getVariant() {
		return variant;
	}

	public void setVariant(VariantVo variant) {
		this.variant = variant;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getSpecialInstructions() {
		return specialInstructions != null ? specialInstructions.trim() : "";
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions.trim();
	}

	public Money getSubtotal() {
		if(this.variant != null){
			return this.variant.getPrice().multipliedBy(this.quantity);
		}else if(this.product != null){
			return this.product.getPrice().multipliedBy(this.quantity);
		}else{
			return Money.parse("ZAR 0.00");
		}
	}
	
	
	
}