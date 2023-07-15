package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import com.yuqian.itax.user.dao.CompanyInvoiceCategoryMapper;
import com.yuqian.itax.user.entity.vo.CompanyInvoiceCategoryJdVO;

import java.util.List;

/**
 * 企业开票类目表service
 * 
 * @Date: 2020年05月18日 10:18:18 
 * @author 蒋匿
 */
public interface CompanyInvoiceCategoryService extends IBaseService<CompanyInvoiceCategoryEntity,CompanyInvoiceCategoryMapper> {

    /**
     * 根据订单更新公司id
     * @param companyId
     * @param orderNo
     * @param oemCode
     */
    void updateCompanyIdByOrderNo(Long companyId, String orderNo, String oemCode);

    /**
     * 批量添加企业开票类目
     * @param entity
     * @param categoryNames
     */
    void addBatch(CompanyInvoiceCategoryEntity entity, List<String> categoryNames);

    /**
     * 查询企业开票类目
     * @param orderNo
     * @param oemCode
     * @param industryId
     * @return
     */
    List<String> getCategoryNames(String orderNo, String oemCode, Long industryId);

    /**
     * 根据行业id保存企业开票类目
     * @param oemCode
     * @param orderNo
     * @param industryId
     * @param addUser
     */
    void addByIndustryId(String oemCode,String orderNo,Long industryId,String addUser);
    /**
     * 删除开票类目根据企业ID
     */
    void deleteByCompanyId(Long companyId);
    /**
     * 删除开票类目根据企业ID
     */
    void deleteByCategoryBaseId(Long companyId);
    /**
     * 获取一条OEM机构匹配的企业开票类目信息
     * @param companyId
     * @return
     */
    CompanyInvoiceCategoryEntity queryOemInvoiceCategory(Long companyId);

    /**
     * 获取一条OEM机构匹配的企业开票类目信息(第三方)
     * @param companyId
     * @return
     */
    public List<CompanyInvoiceCategoryJdVO> queryCompanyInvoiceCategoryJd(Long companyId);
    /**
     * 根据基础类目id批量更新企业类目表
     */
    void batchUpdateByCategoryBaseId(Long categortBaseId);
}

