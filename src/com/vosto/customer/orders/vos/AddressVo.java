package com.vosto.customer.orders.vos;

import java.io.Serializable;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AddressVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String address1;
    private String address2;
	private Integer suburb_id;
	private String suburb;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	private Float latitude;
	private Float longitude;
	
	public AddressVo(){
		this.country = "South Africa";
	}
	
	public AddressVo(String addressJson){  
       try{
    	   JSONObject jsonObj = new JSONObject(addressJson);
    	   this.setAddress1(jsonObj.getString("address1"));
    	   if(!jsonObj.isNull("address2")){
    		   this.setAddress2(jsonObj.getString("address2"));
    	   }
    	   this.setSuburb(jsonObj.getString("suburb"));
    	   this.setSuburb_id(jsonObj.getInt("suburb_id"));
    	   
    	   if(!jsonObj.isNull("city")){
    		   this.setCity(jsonObj.getString("city"));
    	   }
    	   if(!jsonObj.isNull("state")){
    		   this.setState(jsonObj.getString("state"));
    	   }
    	   if(!jsonObj.isNull("country")){
    		   this.setCountry(jsonObj.getString("country"));
    	   }
    	   if(!jsonObj.isNull("zipcode")){
    		   this.setZipcode(jsonObj.getString("zipcode"));
    	   }
    	   Log.d("ADR", "AddressVO constructed from json!");
       }catch(JSONException e){
    	   e.printStackTrace();
       }
	}
	

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		if(address1 != null && address1.equalsIgnoreCase("null")){
			this.address1 = null;
			return;
		}
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		if(address2 != null && address2.equalsIgnoreCase("null")){
			this.address2 = null;
			return;
		}
		this.address2 = address2;
	}

	public Integer getSuburb_id() {
		return suburb_id;
	}

	public void setSuburb_id(Integer suburb_id) {
		this.suburb_id = suburb_id;
	}
	

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		if(suburb != null && suburb.equalsIgnoreCase("null")){
			this.suburb = null;
			return;
		}
		this.suburb = suburb;
	}

	public String getCity() {
		
		return city;
	}

	public void setCity(String city) {
		if(city != null && city.equalsIgnoreCase("null")){
			this.city = null;
			return;
		}
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		if(zipcode != null && zipcode.equalsIgnoreCase("null")){
			this.zipcode = null;
			return;
		}
		this.zipcode = zipcode;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		if(state != null && state.equalsIgnoreCase("null")){
			this.state = null;
			return;
		}
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		if(country != null && country.equalsIgnoreCase("null")){
			this.country = null;
			return;
		}
		this.country = country;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}


	public boolean isEmpty(){
		return this.getAddress1() == null || this.getAddress1().trim().equals("");
	}
	
	public boolean equals(AddressVo otherAddress){
		if(otherAddress == null){
			return false;
		}
		
		if(!this.getAddress1().toLowerCase(Locale.getDefault()).trim().equals(otherAddress.getAddress1().toLowerCase(Locale.getDefault()).trim())){
			return false;
		}
		
		if(!this.getAddress2().toLowerCase(Locale.getDefault()).trim().equals(otherAddress.getAddress2().toLowerCase(Locale.getDefault()).trim())){
			return false;
		}
		
		if(this.getSuburb_id().intValue() != otherAddress.getSuburb_id().intValue()){
			return false;
		}
		
		if(!this.getCity().toLowerCase(Locale.getDefault()).trim().equals(otherAddress.getCity().toLowerCase(Locale.getDefault()).trim())){
			return false;
		}
		
		if(!this.getZipcode().toLowerCase(Locale.getDefault()).trim().equals(otherAddress.getZipcode().toLowerCase(Locale.getDefault()).trim())){
			return false;
		}
		
		if(!this.getCountry().toLowerCase(Locale.getDefault()).trim().equals(otherAddress.getCountry().toLowerCase(Locale.getDefault()).trim())){
			return false;
		}
		
		if(this.getLatitude() != otherAddress.getLatitude()){
			return false;
		}
		
		if(this.getLongitude() != otherAddress.getLongitude()){
			return false;
		}
		
		return true;
	}
	
	public String toJson(){
		return this.toJson(true);
	}
	
	public String toJson(boolean includeSuburbName){
		JSONObject address = new JSONObject();
		try{
			address.put("address1", this.address1);
			address.put("address2", this.address2);
			address.put("suburb_id", this.suburb_id);
			if(includeSuburbName){
				address.put("suburb", this.suburb);
			}
			address.put("city", this.city);
			address.put("zipcode", this.zipcode);
			address.put("country", this.country);
			address.put("latitude", this.latitude);
			address.put("longitude", this.longitude);
			return address.toString();
		}catch(JSONException e){
			e.printStackTrace();
			return "";
		}
	}
	
	public String toString(){
		String address = getAddress1();
		if(getAddress2() != null && !getAddress2().trim().equals("")){
			address += ", " + getAddress2();
		}
		
		if(getSuburb() != null && !getSuburb().trim().equals("")){
			address += ", " + getSuburb();
		}
		
		if(getCity() != null && !getCity().trim().equals("")){
			address += ", " + getCity();
		}
		
		if(getZipcode() != null && !getZipcode().trim().equals("")){
			address += ", " + getZipcode();
		}
		
		return address;
		
	}
	
}