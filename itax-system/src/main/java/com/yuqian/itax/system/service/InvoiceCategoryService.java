package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.InvoiceCategoryMapper;
import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.system.entity.dto.IndustryInfoDTO;

import java.util.List;

/**
 * 开票类目service
 * 
 * @Date: 2019年12月08日 20:37:55 
 * @author yejian
 */
public interface InvoiceCategoryService extends IBaseService<InvoiceCategoryEntity,InvoiceCategoryMapper> {

    /**
     * 获取指定行业的开票类目列表
     * @Param companyId
     * @return
     */
    List<InvoiceCategoryEntity> listInvoiceCategory(Long companyId, String oemCode) throws BusinessException;

    /**
     * 批量添加开票类目
     * @param dto
     */
    void addBatch(IndustryInfoDTO dto);

    /**
     * 根据行业id删除开票类目
     * @param industryId
     */
    void delByIndustryId(Long industryId);

    void deleteByCategoryBaseId(Long categoryBaseId);

    /**
     *  根据基础类目id批量更新
     */
    void batchUpdateByCategoryBaseId(Long categortBaseId);

    /**
     * 根据行业id查询开票类目名称
     * @param industryId
     * @return
     */
    List<String> findCategoryNameByIndustryId(Long industryId);
}

