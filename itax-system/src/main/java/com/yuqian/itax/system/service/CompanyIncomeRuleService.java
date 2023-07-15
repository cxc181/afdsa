package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.CompanyIncomeRuleMapper;
import com.yuqian.itax.system.entity.CompanyIncomeRuleEntity;

/**
 * 企业所得税税率表service
 * 
 * @Date: 2022年09月26日 11:19:08 
 * @author 蒋匿
 */
public interface CompanyIncomeRuleService extends IBaseService<CompanyIncomeRuleEntity,CompanyIncomeRuleMapper> {

    CompanyIncomeRuleEntity queryCompanyIncomeRuleByAmount(Long amount);
}

