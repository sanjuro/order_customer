package com.vosto.customer.cart.vos;

import java.util.ArrayList;
import java.util.Date;

import org.joda.money.Money;

import com.vosto.customer.orders.vos.AddressVo;
import com.vosto.customer.stores.vos.StoreVo;

/**
 * Models the shopping cart.
 * An instance of this is stored in the app context so the cart is available throughout the session.
 *
 */
public class Cart {
	
	/*
	 * The user can only order from one store per order.
	 * Once this store variable is set, it can't be changed until the cart is closed.
	 */
	private StoreVo store;
	
	private ArrayList<CartItem> items;
	private AddressVo deliveryAddress; // if null, the customer will collect in-store
	private Date opened;
	private Date closed;
	private Money deliveryCost;
	
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
		Money total = this.getSubtotalBeforeDelivery();
		if(this.deliveryCost != null){
			total = total.plus(this.deliveryCost);
		}
		return total;
	}
	
	public Money getSubtotalBeforeDelivery(){
		Money subtotal = Money.parse("ZAR 0.00");
		for(int i = 0; i<this.items.size(); i++){
			subtotal = subtotal.plus(this.items.get(i).getSubtotal());
		}
		return subtotal;
	}
	
	
	
	public Money getDeliveryCost() {
		return deliveryCost;
	}

	public void setDeliveryCost(Money deliveryCost) {
		this.deliveryCost = deliveryCost;
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
	
	public AddressVo getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(AddressVo deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public void editItem(int itemIndex, CartItem newItem){
		if(itemIndex < 0 || itemIndex > this.getNumberOfItems()-1){
			return;
		}
		this.items.set(itemIndex, newItem);
	}
	
	public int getIndexForItem(CartItem item){
		return this.items.indexOf(item);
	}
	
	
}