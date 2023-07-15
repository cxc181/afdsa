package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.RatifyTaxMapper;
import com.yuqian.itax.system.entity.RatifyTaxEntity;
import com.yuqian.itax.system.service.RatifyTaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("ratifyTaxService")
public class RatifyTaxServiceImpl extends BaseServiceImpl<RatifyTaxEntity,RatifyTaxMapper> implements RatifyTaxService {
    @Resource
    private RatifyTaxMapper ratifyTaxMapper;

    @Override
    public List<RatifyTaxEntity> listRatifyTax(Long industryId) {
        return this.ratifyTaxMapper.listRatifyTax(industryId);
    }

    @Override
    public void delByIndustryId(Long industryId) {
        mapper.delByIndustryId(industryId);
    }
}

