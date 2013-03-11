package com.vosto.customer.services.vos;

import java.io.Serializable;

public class OptionValueVo implements Serializable {
	
	
	/*
	 * "option_value": {
                        "id": 1,
                        "name": "green noodle",
                        "option_type_id": 1,
                        "position": 10,
                        "presentation": "green noodles"
                    }
	 */
	
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String presentation;
	private int position;
	private int optionTypeId;
	
	
	public OptionValueVo(){
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPresentation() {
		return presentation;
	}


	public void setPresentation(String presentation) {
		this.presentation = presentation;
	}


	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}


	public int getOptionTypeId() {
		return optionTypeId;
	}


	public void setOptionTypeId(int optionTypeId) {
		this.optionTypeId = optionTypeId;
	}


	
	
}