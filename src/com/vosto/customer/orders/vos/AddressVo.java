package com.vosto.customer.orders.vos;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String address1;
    private String address2;
	private Integer suburb_id;
	private String city;
	private String zipcode;
	private String country;
	private Float latitude;
	private Float longitude;
	
	public AddressVo(){
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Integer getSuburb_id() {
		return suburb_id;
	}

	public void setSuburb_id(Integer suburb_id) {
		this.suburb_id = suburb_id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public float getLatitude() {
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
	
	public String toJson(){
		JSONObject address = new JSONObject();
		try{
			address.put("address1", this.address1);
			address.put("address2", this.address2);
			address.put("suburb_id", this.suburb_id);
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
	
}