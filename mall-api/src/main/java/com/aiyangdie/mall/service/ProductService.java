package com.aiyangdie.mall.service;

import com.aiyangdie.mall.common.BusinessException;
import com.aiyangdie.mall.dto.ProductVo;
import com.aiyangdie.mall.dto.SkuVo;
import com.aiyangdie.mall.entity.ProductSku;
import com.aiyangdie.mall.entity.ProductSpu;
import com.aiyangdie.mall.mapper.ProductSkuMapper;
import com.aiyangdie.mall.mapper.ProductSpuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductSpuMapper spuMapper;
    private final ProductSkuMapper skuMapper;

    public List<ProductVo> listOnSale() {
        List<ProductSpu> spus = spuMapper.selectList(new LambdaQueryWrapper<ProductSpu>().eq(ProductSpu::getStatus, 1));
        return spus.stream().map(this::toVo).collect(Collectors.toList());
    }

    public List<ProductVo> listAll() {
        return spuMapper.selectList(null).stream().map(this::toVo).collect(Collectors.toList());
    }

    public ProductVo detail(Long id) {
        ProductSpu spu = spuMapper.selectById(id);
        if (spu == null) {
            throw BusinessException.of(20001, "商品不存在");
        }
        return toVo(spu);
    }

    public ProductVo saveSpu(ProductSpu spu, List<ProductSku> skus) {
        if (spu.getId() == null) {
            spuMapper.insert(spu);
        } else {
            spuMapper.updateById(spu);
        }
        if (skus != null) {
            skuMapper.delete(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getSpuId, spu.getId()));
            for (ProductSku sku : skus) {
                sku.setSpuId(spu.getId());
                sku.setVersion(0);
                skuMapper.insert(sku);
            }
        }
        return toVo(spuMapper.selectById(spu.getId()));
    }

    public void updateStatus(Long id, int status) {
        ProductSpu spu = spuMapper.selectById(id);
        if (spu == null) throw BusinessException.of(20001, "商品不存在");
        spu.setStatus(status);
        spuMapper.updateById(spu);
    }

    private ProductVo toVo(ProductSpu spu) {
        ProductVo vo = new ProductVo();
        BeanUtils.copyProperties(spu, vo);
        List<ProductSku> skus = skuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getSpuId, spu.getId()));
        List<SkuVo> skuVos = skus.stream().map(s -> {
            SkuVo sv = new SkuVo();
            BeanUtils.copyProperties(s, sv);
            return sv;
        }).collect(Collectors.toList());
        vo.setSkus(skuVos);
        if (!skus.isEmpty()) {
            vo.setDefaultSkuId(skus.get(0).getId());
            vo.setMinPrice(skus.stream().map(ProductSku::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        }
        return vo;
    }

    public ProductSku getSku(Long skuId) {
        ProductSku sku = skuMapper.selectById(skuId);
        if (sku == null) throw BusinessException.of(20002, "SKU不存在");
        return sku;
    }
}
