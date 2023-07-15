package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.CompanyIncomeRuleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业所得税税率表dao
 * 
 * @Date: 2022年09月26日 11:19:08 
 * @author 蒋匿
 */
@Mapper
public interface CompanyIncomeRuleMapper extends BaseMapper<CompanyIncomeRuleEntity> {

    CompanyIncomeRuleEntity queryCompanyIncomeRuleByAmount(Long amount);
}

