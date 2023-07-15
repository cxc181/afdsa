package com.yuqian.itax.product.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.product.dao.ProductByParkMapper;
import com.yuqian.itax.product.entity.ProductByParkEntity;
import com.yuqian.itax.product.entity.vo.ProductByParkVO;
import com.yuqian.itax.product.service.ProductByParkService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("productByParkService")
public class ProductByParkServiceImpl extends BaseServiceImpl<ProductByParkEntity,ProductByParkMapper> implements ProductByParkService {

    @Override
    public List<ProductByParkVO> getProductByParkByProductId(Long productId) {
        return mapper.getProductByParkByProductId(productId);
    }

    @Override
    public void updateIsDelete(Long id) {
        mapper.updateIsDelete(id);
    }
}

