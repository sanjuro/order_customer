package com.vosto.customer.products.vos;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.money.Money;

import android.util.Log;

import com.vosto.customer.utils.MoneyUtils;

public class VariantVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String sku;
	private int product_id;
	private boolean isMaster;
	private Money price;
	private int position;
	private ConcurrentHashMap<String, String> optionValueMap;
	
	
	
	public VariantVo(){
	}
	
	/**
	 * Copy constructor
	 * @param other
	 */
	public VariantVo(VariantVo other){
		this.id = other.getId();
		this.sku = other.getSku();
		this.product_id = other.getProduct_id();
		this.isMaster = other.isMaster();
		this.price = Money.of(other.getPrice());
		this.position = other.getPosition();
		this.optionValueMap = other.getOptionValueMap();
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



	public ConcurrentHashMap<String, String> getOptionValueMap() {
		return optionValueMap;
	}



	public void setOptionValueMap(ConcurrentHashMap<String, String> map) {
		this.optionValueMap = map;
	}
	
	/**
	 * Checks whether this variant matches the given combination of option values.
	 * @param optionCombinations - A map of option types + values
	 * @return whether this variant matches the given option combination
	 */
	public boolean matchesOptionCombination(ConcurrentHashMap<String, String> optionCombinations){
		Log.d("CHK", "Checking " + optionCombinations.size() + " size map against internal " + this.optionValueMap.size() + " size map.");
		return optionCombinations.equals(this.optionValueMap);
	}
	
	/**
	 * Gets the option value associated with the given option type for this variant,
	 * or null if the value is not set.
	 * 
	 * @param optionType The option type to search for
	 * @return The value of the option type, or null if it's not set in this variant.
	 */
	public String getValueForOptionType(String optionType){
		return this.optionValueMap.get(optionType.trim().toLowerCase(Locale.US));
	}
	
	/**
	 * Checks whether this variant has the specified value for the specified option type.
	 * 
	 * @param optionType
	 * @param optionValue
	 * @return
	 */
	public boolean hasOptionValue(String optionType, String optionValue){
		String internalValue =  this.optionValueMap.get(optionType.trim().toLowerCase(Locale.US));
		return internalValue != null && optionValue.trim().toLowerCase(Locale.US).equals(internalValue);
	}
	
	public void printOptionMap(){
		 Iterator<Map.Entry<String, String>> it = this.optionValueMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
		        Log.d("MAP", "Map val: " + pairs.getKey() + " = " + pairs.getValue());
		    }
	}
	
	
	/**
	 * Returns a string representation of this variant's values, in the format "penne, full"
	 * This can be displayed on the cart so the user knows what varient they are ordering.
	 * @return
	 */
	public String getOptionsString(){
		String optionsString = "";
		Iterator<Map.Entry<String, String>> it = this.getOptionValueMap().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
	        optionsString += pairs.getValue() + ", ";
	    }
	    if(optionsString.substring(optionsString.length() - 2, optionsString.length()).equals(", ")){
	    	optionsString = optionsString.substring(0, optionsString.length() - 2);
	    }
	    return optionsString;
	}

}