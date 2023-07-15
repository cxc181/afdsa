package com.yuqian.itax.park.dao;

import com.yuqian.itax.park.entity.ParkBusinessAddressRulesEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkBusinessAddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 园区经营地址生成规则dao
 * 
 * @Date: 2020年05月18日 11:33:58 
 * @author 蒋匿
 */
@Mapper
public interface ParkBusinessAddressRulesMapper extends BaseMapper<ParkBusinessAddressRulesEntity> {

    /**
     * 根据园区id查询经营地址
     * @param parkId
     * @return
     */
    ParkBusinessAddressVO queryByParkId(@Param("parkId") Long parkId);
	
}

