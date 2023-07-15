package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.BusinessIncomeRuleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经营所得适用个人所得税税率dao
 *
 * @author yejian
 * @Date: 2020年11月12日 09:14:37
 */
@Mapper
public interface BusinessIncomeRuleMapper extends BaseMapper<BusinessIncomeRuleEntity> {

    /**
     *
     */
    BusinessIncomeRuleEntity queryBusinessIncomeRuleByAmount(Long amount);

}

