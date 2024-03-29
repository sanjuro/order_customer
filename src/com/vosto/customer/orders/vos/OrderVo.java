package com.vosto.customer.orders.vos;

import java.io.Serializable;
import java.util.Date;

import org.joda.money.Money;

import com.vosto.customer.cart.vos.LineItemVo;

public class OrderVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String number;
    private String store_order_number;
	private String state;
    private String time_to_ready;
    private String store_name;
    private int store_id;
	private Date createdAt;
	private Date completedAt;
	private LineItemVo[] lineItems;
	private Money total;
	private Money adjustmentTotal;
	private AddressVo deliveryAddress;
	
	public OrderVo(){
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

    public String getStoreOrderNumber() {
        return store_order_number;
    }

    public void setStoreOrderNumber(String store_order_number) {
        this.store_order_number = store_order_number;
    }

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    public String getStoreName() {
        return store_name;
    }

    public void setStoreName(String store_name) {
        this.store_name = store_name;
    }

    public String getTimeToReady() {
        return time_to_ready;
    }

    public void setTimeToReady(String time_to_ready) {
        this.time_to_ready = time_to_ready;
    }

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public LineItemVo[] getLineItems() {
		return lineItems;
	}

	public void setLineItems(LineItemVo[] lineItems) {
		this.lineItems = lineItems;
	}

	public Money getTotal() {
		return total;
	}

	public void setTotal(Money total) {
		this.total = total;
	}
	
	public void setTotal(double dblTotal){
		this.total = Money.parse("ZAR " + dblTotal);
		this.total = this.total.withAmount(dblTotal);
	}
	
	public Money getSubtotalBeforeDelivery(){
		Money subtotal = this.total;
		if(this.adjustmentTotal != null && this.deliveryAddress != null){
			subtotal = subtotal.minus(this.adjustmentTotal);		
		}
		return subtotal;
	}

	public String getTime_to_ready() {
		return time_to_ready;
	}

	public void setTime_to_ready(String time_to_ready) {
		this.time_to_ready = time_to_ready;
	}

	public Money getAdjustmentTotal() {
		return adjustmentTotal;
	}
	
	public void setAdjustmentTotal(double dblAdjustmentTotal){
		this.adjustmentTotal = Money.parse("ZAR " + dblAdjustmentTotal);
		this.adjustmentTotal = this.adjustmentTotal.withAmount(dblAdjustmentTotal);
	}

	public void setAdjustmentTotal(Money adjustmentTotal) {
		this.adjustmentTotal = adjustmentTotal;
	}

	public AddressVo getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(AddressVo deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

}