package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.dao.ParkCorporateAccountConfigMapper;

import java.util.List;

/**
 * 园区对公户提现配置service
 * 
 * @Date: 2020年09月07日 09:14:49 
 * @author 蒋匿
 */
public interface ParkCorporateAccountConfigService extends IBaseService<ParkCorporateAccountConfigEntity,ParkCorporateAccountConfigMapper> {

    /**
     * @Description 根据用户ID查询支持对公户园区列表
     * @Author  Kaven
     * @Date   2020/9/7 10:48
     * @Param userId
     * @Return List<ParkCorporateAccountConfigEntity>
     * @Exception
    */
    List<ParkCorporateAccountConfigEntity> selectByMemberId(Long userId);

    /**
     * 根据园区id查询配置
     * @param parkId
     * @return
     */
    List<ParkCorporateAccountConfigEntity> getConfigByParkId(Long parkId);
}

