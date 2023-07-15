package com.yuqian.itax.product.dao;

import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 产品与园区的关系dao
 * 
 * @Date: 2019年12月07日 20:43:06 
 * @author 蒋匿
 */
@Mapper
public interface ProductParkRelaMapper extends BaseMapper<ProductParkRelaEntity> {

    /**
     * 批量添加园区产品关系
     * @param parkIds
     * @param productId
     * @param addUser
     * @param addTime
     */
    void addBatch(@Param("parkIds") List<Long> parkIds, @Param("productId") Long productId,
                  @Param("addUser") String addUser, @Param("addTime") Date addTime);

    /**
     * 根据产品id删除收费标准
     * @param productId
     */
    void deleteByProductId(@Param("productId") Long productId);
}

