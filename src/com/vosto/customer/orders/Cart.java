package com.vosto.customer.orders;

import java.util.Date;
import java.util.ArrayList;


public class Cart {
	
	private ArrayList<CartItem> items;
	private Date opened;
	private Date closed;
	
	public Cart(){
		this.opened = new Date();
		this.items = new ArrayList<CartItem>();
	}
	
	public Cart(ArrayList<CartItem> items){
		this.opened = new Date();
		this.items = items;
	}
	
	public void addItem(CartItem item){
		this.items.add(item);
	}
	
	public void removeItem(CartItem item){
		removeItem(item.getId());
	}
	
	public void removeItem(int itemId){
		for(int i = 0; i<this.items.size(); i++){
			if(this.items.get(i).getId() == itemId){
				this.items.remove(i);
			}
		}
	}
	
	public ArrayList<CartItem> getItems(){
		return this.items;
	}
	
	public double getTotalPrice(){
		double total = 0.0d;
		for(int i = 0; i<this.items.size(); i++){
			total += this.items.get(i).getSubtotal();
		}
		return total;
	}
	
	public Date getTimeOpened(){
		return this.opened;
	}
	
	public Date getTimeClosed(){
		return this.closed;
	}
	
	public void close(){
		this.closed = new Date();
	}
	
	public boolean isOpen(){
		return this.opened != null && this.closed == null;
	}
	
	public int getNumberOfItems(){
		int numberOfItems = 0;
		for(int i = 0; i<this.items.size(); i++){
			numberOfItems += this.items.get(i).getQuantity();
		}
		return numberOfItems;
	}
}