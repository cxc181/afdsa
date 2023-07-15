package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.ClassificationCodeVatMapper;

import com.yuqian.itax.system.entity.ClassificationCodeVatEntity;

import java.math.BigDecimal;
import java.util.List;


/**
 * 基础开票类目库增值税率service
 *
 * @Date: 2022年08月19日 11:38:14
 * @author cxz
 */
public interface ClassificationCodeVatService extends IBaseService<ClassificationCodeVatEntity, ClassificationCodeVatMapper> {

    /**
     * 根据企业来票类目查询增值税率
     * @param companyCategoryId
     * @return
     */
    List<BigDecimal> queryVatRateByCompanyCategoryId(Long companyCategoryId);
}
