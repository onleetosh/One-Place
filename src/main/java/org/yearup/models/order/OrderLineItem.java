package org.yearup.models.order;

import java.math.BigDecimal;

public class OrderLineItem {

    /**
     * Properties of a Order Line Item
     */
    private int orderLineId;
    private int orderId;
    private int productId;
    private BigDecimal salesPrice;
    private int quantity;
    private double discount;

    public OrderLineItem()
    {
    }

    public OrderLineItem(int orderLineId,
                         int orderId,
                         int productId,
                         BigDecimal salesPrice,
                         int quantity,
                         double discount) {
        this.orderLineId = orderLineId;
        this.orderId = orderId;
        this.productId = productId;
        this.salesPrice = salesPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public int getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

}
