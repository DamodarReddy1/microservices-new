package com.programming.orderservice.dto;

public class OrderLineItemsDto {

    private Integer id;

    private String skuCode;
    private Integer quantity;
    private Integer price;
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }



    public OrderLineItemsDto(Integer id, String skuCode, Integer quantity) {
        this.id = id;
        this.skuCode = skuCode;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
