package com.yuqian.itax.park.dao;

import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.vo.TaxRatesRulesVO;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 税费规则配置dao
 * 
 * @Date: 2019年12月12日 18:40:35 
 * @author 蒋匿
 */
@Mapper
public interface TaxRulesConfigMapper extends BaseMapper<TaxRulesConfigEntity> {


    List<TaxRulesConfigEntity> queryTaxRulesConfigEntityByParkId(@Param("parkId") Long parkId);

    List<TaxRulesConfigEntity> queryTaxRulesConfigEntityByPolicyId(@Param("policyId") Long policyId);

    List<TaxRulesConfigVO> queryTaxRulesConfig(@Param("parkId") Long parkId, @Param("companyType") Integer companyType, @Param("taxType") Integer taxType,
                                               @Param("calType") Integer calType, @Param("policyId") Long policyId);


    List<TaxRulesConfigVO> queryTaxRulesConfigMinRate(@Param("parkId") Long parkId, @Param("companyType") Integer companyType, @Param("taxType") Integer taxType);


    List<TaxRulesVatRateVO> queryTaxRulesVatRate(@Param("parkId") Long parkId, @Param("companyType") Integer companyType, @Param("invoiceType") Integer invoiceType);

    /**
     * 删除税费规则配置
     * @param policyId
     * @param industryId
     */
    void deleteByPolicyId(@Param("policyId") Long policyId, @Param("industryId") Long industryId);

    /**
     * 根据行业查税率
     * @param policyId
     * @param industryId
     * @return
     */
    List<TaxRatesRulesVO> queryByIndustryId(@Param("policyId") Long policyId, @Param("industryId") Long industryId, @Param("taxType") Integer taxType);

    /**
     * 查询税费政策
     */
    TaxRulesConfigEntity queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount( @Param("taxType") Integer taxType,@Param("invoiceAmount") Long invoiceAmount,@Param("parkId") Long parkId,@Param("companyType" ) Integer companyType,@Param("industryId") Long industryId );
}

