package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.RatifyTaxEntity;
import com.yuqian.itax.system.dao.RatifyTaxMapper;

import java.util.List;

/**
 * 核定税种service
 * 
 * @Date: 2019年12月08日 20:38:10 
 * @author 蒋匿
 */
public interface RatifyTaxService extends IBaseService<RatifyTaxEntity,RatifyTaxMapper> {
    /**
     * @Description 根据行业ID获取核定税种列表
     * @Author  Kaven
     * @Date   2019/12/11 18:03
     * @Param  industryId
     * @Return List
    */
    List<RatifyTaxEntity> listRatifyTax(Long industryId);

    /**
     * 根据行业id删除核定税种
     * @param industryId
     */
    void delByIndustryId(Long industryId);
}

