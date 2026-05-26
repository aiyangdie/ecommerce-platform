package com.aiyangdie.mall.controller;

import com.aiyangdie.mall.common.Result;
import com.aiyangdie.mall.dto.CartAddRequest;
import com.aiyangdie.mall.dto.CartItemVo;
import com.aiyangdie.mall.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public Result<List<CartItemVo>> list() {
        return Result.ok(cartService.list());
    }

    @PostMapping("/items")
    public Result<Void> add(@Valid @RequestBody CartAddRequest req) {
        cartService.add(req);
        return Result.ok(null);
    }

    @DeleteMapping("/items/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(id);
        return Result.ok(null);
    }
}
