package com.redis.learning.stream;

public class Orders {
    private String orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private Double price;

    public Orders(String orderId, String userId, String productId, Integer quantity, Double price) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }
}
