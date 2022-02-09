package com.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderDto {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String OrderId;
    private String userId;
}
