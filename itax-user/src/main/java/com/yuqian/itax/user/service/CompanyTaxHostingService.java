package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.CompanyTaxHostingEntity;
import com.yuqian.itax.user.dao.CompanyTaxHostingMapper;
import com.yuqian.itax.user.entity.po.CompanyTaxHostingPO;

/**
 * 企业税务托管表service
 * 
 * @Date: 2020年12月25日 11:38:54 
 * @author 蒋匿
 */
public interface CompanyTaxHostingService extends IBaseService<CompanyTaxHostingEntity,CompanyTaxHostingMapper> {

    /**
     * 根据企业id获取托管信息
     * @param companyId 企业id
     * @param channel 托管通道 1-百旺
     * @return
     */
    CompanyTaxHostingEntity getCompanyTaxHostingByCompanyId(Long companyId,Integer channel);
    /**
     * 新增或者更新
     */
    void addOrUpdate(CompanyTaxHostingPO po,String account);
}

