package com.vosto.customer.stores.vos;

import java.io.Serializable;

import com.google.common.base.Objects;

public class StoreVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String uniqueId;
    private String name;
    private String description;
    private String address;
    private String managerName;
    private String managerContact;
    private String email;
    private String telephone;
    private String url;
    private String store_image;
    private Boolean is_online;
    private boolean can_deliver;
    private double distance;

    public StoreVo(){
        this.distance = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerContact() {
        return managerContact;
    }

    public void setManagerContact(String managerContact) {
        this.managerContact = managerContact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStoreImage() {
        return store_image;
    }

    public void setStoreImage(String store_image) {
        this.store_image = store_image;
    }

    public Boolean getIsOnline() {
        return is_online;
    }

    public void setIsOnline(Boolean is_online) {
        this.is_online = is_online;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean canDeliver() {
		return can_deliver;
	}

	public void setCanDeliver(boolean can_deliver) {
		this.can_deliver = can_deliver;
	}

	@Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name).toString();
    }
}