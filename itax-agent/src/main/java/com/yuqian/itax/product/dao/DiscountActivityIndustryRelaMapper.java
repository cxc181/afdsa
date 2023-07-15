package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.DiscountActivityIndustryRelaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 产品特价活动行业关系表dao
 * 
 * @Date: 2021年07月15日 15:47:17 
 * @author 蒋匿
 */
@Mapper
public interface DiscountActivityIndustryRelaMapper extends BaseMapper<DiscountActivityIndustryRelaEntity> {

    void batchAddIndusty(@Param("industryIds") List<Long> industryIds, Long discountActivityId, String oemCode, Date addDate, String addUser);


    /**
     * 根据活动id 删除
     * @param discountActivityId
     */
    void deleteBydiscountActivityId(Long discountActivityId);
	
}

