package com.sxps.xsps.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String cover;
    private BigDecimal price;
    private Integer quantity;
}
