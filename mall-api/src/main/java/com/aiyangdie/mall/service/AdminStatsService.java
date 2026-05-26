package com.aiyangdie.mall.service;

import com.aiyangdie.mall.dto.AdminStatsVo;
import com.aiyangdie.mall.entity.MallOrder;
import com.aiyangdie.mall.entity.ProductSpu;
import com.aiyangdie.mall.mapper.MallOrderMapper;
import com.aiyangdie.mall.mapper.ProductSpuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final ProductSpuMapper spuMapper;
    private final MallOrderMapper orderMapper;

    public AdminStatsVo stats() {
        List<ProductSpu> all = spuMapper.selectList(null);
        List<MallOrder> orders = orderMapper.selectList(null);
        AdminStatsVo vo = new AdminStatsVo();
        vo.setProductCount(all.size());
        vo.setOnSaleCount(all.stream().filter(p -> p.getStatus() != null && p.getStatus() == 1).count());
        vo.setOrderCount(orders.size());
        vo.setPendingPayCount(orders.stream().filter(o -> o.getStatus() == 10).count());
        vo.setPendingShipCount(orders.stream().filter(o -> o.getStatus() == 20).count());
        vo.setTotalGmv(orders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() >= 20 && o.getStatus() != 50)
                .map(MallOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }
}
