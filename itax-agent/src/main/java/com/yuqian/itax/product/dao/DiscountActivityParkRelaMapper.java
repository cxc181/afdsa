package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.DiscountActivityParkRelaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 产品特价活动园区关系表dao
 * 
 * @Date: 2021年07月15日 15:47:07 
 * @author 蒋匿
 */
@Mapper
public interface DiscountActivityParkRelaMapper extends BaseMapper<DiscountActivityParkRelaEntity> {

    void batchAddPark(@Param("parkIds") List<Long> parkIds, Long discountActivityId, String oemCode, Date addDate, String addUser);

    /**
     * 根据活动id 删除
     * @param discountActivityId
     */
    void deleteBydiscountActivityId(Long discountActivityId);
	
}

