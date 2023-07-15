package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.ProductByParkEntity;
import com.yuqian.itax.product.entity.vo.ProductByParkVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品园区定价dao
 * 
 * @Date: 2022年04月11日 10:57:21 
 * @author 蒋匿
 */
@Mapper
public interface ProductByParkMapper extends BaseMapper<ProductByParkEntity> {

    /**
     * 根据产品id获取产品园区单独定价信息
     * @param productId
     * @return
     */
    List<ProductByParkVO> getProductByParkByProductId(@Param("productId") Long productId);


    /**
     * 逻辑删除
     * @param id
     */
    void updateIsDelete(@Param("id") Long id);
}

