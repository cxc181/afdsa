package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.entity.dto.InvoiceCategoryBaseDTO;
import com.yuqian.itax.system.entity.query.InvoiceCategoryBaseQuery;
import com.yuqian.itax.system.entity.vo.GroupSelectVO;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseVO;
import com.yuqian.itax.system.entity.vo.SearchInvoiceCategoryBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基础开票类目库dao
 * 
 * @Date: 2020年12月25日 11:38:14 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceCategoryBaseMapper extends BaseMapper<InvoiceCategoryBaseEntity> {

    /**
     * 基础类目库列表
     */
    List<InvoiceCategoryBaseVO> queryInvoiceCategoryBaseList(InvoiceCategoryBaseQuery query);
    /**
     * 批量插入
     */
    void batchAdd(InvoiceCategoryBaseDTO dto);

    List<String> queryTaxClassificationAbbreviationList(InvoiceCategoryBaseQuery query);

    /**
     *
     */
    List<GroupSelectVO> queryInvoiceCategoryBaseGroupSelect();

    /**
     * 根据关键词搜索开票类目
     * @param keyWord
     * @return
     */
    List<SearchInvoiceCategoryBaseVO> queryCategoryByKeyWord(@Param("keyWord") String keyWord);

    /**
     * 根据类目id获取增值税率列表
     * @param categoryId 基础类目id
     * @return
     */
    List<BigDecimal> findVatFeeRateByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据税收分类编码查询  基础开票类目库表 是否存在该数据
     * @param taxClassificationCode
     * @return
     */
    Integer getInvoiceCategoryOne(@Param("taxClassificationCode")String taxClassificationCode);

}

