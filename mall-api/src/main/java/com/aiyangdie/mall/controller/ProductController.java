package com.aiyangdie.mall.controller;

import com.aiyangdie.mall.common.Result;
import com.aiyangdie.mall.dto.ProductVo;
import com.aiyangdie.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "上架商品列表")
    @GetMapping
    public Result<List<ProductVo>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(productService.listOnSale(keyword));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductVo> detail(@PathVariable Long id) {
        return Result.ok(productService.detail(id));
    }
}
