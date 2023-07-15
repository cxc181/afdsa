package com.yuqian.itax.park.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.park.dao.TaxPolicyMapper;
import com.yuqian.itax.park.dao.TaxRulesConfigMapper;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.park.entity.dto.IndustryPolicyDTO;
import com.yuqian.itax.park.entity.vo.TaxRatesRulesVO;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service("taxRulesConfigService")
public class TaxRulesConfigServiceImpl extends BaseServiceImpl<TaxRulesConfigEntity,TaxRulesConfigMapper> implements TaxRulesConfigService {
    @Autowired
    private ParkService parkService;
    @Resource
    private TaxPolicyMapper taxPolicyMapper;

    @Override
    public List<TaxRulesConfigEntity> queryTaxRulesConfigEntityByParkId(Long parkId) {
        return mapper.queryTaxRulesConfigEntityByParkId(parkId);
    }

    @Override
    public List<TaxRulesConfigVO> queryTaxRules(Long parkId, Integer taxType, Integer companyType, Integer calType, Integer taxpayerType) throws BusinessException {

        // 初始化list
        List<TaxRulesConfigVO> list = new LinkedList<TaxRulesConfigVO>();

        // 查询减免额度
        TaxRulesConfigVO rule = new TaxRulesConfigVO();
        TaxPolicyEntity policy = taxPolicyMapper.queryTaxPolicyByParkId(parkId, companyType, taxpayerType);
        if(null == policy){
            throw new BusinessException("税费计算规则查询失败，税费政策不存在");
        }
        rule.setMinAmount(0L);
        if(taxType == 1){
            rule.setLevyWay(policy.getLevyWay());
            rule.setMaxAmount(policy.getIncomeTaxBreaksAmount());
        }else if(taxType == 2){
            rule.setMaxAmount(policy.getVatBreaksAmount());
        }
        if(rule.getMaxAmount()!=null&&rule.getMaxAmount() > 0){
            rule.setIsFree(1);
            list.add(rule);
        }

        // 按条件查询
        List<TaxRulesConfigVO> ruleList = this.mapper.queryTaxRulesConfig(parkId, companyType, taxType, calType, policy.getId());
        if(CollectionUtils.isEmpty(ruleList)){
            throw new BusinessException("税费计算规则查询失败，税费规则不存在");
        }

        for(TaxRulesConfigVO step : ruleList){
            if(taxType == 1){
                step.setLevyWay(policy.getLevyWay());
                step.setMinAmount(step.getMinAmount() + policy.getIncomeTaxBreaksAmount());
                if(step.getMaxAmount() < Integer.MAX_VALUE){
                    step.setMaxAmount(step.getMaxAmount() + policy.getIncomeTaxBreaksAmount());
                }
            }else if(taxType == 2){
                step.setMinAmount(step.getMinAmount() + policy.getVatBreaksAmount());
                if(step.getMaxAmount() < Integer.MAX_VALUE){
                    step.setMaxAmount(step.getMaxAmount() + policy.getVatBreaksAmount());
                }
            }
        }
        list.addAll(ruleList);
        return list;
    }

    @Override
    public List<TaxRulesVatRateVO> queryTaxRulesVatRate(Long parkId, Integer companyType, Integer invoiceType) {
        return mapper.queryTaxRulesVatRate(parkId, companyType, invoiceType);
    }

    @Override
    @Transactional
    public void insertIndustryRules(IndustryPolicyDTO dto, TaxPolicyEntity taxPolicyEntity, String useraccount) {
        Date date = new Date();
        //删除原有的行业税率政策
        mapper.deleteByPolicyId(taxPolicyEntity.getId(), dto.getChangeIndustryId());
        //新增行业税率政策
        TaxRulesConfigEntity entity;
        List<TaxRatesRulesVO> rules = dto.getRules();
        for (TaxRatesRulesVO vo : rules) {
            entity = new TaxRulesConfigEntity();
            entity.setPolicyId(taxPolicyEntity.getId());
            entity.setParkId(taxPolicyEntity.getParkId());
            entity.setCompanyType(taxPolicyEntity.getCompanyType());
            entity.setIndustryId(dto.getIndustryId());
            entity.setRate(vo.getRate());
            entity.setMinAmount(vo.getMinAmount());
            entity.setMaxAmount(vo.getMaxAmount());
            entity.setTaxType(1);
            entity.setAddTime(date);
            entity.setAddUser(useraccount);
            mapper.insert(entity);
        }
    }

    @Override
    public List<TaxRulesConfigEntity> queryTaxRulesConfigEntityByPolicyId(Long policyId) {
        return mapper.queryTaxRulesConfigEntityByPolicyId(policyId);
    }

    @Override
    public List<TaxRatesRulesVO> queryByIndustryId(Long policyId, Long industryId, Integer taxType) {
        return mapper.queryByIndustryId(policyId, industryId, taxType);
    }

    @Override
    public TaxRulesConfigEntity queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount( Integer taxType, Long invoiceAmount, Long parkId, Integer companyType,Long industryId) {
        return mapper.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(taxType,invoiceAmount,parkId,companyType,industryId);
    }

    @Override
    public List<TaxRulesConfigVO> queryTaxRulesConfigMinRate(Long parkId,Integer companyType,Integer taxType) {
        return mapper.queryTaxRulesConfigMinRate(parkId, companyType, taxType);
    }
}

