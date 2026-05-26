package com.aiyangdie.mall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVo {
    private Long id;
    private String orderNo;
    private Integer status;
    private BigDecimal payAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddr;
    private String createdAt;
    private List<OrderItemVo> items;
}
