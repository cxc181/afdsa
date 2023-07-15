package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.BusinessIncomeRuleMapper;
import com.yuqian.itax.system.entity.BusinessIncomeRuleEntity;

/**
 * 经营所得适用个人所得税税率service
 *
 * @author yejian
 * @Date: 2020年11月12日 09:14:37
 */
public interface BusinessIncomeRuleService extends IBaseService<BusinessIncomeRuleEntity, BusinessIncomeRuleMapper> {

    /**
     *
     */
    BusinessIncomeRuleEntity queryBusinessIncomeRuleByAmount(Long amount);
}

