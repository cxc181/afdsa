package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.CompanyIncomeRuleMapper;
import com.yuqian.itax.system.entity.CompanyIncomeRuleEntity;
import com.yuqian.itax.system.service.CompanyIncomeRuleService;
import org.springframework.stereotype.Service;


@Service("companyIncomeRuleService")
public class CompanyIncomeRuleServiceImpl extends BaseServiceImpl<CompanyIncomeRuleEntity,CompanyIncomeRuleMapper> implements CompanyIncomeRuleService {

    @Override
    public CompanyIncomeRuleEntity queryCompanyIncomeRuleByAmount(Long amount) {
        if (amount == 0) {
            // 使用第一级阶梯
            CompanyIncomeRuleEntity companyIncomeRuleEntity = new CompanyIncomeRuleEntity();
            companyIncomeRuleEntity.setLevel(1L);
            return this.selectOne(companyIncomeRuleEntity);
        }
        return mapper.queryCompanyIncomeRuleByAmount(amount);
    }
}

