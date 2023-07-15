package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkTaxRefundPolicyEntity;
import com.yuqian.itax.park.entity.vo.ParkTaxRefundPolicyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区奖励政策dao
 * 
 * @Date: 2022年09月26日 10:51:28 
 * @author 蒋匿
 */
@Mapper
public interface ParkTaxRefundPolicyMapper extends BaseMapper<ParkTaxRefundPolicyEntity> {

    /**
     * 根据园区id查询园区返税政策数据
     * @param parkId
     * @return
     */
    List<ParkTaxRefundPolicyVO> queryList(@Param("parkId") Long parkId);

    /**
     * 根据园区id删除原有的返税政策数据
     * @param parkId
     */
    void deletePolicyAll(@Param("parkId") Long parkId);

}

