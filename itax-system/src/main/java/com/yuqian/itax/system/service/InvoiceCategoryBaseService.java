package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.InvoiceCategoryBaseMapper;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.entity.dto.InvoiceCategoryBaseDTO;
import com.yuqian.itax.system.entity.query.InvoiceCategoryBaseQuery;
import com.yuqian.itax.system.entity.vo.GroupSelectVO;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseVO;
import com.yuqian.itax.system.entity.vo.SearchInvoiceCategoryBaseVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基础开票类目库service
 * 
 * @Date: 2020年12月25日 11:38:14 
 * @author 蒋匿
 */
public interface InvoiceCategoryBaseService extends IBaseService<InvoiceCategoryBaseEntity,InvoiceCategoryBaseMapper> {

    /**
     * 基础类目库列表（分页）
     */
    PageInfo<InvoiceCategoryBaseVO> queryInvoiceCategoryBasePageInfo(InvoiceCategoryBaseQuery query);

    /**
     * 基础类目库列表
     */
    List<InvoiceCategoryBaseVO> queryInvoiceCategoryBaseList(InvoiceCategoryBaseQuery query);
    /**
     * 批量导入
     */
    void add (InvoiceCategoryBaseDTO dto);

    /**
     *
     * @param query
     * @return
     */
    List<String> queryTaxClassificationAbbreviationList(InvoiceCategoryBaseQuery query);

    /**
     * 集团开票下拉专用类目查询接口
     */
    List<GroupSelectVO> queryInvoiceCategoryBaseGroupSelect();

    /**
     * 根据关键词搜索开票类目
     * @param keyWord
     * @return
     */
    List<SearchInvoiceCategoryBaseVO> getCategoryByKeyWord(String keyWord);

    /**
     * 根据类目id获取增值税率列表
     * @param categoryId 基础类目id
     * @return
     */
    List<BigDecimal> findVatFeeRateByCategoryId(Long categoryId);
}

