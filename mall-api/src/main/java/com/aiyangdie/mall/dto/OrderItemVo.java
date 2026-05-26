package com.aiyangdie.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVo {
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
}
