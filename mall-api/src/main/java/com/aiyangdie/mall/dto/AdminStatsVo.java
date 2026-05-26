package com.aiyangdie.mall.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminStatsVo {
    private long productCount;
    private long onSaleCount;
    private long orderCount;
    private long pendingPayCount;
    private long pendingShipCount;
    private BigDecimal totalGmv;
}
