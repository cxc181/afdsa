package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.CompanyResourcesAddressEntity;
import com.yuqian.itax.user.dao.CompanyResourcesAddressMapper;
import com.yuqian.itax.user.entity.po.CompanyResourcesAddressPO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesAddressVO;

import java.util.List;

/**
 * 企业资源所在地管理service
 * 
 * @Date: 2019年12月14日 13:56:31 
 * @author 蒋匿
 */
public interface CompanyResourcesAddressService extends IBaseService<CompanyResourcesAddressEntity,CompanyResourcesAddressMapper> {

    /**
     * 根据公司id查询企业资源所在地
     * @param companyId
     * @param oemCode
     * @return
     */
    List<CompanyResourcesAddressEntity> listCompanyResourcesAddress(Long companyId, String oemCode);
    /**
     * 获取 企业资源所在地管理 详情
     */
    CompanyResourcesAddressVO queryCompanyResourcesAddressDetail(Long id);
    /**
     * 编辑 企业资源所在地管理
     */
    void updateCompanyResourcesAddress(CompanyResourcesAddressPO po,String account);

    /**
     * 批量修改企业资源所在地 ， 用 ，分开
     */
    String updateBatchCompanyResourcesAddress(String  applyResouces,Long id,String account);

}

