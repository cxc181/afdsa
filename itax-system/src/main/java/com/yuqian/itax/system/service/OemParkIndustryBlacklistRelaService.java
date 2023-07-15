package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.OemParkIndustryBlacklistRelaMapper;
import com.yuqian.itax.system.entity.OemParkIndustryBlacklistRelaEntity;

import java.util.List;

/**
 * oem机构园区行业黑名单service
 * 
 * @Date: 2020年08月07日 10:38:39 
 * @author 蒋匿
 */
public interface OemParkIndustryBlacklistRelaService extends IBaseService<OemParkIndustryBlacklistRelaEntity,OemParkIndustryBlacklistRelaMapper> {

    /**
     * 批量添加园区规则例外表
     * @param oemCode
     * @param parkId
     * @param ids
     * @param useraccount
     */
    void addBatch(String oemCode, Long parkId, List<Long> ids, String useraccount);

    /**
     * 删除OEM机构不在当前当前园区的行业黑名单
     * @param oemCode
     * @param parkIdList
     */
    void deleteByParkIds(String oemCode, List<Long> parkIdList);
}

