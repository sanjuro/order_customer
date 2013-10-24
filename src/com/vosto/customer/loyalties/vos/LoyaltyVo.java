package com.vosto.customer.loyalties.vos;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: macbookpro
 * Date: 2013/09/08
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoyaltyVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private String win_count;
    private String name;
    private String description;
    private String prize;

    public LoyaltyVo(){
    }

    public String getWinCount() {
        return win_count;
    }

    public void setWinCount(String win_count) {
        this.win_count = win_count;
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

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

}
