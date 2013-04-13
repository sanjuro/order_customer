package com.vosto.customer.products.vos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.money.Money;

import com.google.common.base.Objects;
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
    
    /**
     * From the server we get a list of variants (unique option combinations).
     * We need to transform that into a map of all possible option types -> values for this product
     * so that we can render the selection button grid.
     * @return A list of option type names.
     */
    public ArrayList<String> getOptionTypes(){
    	ArrayList<String> optionTypes = new ArrayList<String>();
    	for(VariantVo variant : this.variants){
    		 Iterator<Map.Entry<String, String>> it = variant.getOptionValueMap().entrySet().iterator();
    		    while (it.hasNext()) {
    		        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
    		        if(!optionTypes.contains(pairs.getKey().trim().toLowerCase(Locale.US))){
    		        	optionTypes.add(pairs.getKey().trim().toLowerCase(Locale.US));
    		        }
    		    }
    	}
    	Collections.sort(optionTypes);
    	return optionTypes;
    }
    
  
    /**
     * 
     * @param optionType - The name of the option type whose options we want.
     * @return An array of option value names.
     */
    public ArrayList<String> getPossibleOptionValues(String optionType){
    	ArrayList<String> optionValues = new ArrayList<String>();
    	for(VariantVo variant : this.variants){
    		String value = variant.getValueForOptionType(optionType);
    		if(value != null && !optionValues.contains(value.trim().toLowerCase(Locale.US))){
    			optionValues.add(value.trim().toLowerCase(Locale.US));
    		}
    	}
    	Collections.sort(optionValues);
    	
    	return optionValues;
    }
    
    /**
     * Takes a map of selected option values and returns the variant associated with that combination.
     * If no variant matches the combination, it returns null.
     * @param optionCombination - List of strings, each in the format "portion:full"
     * @return The VariantVo for the given combination, or null if no match
     */
    public VariantVo getVariantForOptionCombinations(ConcurrentHashMap<String, String> optionCombinations){
    	for(VariantVo variant : this.variants){
    		if(variant.matchesOptionCombination(optionCombinations)){
    			return variant;
    		}
    	}
    	
    	// No variants found matching this option value combination.
    	return null;
    }


    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("name", name).toString();
    }



}