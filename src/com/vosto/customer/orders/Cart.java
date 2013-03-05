package com.vosto.customer.orders;

import java.util.Date;
import java.util.ArrayList;

import org.joda.money.Money;

import com.vosto.customer.services.vos.StoreVo;

import android.util.Log;


public class Cart {
	
	private StoreVo store;
	private ArrayList<CartItem> items;
	private Date opened;
	private Date closed;
	
	public Cart(){
		this.opened = new Date();
		this.items = new ArrayList<CartItem>();
	}
	
	public Cart(StoreVo store){
		this.opened = new Date();
		this.items = new ArrayList<CartItem>();
		this.store = store;
	}
	
	public Cart(StoreVo store, ArrayList<CartItem> items){
		this.opened = new Date();
		this.items = items;
		this.store = store;
	}
	
	public void addItem(CartItem item){
		this.items.add(item);
	}
	
	public void removeItem(CartItem item){
		removeItem(this.items.indexOf(item));
	}
	
	public void removeItem(int index){
		this.items.remove(index);
	}
	
	public ArrayList<CartItem> getItems(){
		return this.items;
	}
	
	public Money getTotalPrice(){
		Money total = Money.parse("ZAR 0.00");
		for(int i = 0; i<this.items.size(); i++){
			total = total.plus(this.items.get(i).getSubtotal());
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
		Log.d("CRT", "Cart opened: " + this.opened);
		Log.d("CRT", "Cart closed: " + (this.closed == null ? "null" : this.closed));
		return this.opened != null && this.closed == null;
	}
	
	public int getNumberOfItems(){
		int numberOfItems = 0;
		for(int i = 0; i<this.items.size(); i++){
			numberOfItems += this.items.get(i).getQuantity();
		}
		return numberOfItems;
	}

	public StoreVo getStore() {
		return store;
	}

	public void setStore(StoreVo store) {
		this.store = store;
	}

	public Date getOpened() {
		return opened;
	}

	public void setOpened(Date opened) {
		this.opened = opened;
	}

	public Date getClosed() {
		return closed;
	}

	public void setClosed(Date closed) {
		this.closed = closed;
	}
	
	
}