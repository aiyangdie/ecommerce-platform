package com.aiyangdie.mall.controller;

import com.aiyangdie.mall.common.Result;
import com.aiyangdie.mall.dto.OrderVo;
import com.aiyangdie.mall.dto.ProductVo;
import com.aiyangdie.mall.entity.ProductSku;
import com.aiyangdie.mall.entity.ProductSpu;
import com.aiyangdie.mall.service.OrderService;
import com.aiyangdie.mall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/products")
    public Result<List<ProductVo>> products() {
        return Result.ok(productService.listAll());
    }

    @PostMapping("/products")
    public Result<ProductVo> saveProduct(@RequestBody SaveProductRequest req) {
        ProductSpu spu = new ProductSpu();
        spu.setId(req.getId());
        spu.setTitle(req.getTitle());
        spu.setSubTitle(req.getSubTitle());
        spu.setCoverUrl(req.getCoverUrl());
        spu.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        List<ProductSku> skus = req.getSkus().stream().map(s -> {
            ProductSku sku = new ProductSku();
            sku.setSkuName(s.getSkuName());
            sku.setPrice(s.getPrice());
            sku.setStock(s.getStock());
            return sku;
        }).toList();
        return Result.ok(productService.saveSpu(spu, skus));
    }

    @PutMapping("/products/{id}/status")
    public Result<Void> status(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateStatus(id, body.getOrDefault("status", 1));
        return Result.ok(null);
    }

    @GetMapping("/orders")
    public Result<List<OrderVo>> orders() {
        return Result.ok(orderService.listAll());
    }

    @PostMapping("/orders/{orderNo}/ship")
    public Result<Void> ship(@PathVariable String orderNo) {
        orderService.ship(orderNo);
        return Result.ok(null);
    }

    @Data
    public static class SaveProductRequest {
        private Long id;
        private String title;
        private String subTitle;
        private String coverUrl;
        private Integer status;
        private List<SkuInput> skus;
    }

    @Data
    public static class SkuInput {
        private String skuName;
        private BigDecimal price;
        private Integer stock;
    }
}
