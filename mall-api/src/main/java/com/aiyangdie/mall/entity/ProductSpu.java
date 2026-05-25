package com.aiyangdie.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_spu")
public class ProductSpu {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String subTitle;
    private String coverUrl;
    private Long categoryId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
