package com.yuqian.itax.product.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.product.dao.DiscountActivityChargeStandardRelaMapper;
import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;
import com.yuqian.itax.product.service.DiscountActivityChargeStandardRelaService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("discountActivityChargeStandardRelaService")
public class DiscountActivityChargeStandardRelaServiceImpl extends BaseServiceImpl<DiscountActivityChargeStandardRelaEntity,DiscountActivityChargeStandardRelaMapper> implements DiscountActivityChargeStandardRelaService {

    /**
     * 获取特价活动开票服务费阶梯
     * @param memberId
     * @param parkId
     * @param industryId
     * @param productType
     * @param oemCode
     * @return
     */
    @Override
    public List<DiscountActivityChargeStandardRelaEntity> discountActivityInvoiceChargeStandard(Long memberId, Long parkId, Long industryId, Integer productType, String oemCode){
        return mapper.discountActivityInvoiceChargeStandard(memberId,parkId,industryId,productType,oemCode);
    }
}

