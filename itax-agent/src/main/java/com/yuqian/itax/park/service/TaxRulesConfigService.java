package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.TaxRulesConfigMapper;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.park.entity.dto.IndustryPolicyDTO;
import com.yuqian.itax.park.entity.vo.TaxRatesRulesVO;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;

import java.util.List;

/**
 * 税费规则配置service
 * 
 * @Date: 2019年12月12日 18:40:35 
 * @author 蒋匿
 */
public interface TaxRulesConfigService extends IBaseService<TaxRulesConfigEntity,TaxRulesConfigMapper> {

    /**
     * 根据Parkid查询税费政策列表
     */
    List<TaxRulesConfigEntity> queryTaxRulesConfigEntityByParkId (Long parkId);

    /**
     * @Description 根据园区ID查询税费计算规则
     * @Author  Kaven
     * @Date   2020/3/27 14:45
     * @Param parkId 园区ID
     * @Param taxType 税种类型 1-所得税 2-增值税
     * @Param companyType 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     * @Param calType 计算类型 1-按月 2-按季
     * @Param taxpayerType 纳税人类型 1-小规模纳税人 2-一般纳税人
     * @Return  List<TaxRulesConfigEntity>
     * @Exception  BusinessException
    */
    List<TaxRulesConfigVO> queryTaxRules(Long parkId, Integer taxType, Integer companyType, Integer calType, Integer taxpayerType) throws BusinessException;

    /**
     * 查询增值税税率列表（小规模纳税人）
     * @param parkId
     * @param companyType
     */
    List<TaxRulesVatRateVO> queryTaxRulesVatRate(Long parkId, Integer companyType, Integer invoiceType);

    /**
     * 保存列外行业数据
     * @param dto
     * @param taxPolicyEntity
     * @param useraccount
     */
    void insertIndustryRules(IndustryPolicyDTO dto, TaxPolicyEntity taxPolicyEntity, String useraccount);

    /**
     * 根据政策查信息
     * @param policyId
     * @return
     */
    List<TaxRulesConfigEntity> queryTaxRulesConfigEntityByPolicyId(Long policyId);

    /**
     * 根据行业查询税率
     * @param policyId
     * @param industryId
     * @param taxType
     * @return
     */
    List<TaxRatesRulesVO> queryByIndustryId(Long policyId, Long industryId, Integer taxType);
    /**
     * 根据税种和开票金额取税费政策
     */
    TaxRulesConfigEntity queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(Integer taxType, Long invoiceAmount, Long parkId, Integer companyType,Long industryId);

    List<TaxRulesConfigVO> queryTaxRulesConfigMinRate(Long parkId,Integer companyType,Integer taxType);
}

