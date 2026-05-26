package com.aiyangdie.mall.service;

import com.aiyangdie.mall.common.AuthContext;
import com.aiyangdie.mall.common.BusinessException;
import com.aiyangdie.mall.dto.CartAddRequest;
import com.aiyangdie.mall.dto.CartItemVo;
import com.aiyangdie.mall.entity.CartItem;
import com.aiyangdie.mall.entity.ProductSku;
import com.aiyangdie.mall.entity.ProductSpu;
import com.aiyangdie.mall.mapper.CartItemMapper;
import com.aiyangdie.mall.mapper.ProductSpuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductService productService;
    private final ProductSpuMapper spuMapper;

    public void add(CartAddRequest req) {
        Long userId = AuthContext.userId();
        ProductSku sku = productService.getSku(req.getSkuId());
        CartItem exist = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId).eq(CartItem::getSkuId, req.getSkuId()));
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + req.getQuantity());
            cartItemMapper.updateById(exist);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setSkuId(req.getSkuId());
            item.setQuantity(req.getQuantity());
            cartItemMapper.insert(item);
        }
    }

    public List<CartItemVo> list() {
        Long userId = AuthContext.userId();
        List<CartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
        List<CartItemVo> vos = new ArrayList<>();
        for (CartItem item : items) {
            ProductSku sku = productService.getSku(item.getSkuId());
            CartItemVo vo = new CartItemVo();
            vo.setId(item.getId());
            vo.setSkuId(sku.getId());
            vo.setSkuName(sku.getSkuName());
            vo.setPrice(sku.getPrice());
            vo.setQuantity(item.getQuantity());
            ProductSpu spu = spuMapper.selectById(sku.getSpuId());
            if (spu != null) vo.setCoverUrl(spu.getCoverUrl());
            vos.add(vo);
        }
        return vos;
    }

    public void remove(Long id) {
        CartItem item = cartItemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(AuthContext.userId())) {
            throw BusinessException.of(30001, "购物车项不存在");
        }
        cartItemMapper.deleteById(id);
    }

    public void clearUserCart(Long userId) {
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
    }
}
