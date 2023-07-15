package com.yuqian.itax.user.dao;

import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.vo.CompanyInvoiceCategoryJdVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业开票类目表dao
 * 
 * @Date: 2020年05月18日 10:18:18 
 * @author 蒋匿
 */
@Mapper
public interface CompanyInvoiceCategoryMapper extends BaseMapper<CompanyInvoiceCategoryEntity> {

    /**
     * 根据订单更新公司id
     * @param companyId
     * @param orderNo
     * @param oemCode
     */
    void updateCompanyIdByOrderNo(@Param("companyId")Long companyId,  @Param("orderNo")String orderNo,  @Param("oemCode")String oemCode);

    /**
     * 批量添加企业开票类目
     * @param entity
     * @param categoryList
     */
    void addBatch(@Param("entity")CompanyInvoiceCategoryEntity entity, @Param("categoryNames")List<String> categoryList);

    /**
     * 批量添加企业开票类目
     * @param entity
     * @param categoryList
     */
    void addBatchByInvoiceCategoryBaseStringVO(@Param("entity")CompanyInvoiceCategoryEntity entity, @Param("categoryList")List<InvoiceCategoryBaseStringVO> categoryList);

    /**
     * 根据订单编号删除企业开票类目
     * @param orderNo
     * @param oemCode
     */
    void deleteByOrderNo(@Param("orderNo")String orderNo,  @Param("oemCode")String oemCode);

    /**
     * 根据行业id保存企业开票类目
     * @param oemCode
     * @param orderNo
     * @param industryId
     * @param addUser
     */
    void addByIndustryId(@Param("oemCode")String oemCode,@Param("orderNo")String orderNo,  @Param("industryId")Long industryId,@Param("addUser") String addUser);
    /**
     * 根据企业ID删除开票类目
     */
    void deleteByCompanyId(@Param("companyId") Long companyId);

    void deleteByCategoryBaseId(@Param("categoryBaseId") Long categoryBaseId);

    /**
     * 获取一条OEM机构匹配的企业开票类目信息
     * @param companyId
     * @return
     */
    CompanyInvoiceCategoryEntity queryOemInvoiceCategory(@Param("companyId")Long companyId);
    /**
     * 获取一条OEM机构匹配的企业开票类目信息
     * @param companyId
     * @return
     */
    List<CompanyInvoiceCategoryJdVO> queryCompanyInvoiceCategoryJd(@Param("companyId")Long companyId);

    /**
     * 根据基础类目id批量更新企业类目表
     */
    void batchUpdateByCategoryBaseId(@Param("categoryBaseId") Long categortBaseId) ;
}

