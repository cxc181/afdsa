package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.ClassificationCodeVatMapper;
import com.yuqian.itax.system.entity.ClassificationCodeVatEntity;
import com.yuqian.itax.system.service.ClassificationCodeVatService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("classificationCodeVatService")
public class ClassificationCodeVatServiceImpl extends BaseServiceImpl<ClassificationCodeVatEntity, ClassificationCodeVatMapper> implements ClassificationCodeVatService {

    @Override
    public List<BigDecimal> queryVatRateByCompanyCategoryId(Long companyCategoryId) {
        return mapper.queryVatRateByCompanyCategoryId(companyCategoryId);
    }
}
