package com.vosto.customer.loyalties.vos;

import com.vosto.customer.loyalties.vos.LoyaltyVo;
import com.vosto.customer.stores.vos.StoreVo;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/09/08
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoyaltyCardVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Boolean is_won;
    private String count;
    private Date createdAt;
    private Date updatedAt;
    private LoyaltyVo loyalty;
    private StoreVo[] storeItems;

    public LoyaltyCardVo(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getIsWon() {
        return is_won;
    }

    public void setIsWon(Boolean is_won) {
        this.is_won = is_won;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LoyaltyVo getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(LoyaltyVo loyalty) {
        this.loyalty = loyalty;
    }

    public StoreVo[] getStoreItems() {
        return storeItems;
    }

    public void setStoreItems(StoreVo[] storeItems) {
        this.storeItems = storeItems;
    }
}
