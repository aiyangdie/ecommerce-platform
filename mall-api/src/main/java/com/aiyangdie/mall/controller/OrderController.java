package com.aiyangdie.mall.controller;

import com.aiyangdie.mall.common.Result;
import com.aiyangdie.mall.dto.CheckoutCartRequest;
import com.aiyangdie.mall.dto.CreateOrderRequest;
import com.aiyangdie.mall.dto.OrderVo;
import com.aiyangdie.mall.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderVo> create(@Valid @RequestBody CreateOrderRequest req) {
        return Result.ok(orderService.createFromSku(req));
    }

    @PostMapping("/from-cart")
    public Result<OrderVo> fromCart(@Valid @RequestBody CheckoutCartRequest req) {
        return Result.ok(orderService.createFromCart(req));
    }

    @GetMapping
    public Result<List<OrderVo>> list() {
        return Result.ok(orderService.listMyOrders());
    }

    @PostMapping("/{orderNo}/pay")
    public Result<Void> pay(@PathVariable String orderNo) {
        orderService.pay(orderNo);
        return Result.ok(null);
    }

    @PostMapping("/{orderNo}/confirm")
    public Result<Void> confirm(@PathVariable String orderNo) {
        orderService.confirm(orderNo);
        return Result.ok(null);
    }
}
