package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.park.dao.ParkTaxRefundPolicyMapper;
import com.yuqian.itax.park.entity.ParkTaxRefundPolicyEntity;
import com.yuqian.itax.park.entity.ParkTaxRefundPolicyPO;
import com.yuqian.itax.park.entity.vo.ParkTaxRefundPolicyVO;

import java.util.List;

/**
 * 园区奖励政策service
 * 
 * @Date: 2022年09月26日 10:51:28 
 * @author 蒋匿
 */
public interface ParkTaxRefundPolicyService extends IBaseService<ParkTaxRefundPolicyEntity, ParkTaxRefundPolicyMapper> {


    /**
     * 根据园区id查询园区返税政策详情
     * @param parkId
     * @return
     */
    List<ParkTaxRefundPolicyVO> info(Long parkId);

    /**
     * 修改园区返税政策
     * @param poList
     * @param currUser
     */
    void save(List<ParkTaxRefundPolicyPO> poList, CurrUser currUser);

}

