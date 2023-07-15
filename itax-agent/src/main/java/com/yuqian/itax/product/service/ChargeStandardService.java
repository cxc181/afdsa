package com.yuqian.itax.product.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.product.dao.ChargeStandardMapper;

import java.util.List;
import java.util.Map;

/**
 * 收费标准service
 * 
 * @Date: 2019年12月07日 20:42:23 
 * @author 蒋匿
 */
public interface ChargeStandardService extends IBaseService<ChargeStandardEntity,ChargeStandardMapper> {
    /**
     * @Description 根据园区的政策，显示不同的收费标准
     * @Author  Kaven
     * @Date   2019/12/10 17:22
     * @Param  prodcutId
     * @param includeParkPricing 包含园区单独定价 0-否 1-是
     * @Return List
    */
    List<ChargeStandardEntity> getChargeStandards(Long prodcutId, Long parkId, int includeParkPricing) throws BusinessException;

    /**
     * @Description 查询收费标准
     * @Author  Kaven
     * @Date   2020/1/13 15:37
     * @Param  productId
     * @Return map
     * @Exception BusinessException
    */
    Map<String, Object> queryChargeStandards(Long productId, Long parkId, Long memberId, Long industryId) throws BusinessException;

    /**
     * 根据园区产品定价id 获取收费标准
     * @param parkProductId
     * @return
     */
    List<ChargeStandardEntity> getChargeStandardsByParkProductId(Long parkProductId);
}

