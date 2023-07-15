package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.BusinessIncomeRuleMapper;
import com.yuqian.itax.system.entity.BusinessIncomeRuleEntity;
import com.yuqian.itax.system.service.BusinessIncomeRuleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * 经营所得适用个人所得税税率service实现
 *
 * @author yejian
 * @Date: 2020年11月12日 09:14:37
 */
@Service("businessIncomeRuleService")
public class BusinessIncomeRuleServiceImpl extends BaseServiceImpl<BusinessIncomeRuleEntity, BusinessIncomeRuleMapper> implements BusinessIncomeRuleService {

    @Override
    public BusinessIncomeRuleEntity queryBusinessIncomeRuleByAmount(Long amount) {
        if (amount == 0L) {
            // 使用第一级阶梯
            BusinessIncomeRuleEntity businessIncomeRuleEntity = new BusinessIncomeRuleEntity();
            businessIncomeRuleEntity.setLevel(1L);
            return this.selectOne(businessIncomeRuleEntity);
        }
        return mapper.queryBusinessIncomeRuleByAmount(amount);
    }
}

