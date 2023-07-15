package com.yuqian.itax.product.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.dao.DiscountActivityChargeStandardRelaMapper;
import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;

import java.util.List;

/**
 * 特价活动开票服务费标准service
 * 
 * @Date: 2021年07月15日 15:47:32 
 * @author 蒋匿
 */
public interface DiscountActivityChargeStandardRelaService extends IBaseService<DiscountActivityChargeStandardRelaEntity,DiscountActivityChargeStandardRelaMapper> {

    /**
     * 获取特价活动开票服务费阶梯
     * @param memberId
     * @param parkId
     * @param industryId
     * @param productType
     * @param oemCode
     * @return
     */
    List<DiscountActivityChargeStandardRelaEntity> discountActivityInvoiceChargeStandard(Long memberId, Long parkId, Long industryId, Integer productType, String oemCode);
}

