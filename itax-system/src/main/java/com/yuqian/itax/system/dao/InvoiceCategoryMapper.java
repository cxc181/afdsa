package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.dto.IndustryInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 开票类目dao
 * 
 * @Date: 2019年12月08日 20:37:55 
 * @author yejian
 */
@Mapper
public interface InvoiceCategoryMapper extends BaseMapper<InvoiceCategoryEntity> {

    /**
     * 获取指定行业id的开票类目列表
     * @Param companyId
     * @return
     */
    List<InvoiceCategoryEntity> listInvoiceCategory(@Param("companyId") Long companyId, @Param("oemCode") String oemCode);

    /**
     * 批量添加开票类目
     * @param dto
     */
    void addBatch(IndustryInfoDTO dto);

    /**
     * 根据行业id删除开票类目
     * @param industryId
     */
    void delByIndustryId(@Param("industryId") Long industryId);

    void deleteByCategoryBaseId(@Param("categoryBaseId") Long categoryBaseId);
    /**
     * 根据基础类目id批量
     */
    void batchUpdateByCategoryBaseId(@Param("categoryBaseId") Long categortBaseId);

    /**
     * 根据行业id查询开票类目名称
     * @param industryId
     * @return
     */
    List<String> queryCategoryNameByIndustryId(@Param("industryId") Long industryId);
}

