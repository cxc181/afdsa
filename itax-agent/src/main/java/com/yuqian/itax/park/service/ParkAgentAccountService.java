package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.entity.ParkAgentAccountEntity;
import com.yuqian.itax.park.dao.ParkAgentAccountMapper;
import com.yuqian.itax.park.entity.query.ParkAgentAccountQuery;
import com.yuqian.itax.park.entity.vo.ParkAgentAccountPO;

import java.util.List;

/**
 * 园区经办人账号表service
 * 
 * @Date: 2020年03月04日 09:44:53 
 * @author 蒋匿
 */
public interface ParkAgentAccountService extends IBaseService<ParkAgentAccountEntity,ParkAgentAccountMapper> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    PageInfo<ParkAgentAccountEntity> queryParkAgentAccount(ParkAgentAccountQuery query);

    List<ParkAgentAccountEntity> queryParkAgentAccountList(ParkAgentAccountQuery query);

    /**
     * 状态变更
     */
    void updateParkAgentAccountStatus(Long id ,Integer status,String account);
    /**
     * 新增
     */
    ParkAgentAccountEntity addParkAgentAccount(ParkAgentAccountPO po,String account);

    /**
     * 查询园区经办人信息
     * @param parkId
     * @param agentAccount
     * @param status
     */
    ParkAgentAccountEntity queryParkAgentAccountByAgentAccount(Long parkId, String agentAccount, Integer status);
}

