package com.aiyangdie.mall.service;

import com.aiyangdie.mall.common.AuthContext;
import com.aiyangdie.mall.common.BusinessException;
import com.aiyangdie.mall.dto.*;
import com.aiyangdie.mall.entity.*;
import com.aiyangdie.mall.mapper.MallOrderMapper;
import com.aiyangdie.mall.mapper.OrderItemMapper;
import com.aiyangdie.mall.mapper.ProductSkuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MallOrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductSkuMapper skuMapper;
    private final ProductService productService;
    private final CartService cartService;

    @Transactional
    public OrderVo createFromSku(CreateOrderRequest req) {
        ProductSku sku = productService.getSku(req.getSkuId());
        deductStock(sku, req.getQuantity());
        return createOrder(List.of(line(sku, req.getQuantity())), req.getReceiverName(), req.getReceiverPhone(), req.getReceiverAddr());
    }

    @Transactional
    public OrderVo createFromCart(CheckoutCartRequest req) {
        List<CartItemVo> cart = cartService.list();
        if (cart.isEmpty()) throw BusinessException.of(30002, "购物车为空");
        List<OrderLine> lines = new ArrayList<>();
        for (CartItemVo c : cart) {
            ProductSku sku = productService.getSku(c.getSkuId());
            deductStock(sku, c.getQuantity());
            lines.add(line(sku, c.getQuantity()));
        }
        OrderVo vo = createOrder(lines, req.getReceiverName(), req.getReceiverPhone(), req.getReceiverAddr());
        cartService.clearUserCart(AuthContext.userId());
        return vo;
    }

    public List<OrderVo> listMyOrders() {
        return orderMapper.selectList(new LambdaQueryWrapper<MallOrder>()
                        .eq(MallOrder::getUserId, AuthContext.userId())
                        .orderByDesc(MallOrder::getId))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    public List<OrderVo> listAll() {
        return orderMapper.selectList(new LambdaQueryWrapper<MallOrder>().orderByDesc(MallOrder::getId))
                .stream().map(this::toVo).collect(Collectors.toList());
    }

    @Transactional
    public void pay(String orderNo) {
        MallOrder order = getOwnedOrAdmin(orderNo);
        if (order.getStatus() != 10) throw BusinessException.of(30003, "订单状态不可支付");
        order.setStatus(20);
        orderMapper.updateById(order);
    }

    @Transactional
    public void ship(String orderNo) {
        MallOrder order = requireOrder(orderNo);
        if (order.getStatus() != 20) throw BusinessException.of(30004, "仅待发货订单可发货");
        order.setStatus(30);
        orderMapper.updateById(order);
    }

    @Transactional
    public void confirm(String orderNo) {
        MallOrder order = getOwnedOrAdmin(orderNo);
        if (order.getStatus() != 30) throw BusinessException.of(30005, "订单状态不可确认收货");
        order.setStatus(40);
        orderMapper.updateById(order);
    }

    private OrderVo createOrder(List<OrderLine> lines, String name, String phone, String addr) {
        BigDecimal total = lines.stream()
                .map(l -> l.sku.getPrice().multiply(BigDecimal.valueOf(l.qty)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        MallOrder order = new MallOrder();
        order.setOrderNo(genOrderNo());
        order.setUserId(AuthContext.userId());
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setStatus(10);
        order.setReceiverName(name);
        order.setReceiverPhone(phone);
        order.setReceiverAddr(addr);
        orderMapper.insert(order);
        for (OrderLine l : lines) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
            item.setSpuId(l.sku.getSpuId());
            item.setSkuId(l.sku.getId());
            item.setSkuName(l.sku.getSkuName());
            item.setPrice(l.sku.getPrice());
            item.setQuantity(l.qty);
            orderItemMapper.insert(item);
        }
        return toVo(order);
    }

    private void deductStock(ProductSku sku, int qty) {
        if (sku.getStock() < qty) throw BusinessException.of(20003, "库存不足");
        sku.setStock(sku.getStock() - qty);
        int rows = skuMapper.updateById(sku);
        if (rows == 0) throw BusinessException.of(20004, "库存扣减失败，请重试");
    }

    private OrderLine line(ProductSku sku, int qty) {
        return new OrderLine(sku, qty);
    }

    private String genOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return ts + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    private MallOrder requireOrder(String orderNo) {
        MallOrder order = orderMapper.selectOne(new LambdaQueryWrapper<MallOrder>().eq(MallOrder::getOrderNo, orderNo));
        if (order == null) throw BusinessException.of(30006, "订单不存在");
        return order;
    }

    private MallOrder getOwnedOrAdmin(String orderNo) {
        MallOrder order = requireOrder(orderNo);
        AuthContext.LoginUser u = AuthContext.get();
        if ("ADMIN".equals(u.role()) || order.getUserId().equals(u.id())) return order;
        throw BusinessException.of(40300, "无权操作此订单");
    }

    private OrderVo toVo(MallOrder order) {
        OrderVo vo = new OrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setPayAmount(order.getPayAmount());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddr(order.getReceiverAddr());
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items.stream().map(i -> {
            OrderItemVo iv = new OrderItemVo();
            iv.setSkuName(i.getSkuName());
            iv.setPrice(i.getPrice());
            iv.setQuantity(i.getQuantity());
            return iv;
        }).collect(Collectors.toList()));
        return vo;
    }

    private record OrderLine(ProductSku sku, int qty) {
    }
}
