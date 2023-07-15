package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.DiscountActivityCrowdLabelRelaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 产品特价活动人群关系表dao
 * 
 * @Date: 2021年07月15日 15:46:59 
 * @author 蒋匿
 */
@Mapper
public interface DiscountActivityCrowdLabelRelaMapper extends BaseMapper<DiscountActivityCrowdLabelRelaEntity> {

    /**
     * 添加人群id
     *
     * @param crowdLabelIds
     * @param discountActivityId
     */
    void batchAdd(@Param("crowdLabelIds") List<Long> crowdLabelIds, Long discountActivityId, String oemCode, Date addDate, String addUser);

    /**
     * 根据活动id 删除
     * @param discountActivityId
     */
    void deleteBydiscountActivityId(Long discountActivityId);
}

