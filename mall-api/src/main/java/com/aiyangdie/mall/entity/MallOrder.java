package com.aiyangdie.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mall_order")
public class MallOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    /** 10待支付 20待发货 30待收货 40已完成 50已取消 */
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddr;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
