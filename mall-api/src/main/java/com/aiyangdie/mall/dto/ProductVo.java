package com.aiyangdie.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVo {
    private Long id;
    private String title;
    private String subTitle;
    private String coverUrl;
    private Integer status;
    private BigDecimal minPrice;
    private Long defaultSkuId;
    private List<SkuVo> skus;
}
