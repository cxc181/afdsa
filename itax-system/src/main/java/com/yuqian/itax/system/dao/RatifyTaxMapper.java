package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.RatifyTaxEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 核定税种dao
 * 
 * @Date: 2019年12月08日 20:38:10 
 * @author 蒋匿
 */
@Mapper
public interface RatifyTaxMapper extends BaseMapper<RatifyTaxEntity> {
    /**
     * @Description 根据行业ID获取核定税种列表
     * @Author  Kaven
     * @Date   2019/12/11 18:04
     * @Param  industryId
     * @Return List
    */
    List<RatifyTaxEntity> listRatifyTax(Long industryId);

    /**
     * 根据行业id删除核定税种
     * @param industryId
     */
    void delByIndustryId(@Param("industryId") Long industryId);
}

