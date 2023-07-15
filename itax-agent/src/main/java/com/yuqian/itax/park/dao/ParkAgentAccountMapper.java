package com.yuqian.itax.park.dao;

import com.yuqian.itax.park.entity.ParkAgentAccountEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.query.ParkAgentAccountQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区经办人账号表dao
 * 
 * @Date: 2020年03月04日 09:44:53 
 * @author 蒋匿
 */
@Mapper
public interface ParkAgentAccountMapper extends BaseMapper<ParkAgentAccountEntity> {

    List<ParkAgentAccountEntity> queryParkAgentAccount(ParkAgentAccountQuery query);

    /**
     * 查询园区经办人信息
     * @param parkId
     * @param agentAccount
     * @param status
     */
    ParkAgentAccountEntity queryParkAgentAccountByAgentAccount(@Param("parkId")Long parkId, @Param("agentAccount")String agentAccount, @Param("status")Integer status);
}

