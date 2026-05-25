package com.aiyangdie.mall.controller;

import com.aiyangdie.mall.common.Result;
import com.aiyangdie.mall.entity.ProductSpu;
import com.aiyangdie.mall.mapper.ProductSpuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSpuMapper productSpuMapper;

    @Operation(summary = "商品列表（上架）")
    @GetMapping
    public Result<List<ProductSpu>> list() {
        List<ProductSpu> list = productSpuMapper.selectList(
                new LambdaQueryWrapper<ProductSpu>().eq(ProductSpu::getStatus, 1)
        );
        return Result.ok(list);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductSpu> detail(@PathVariable Long id) {
        ProductSpu spu = productSpuMapper.selectById(id);
        if (spu == null) {
            return Result.fail(20001, "商品不存在");
        }
        return Result.ok(spu);
    }
}
