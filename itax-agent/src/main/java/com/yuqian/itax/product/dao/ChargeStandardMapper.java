package com.yuqian.itax.product.dao;

import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 收费标准dao
 * 
 * @Date: 2019年12月07日 20:42:23 
 * @author 蒋匿
 */
@Mapper
public interface ChargeStandardMapper extends BaseMapper<ChargeStandardEntity> {
    /**
     * @Description 获取平台收费标准
     * @Author  Kaven
     * @Date   2019/12/10 18:24
     * @Param  prodcutId
     * @param includeParkPricing 是否包含园区单独定价
     * @Return List
    */
    List<ChargeStandardEntity> getChargeStandards(@Param("productId") Long productId, @Param("parkId") Long parkId, @Param("includeParkPricing") int includeParkPricing);

    /**
     * 批量添加产品收费标准
     * @param charges
     * @param productId
     * @param addUser
     * @param addTime
     */
    void addBatch(@Param("charges") List<ChargeStandardEntity> charges, @Param("productId") Long productId,
                  @Param("addUser") String addUser, @Param("addTime") Date addTime,@Param("oemCode") String oemCode,@Param("parkProductId") Long parkProductId,@Param("parkId") Long parkId);

    /**
     * 根据产品id删除收费标准
     * @param productId
     */
    void deleteByProductId(@Param("productId") Long productId);

    /**
     * 根据园区产品定价id删除收费标准
     * @param parkProductId
     */
    void deleteByParkProductId(@Param("parkProductId") Long parkProductId);

    /**
     * 根据园区产品定价id 获取收费标准
     * @param parkProductId
     * @return
     */
    List<ChargeStandardEntity> getChargeStandardsByParkProductId(@Param("parkProductId") Long parkProductId);
}

