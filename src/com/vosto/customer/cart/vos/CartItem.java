package com.vosto.customer.cart.vos;

import org.joda.money.Money;

import com.vosto.customer.products.vos.ProductVo;
import com.vosto.customer.products.vos.VariantVo;

/**
 * Models a cart item. Not to be confused with LineItem which is an item inside a completed order.
 * CartItems are inside the cart before the order is placed, LineItems are returned from the server as part
 * of an existing order's data.
 */
public class CartItem {
	
	private int id; // Not actually used...we just access the items by index.
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
			this.variant = product.getVariants()[0];
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
	
	public boolean equals(CartItem otherItem){
		if(otherItem.getProduct().getId() != this.getProduct().getId()){
			return false;
		}
		if(otherItem.getVariant().getId() != this.getVariant().getId()){
			return false;
		}
		if(otherItem.getQuantity() != this.getQuantity()){
			return false;
		}
		if(!otherItem.getSpecialInstructions().equals(this.getSpecialInstructions())){
			return false;
		}
		if(!otherItem.getSubtotal().equals(this.getSubtotal())){
			return false;
		}
		return true;
	}
	
	
	
}