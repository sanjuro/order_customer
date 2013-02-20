package com.vosto.customer.services.vos;

public class TaxonVo {
	
	private int id;
	private int parent_id;
	private int position;
	private String name;
	
	
	
	public TaxonVo(){
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getParentId() {
		return parent_id;
	}


	public void setParentId(int parent_id) {
		this.parent_id = parent_id;
	}


	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	

	
}