package org.yearup.models.order;

import org.yearup.models.Profile;
import org.yearup.models.cart.ShoppingCart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class Order {
    /**
     * Properties of an order
     */
    private int orderId;
    private int userId;
    LocalDateTime date;
    private String address;
    private String city;
    private String state;
    private String zip;
    private BigDecimal shipping_amount; //total amount

    public Order()
    {
    }

    public Order(int orderId,
                 int userId,
                 LocalDateTime date,
                 String address,
                 String city,
                 String state,
                 String zip,
                 BigDecimal shipping_amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.shipping_amount = shipping_amount;
    }


    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public BigDecimal getShipping_amount() {
        return shipping_amount;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setShipping_amount(BigDecimal shipping_amount) {
        this.shipping_amount = shipping_amount;
    }
}
