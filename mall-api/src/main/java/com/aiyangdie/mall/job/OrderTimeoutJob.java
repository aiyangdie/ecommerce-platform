package com.aiyangdie.mall.job;

import com.aiyangdie.mall.entity.MallOrder;
import com.aiyangdie.mall.entity.OrderItem;
import com.aiyangdie.mall.entity.ProductSku;
import com.aiyangdie.mall.mapper.MallOrderMapper;
import com.aiyangdie.mall.mapper.OrderItemMapper;
import com.aiyangdie.mall.mapper.ProductSkuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutJob {

    private final MallOrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductSkuMapper skuMapper;

    /** 每 5 分钟关闭超过 30 分钟未支付的订单并回滚库存 */
    @Scheduled(fixedDelay = 300_000, initialDelay = 60_000)
    @Transactional
    public void closeExpiredOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);
        List<MallOrder> expired = orderMapper.selectList(new LambdaQueryWrapper<MallOrder>()
                .eq(MallOrder::getStatus, 10)
                .lt(MallOrder::getCreatedAt, deadline));
        for (MallOrder order : expired) {
            order.setStatus(50);
            orderMapper.updateById(order);
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            for (OrderItem item : items) {
                ProductSku sku = skuMapper.selectById(item.getSkuId());
                if (sku != null) {
                    sku.setStock(sku.getStock() + item.getQuantity());
                    skuMapper.updateById(sku);
                }
            }
            log.info("订单超时关闭: {}", order.getOrderNo());
        }
    }
}
