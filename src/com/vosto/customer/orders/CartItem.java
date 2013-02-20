package com.vosto.customer.orders;

import com.vosto.customer.services.vos.ProductVo;
import com.vosto.customer.services.vos.VariantVo;


public class CartItem {
	
	private int id;
	private ProductVo product;
	private VariantVo variant;
	private int quantity;
	private String comments;
	
	public CartItem(){
		this.quantity = 1;
		this.comments = "";
	}
	
	public CartItem(ProductVo product, VariantVo variant){
		this.product = product;
		this.variant = variant;
		this.quantity = 1;
		this.comments = "";
	}
	
	public CartItem(ProductVo product, VariantVo variant, int quantity){
		this.product = product;
		this.variant = variant;
		this.quantity = quantity;
		this.comments = "";
	}
	
	public CartItem(ProductVo product, int quantity){
		this.product = product;
		this.variant = null;
		this.quantity = quantity;
		this.comments = "";
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getSubtotal() {
		if(this.variant != null){
			return this.quantity * this.variant.getPrice();
		}else if(this.product != null){
			return this.quantity * this.product.getPrice();
		}else{
			return 0.0d;
		}
	}
	
	
	
}