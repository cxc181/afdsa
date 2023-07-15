package com.yuqian.itax.product.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.dao.ProductByParkMapper;
import com.yuqian.itax.product.entity.ProductByParkEntity;
import com.yuqian.itax.product.entity.vo.ProductByParkVO;

import java.util.List;

/**
 * 产品园区定价service
 * 
 * @Date: 2022年04月11日 10:57:21 
 * @author 蒋匿
 */
public interface ProductByParkService extends IBaseService<ProductByParkEntity,ProductByParkMapper> {

    /**
     * 根据产品id获取产品园区单独定价信息
     * @param productId
     * @return
     */
    List<ProductByParkVO> getProductByParkByProductId(Long productId);

    /**
     * 逻辑删除
     * @param id
     */
    void updateIsDelete(Long id);
	
}

