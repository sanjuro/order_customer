package com.vosto.customer.stores.vos;

import java.io.Serializable;

import com.google.common.base.Objects;

public class DealVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String uniqueId;
    private String name;
    private String description;
    private Integer dealableId;
    private String dealableType;
    private String dealImage;
    private Boolean isActive;
    private StoreVo store;

    public DealVo(){

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

    public String getDealableType() {
        return dealableType;
    }

    public void setDealableType(String dealableType) {
        this.dealableType = dealableType;
    }

    public Integer getDealableId() {
        return dealableId;
    }

    public void setDealableId(Integer dealableId) {
        this.dealableId = dealableId;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public StoreVo getStore() {
        return store;
    }

    public void setStore(StoreVo store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name).toString();
    }
}