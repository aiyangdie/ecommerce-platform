package com.aiyangdie.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuVo {
    private Long id;
    private Long spuId;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
}
