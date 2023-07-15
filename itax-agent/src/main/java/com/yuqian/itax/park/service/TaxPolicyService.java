package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.TaxPolicyMapper;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.po.TaxPolicyPO;
import com.yuqian.itax.park.entity.query.IndustryPolicyQuery;
import com.yuqian.itax.park.entity.vo.IndustryPolicyVO;
import com.yuqian.itax.park.entity.vo.TaxPolicySelectVO;
import com.yuqian.itax.park.entity.vo.TaxPolicyVO;

import java.util.List;
import java.util.Map;

/**
 * 税费政策service
 * 
 * @Date: 2019年12月12日 18:38:59 
 * @author 蒋匿
 */
public interface TaxPolicyService extends IBaseService<TaxPolicyEntity,TaxPolicyMapper> {

    /**
     * 新增园区政策
     * @param taxPolicyEntity
     * @param userAccount
     */
    void addTaxPolicy(TaxPolicyEntity taxPolicyEntity,String userAccount);
    /**
     * 根据园区id获取园区政策配置企业类型
     * @param parkId
     * @return
     */
    List<Map<String,Object>> queryTaxPolicyCompanyTypeByParkId(Long parkId);

    /**
     * 根据parkId和企业类型查询税费政策
     * @param parkId 园区id
     * @param companyType 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     * @param taxpayerType 纳税人类型  1-小规模纳税人 2-一般纳税人
     * @return
     */
    List<TaxPolicyVO> queryTaxPolicyByParkIdAndCompanyType(Long parkId,Integer companyType,Integer taxpayerType);
    /**
     * 编辑税费政策
     */
    void    updateTaxRules(TaxPolicyPO taxPolicyPO,String userAccount);

    /**
     * 编辑其他税费政策
     */
    void   updateOtherTaxRules(Long policyId,Long totalInvoiceAmount,Long quarterInvoiceAmount,String parkPolicyDesc,String specialConsiderations,String userAccount,Long monthInvoiceAmount);

    /**
     * 保存税费政策历史数据
     * @param parkId 园区id
     * @param companyType 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     * @param taxpayerType 纳税人类型  1-小规模纳税人 2-一般纳税人
     * @param userAccount
     */
    void saveTaxPolicyChangeData(Long parkId,Integer companyType,Integer taxpayerType,String userAccount);

    List<TaxPolicySelectVO> queryTaxPolicySelectByParkId(Long parkId);
    /**
     * 分页查询列外行业税率
     * @param query
     * @return
     */
    PageInfo<IndustryPolicyVO> listPageIndustryRules(IndustryPolicyQuery query);

    /**
     * 根据园区id和企业类型查询税费政策
     * @param parkId
     * @param companyType
     * @param taxpayerType
     * @return
     */
    TaxPolicyEntity queryTaxPolicyByParkId(Long parkId, Integer companyType,Integer taxpayerType);

    /**
     * 根据征收方式查询税费政策
     * @param oemCode
     * @param incomeLevyType
     * @return
     */
    List<TaxPolicyEntity> queryByIncomeLevyType(String oemCode, Integer incomeLevyType);
}

