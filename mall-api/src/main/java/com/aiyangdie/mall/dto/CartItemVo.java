package com.aiyangdie.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVo {
    private Long id;
    private Long skuId;
    private String skuName;
    private BigDecimal price;
    private Integer quantity;
    private String coverUrl;
}
