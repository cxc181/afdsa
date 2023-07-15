package com.yuqian.itax.park.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.dao.TaxPolicyMapper;
import com.yuqian.itax.park.dao.TaxRulesConfigMapper;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyChangeEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.park.entity.po.TaxPolicyPO;
import com.yuqian.itax.park.entity.query.IndustryPolicyQuery;
import com.yuqian.itax.park.entity.vo.IndustryPolicyVO;
import com.yuqian.itax.park.entity.vo.TaxPolicySelectVO;
import com.yuqian.itax.park.entity.vo.TaxPolicyVO;
import com.yuqian.itax.park.entity.vo.TaxRatesRulesVO;
import com.yuqian.itax.park.enums.LevyWayEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyChangeService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("taxPolicyService")
public class TaxPolicyServiceImpl extends BaseServiceImpl<TaxPolicyEntity, TaxPolicyMapper> implements TaxPolicyService {

    @Resource
    private TaxRulesConfigMapper taxRulesConfigMapper;
    @Autowired
    TaxPolicyChangeService taxPolicyChangeService;
    @Autowired
    ParkService parkService;

    /**
     * 新增园区政策
     * @param taxPolicyEntity
     * @param userAccount
     */
    @Override
    public void addTaxPolicy(TaxPolicyEntity taxPolicyEntity,String userAccount){
        taxPolicyEntity.setStatus(1);
        taxPolicyEntity.setTransactRequire("经营者身份证正反面照片");
        taxPolicyEntity.setAddTime(new Date());
        taxPolicyEntity.setAddUser(userAccount);
        taxPolicyEntity.setVatBreaksAmount(0L);
        taxPolicyEntity.setIncomeTaxBreaksAmount(0L);
        taxPolicyEntity.setVatBreaksCycle(1);
        taxPolicyEntity.setIncomeTaxBreaksCycle(1);
        taxPolicyEntity.setSurchargeBreaksAmount(0L);
        taxPolicyEntity.setSurchargeBreaksCycle(1);
//                if (taxPolicyEntity.getQuarterInvoiceAmount() !=null && taxPolicyEntity.getQuarterInvoiceAmount()> taxPolicyEntity.getTotalInvoiceAmount()){
//                    throw new BusinessException("季开票额度不能大于年开票额度");
//                }
        if (taxPolicyEntity.getMonthInvoiceAmount() != null && taxPolicyEntity.getMonthInvoiceAmount() > taxPolicyEntity.getTotalInvoiceAmount()) {
            throw new BusinessException("月开票额度不能大于近12个月可开票额度");
        }
        taxPolicyEntity.setTaxpayerType(1);
        if(taxPolicyEntity.getCompanyType().intValue()==1){
            taxPolicyEntity.setReadContent("我自愿办理个体户");
        }else if(taxPolicyEntity.getCompanyType().intValue()==2){
            taxPolicyEntity.setIncomeLevyType(1);
            taxPolicyEntity.setReadContent("我自愿办理个人独资企业");
            taxPolicyEntity.setTaxpayerType(2);
            mapper.insertSelective(taxPolicyEntity);
            taxPolicyEntity.setId(null);
            taxPolicyEntity.setTaxpayerType(1);
        }else if(taxPolicyEntity.getCompanyType().intValue()==3){
            taxPolicyEntity.setIncomeLevyType(1);
            taxPolicyEntity.setReadContent("我自愿办理有限合伙公司");
            taxPolicyEntity.setTaxpayerType(2);
            mapper.insertSelective(taxPolicyEntity);
            taxPolicyEntity.setId(null);
            taxPolicyEntity.setTaxpayerType(1);
        }else{
            taxPolicyEntity.setIncomeLevyType(1);
            taxPolicyEntity.setReadContent("我自愿办理有限责任公司");
            taxPolicyEntity.setTaxpayerType(2);
            mapper.insertSelective(taxPolicyEntity);
            taxPolicyEntity.setId(null);
            taxPolicyEntity.setTaxpayerType(1);
        }
        if(StringUtils.isNotBlank(taxPolicyEntity.getPolicyFileUrl())){
            taxPolicyEntity.setPolicyFileUrl(taxPolicyEntity.getPolicyFileUrl());
        }
        mapper.insertSelective(taxPolicyEntity);
    }

    /**
     * 根据园区id获取园区政策配置企业类型
     * @param parkId
     * @return
     */
    @Override
    public List<Map<String,Object>> queryTaxPolicyCompanyTypeByParkId(Long parkId){
        return mapper.queryTaxPolicyCompanyTypeByParkId(parkId);
    }

    @Override
    public List<TaxPolicyVO> queryTaxPolicyByParkIdAndCompanyType(Long parkId,Integer companyType,Integer taxpayerType) {
        List<TaxPolicyVO> taxPolicyVOList = new ArrayList<>();

        List<TaxPolicyEntity> list = mapper.queryTaxPolicyByParkIdAndCompanyType(parkId,companyType,taxpayerType);
        TaxPolicyEntity taxPolicyEntity = null;
        TaxRulesConfigEntity taxRulesConfigEntity = null;
        for (int i = 0; i < list.size(); i++) {
            taxPolicyEntity = list.get(i);
            TaxPolicyVO taxPolicyVO=new TaxPolicyVO();
            BeanUtils.copyProperties(taxPolicyEntity,taxPolicyVO);
            List<TaxRulesConfigEntity> taxRulesConfigResultList= new ArrayList<>();
            List<TaxRulesConfigEntity> taxRulesConfigList = taxRulesConfigMapper.queryTaxRulesConfigEntityByPolicyId(taxPolicyEntity.getId());
            for(int j=0;j<taxRulesConfigList.size();j++){
                 taxRulesConfigEntity=taxRulesConfigList.get(j);
                taxRulesConfigResultList.add(taxRulesConfigEntity);
            }
            taxPolicyVO.setTaxRulesConfigEntityList(taxRulesConfigResultList);

            taxPolicyVOList.add(taxPolicyVO);
        }
        return taxPolicyVOList;
    }

    @Override
    @Transactional
    public void updateTaxRules(TaxPolicyPO taxPolicyPO,String userAccount) {
        Long parkId = null;
        List<TaxRulesConfigEntity> list=taxPolicyPO.getTaxRulesConfigEntityList();
        TaxPolicyEntity taxPolicyEntity=mapper.selectByPrimaryKey(taxPolicyPO.getId());
        if (taxPolicyEntity == null) {
            throw new BusinessException("税费政策不存在");
        }
        parkId = taxPolicyEntity.getParkId();
        //校验
        //list排序
        List<TaxRulesConfigEntity> collect = list.stream().filter(u -> u.getTaxType() == 1).collect(Collectors.toList());
        if (Objects.equals(taxPolicyPO.getLevyWay(), LevyWayEnum.TAXABLE_INCOME_RATE.getValue())) {
            TaxRulesConfigEntity taxRulesConfigEntity = collect.get(0);
            taxRulesConfigEntity.setMinAmount(0L);
            taxRulesConfigEntity.setMaxAmount(Long.MAX_VALUE);
            collect.clear();
            collect.add(taxRulesConfigEntity);
        } else {
            Collections.sort(collect, new Comparator<TaxRulesConfigEntity>() {
                @Override
                public int compare(TaxRulesConfigEntity u1, TaxRulesConfigEntity u2) {
                    Long diff =(u1.getMinAmount() - u2.getMinAmount());
                    if (diff > 0L) {
                        return 1;
                    }else if (diff < 0L) {
                        return -1;
                    }
                    return 0; //相等为0
                }
            });
            for(int i=0;i<collect.size();i++){
                TaxRulesConfigEntity taxRulesConfigEntity=collect.get(i);
                if(taxRulesConfigEntity.getTaxType()!=3){
                    continue;
                }
                if (taxRulesConfigEntity.getRate() == null) {
                    throw new BusinessException("所得税税率不能为空");
                }
                if (taxRulesConfigEntity.getRate().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("所得税税率必须大于或等于0%");
                }
                if (taxRulesConfigEntity.getRate().compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessException("所得税税率必须小于或等于100%");
                }
                if(!Objects.equals(taxRulesConfigEntity.getMinAmount(),0L) && i==0){
                    throw  new BusinessException("区间开始最小值必须为0，请确认");
                }
                if (collect.size() -1 == i) {
                    taxRulesConfigEntity.setMaxAmount(Long.MAX_VALUE);
                }
                if(taxRulesConfigEntity.getMaxAmount()-taxRulesConfigEntity.getMinAmount()<=0L){
                    throw  new BusinessException("最小值必须大于最大值，请确认");
                }
                if(collect.size()>1 && collect.size()!=i+1){
                    TaxRulesConfigEntity taxRulesConfigEntityNext=list.get(i+1);
                    if(!Objects.equals(taxRulesConfigEntity.getMaxAmount(),taxRulesConfigEntityNext.getMinAmount())){
                        throw  new BusinessException("区间不连续，请确认");
                    }
                }
            }
        }

        ParkEntity parkEntity =  parkService.findById(parkId);
        if (parkEntity==null){
            throw new BusinessException("园区信息不存在");
        }
        if(taxPolicyPO.getCompanyType() == 1 &&parkEntity.getStatus() != 0 && taxPolicyEntity.getIncomeLevyType() != taxPolicyPO.getIncomeLevyType()){
            throw new BusinessException("个体户非待上架状态征收方式不支持修改");
        }
        if(taxPolicyPO.getCompanyType() == 1 &&parkEntity.getStatus() != 0 && taxPolicyEntity.getLevyWay() != taxPolicyPO.getLevyWay()){
            throw new BusinessException("个体户非待上架状态计税方式不支持修改");
        }

        if (null != taxPolicyPO.getIncomeTaxableIncomeBreaks() && taxPolicyPO.getCompanyType() == 1) {
            if (taxPolicyPO.getIncomeTaxableIncomeBreaks() < 0L || taxPolicyPO.getIncomeTaxableIncomeBreaks() > 500000000L) {
                throw new BusinessException("应纳税所得额支持输入0~500w");
            }

        }
        if (null != taxPolicyPO.getIncomeTaxReliefRatio() && taxPolicyPO.getCompanyType() == 1) {
            if (taxPolicyPO.getIncomeTaxReliefRatio().compareTo(BigDecimal.ZERO) < 0 || taxPolicyPO.getIncomeTaxReliefRatio().compareTo(new BigDecimal("100")) >0) {
                throw new BusinessException("税额减免支持输入0~100的数字");
            }
        }

        // 如果计税方式改为预缴征收率，清除应纳税所得额及税额减免数据
        if (LevyWayEnum.LEVY_RATE.getValue().equals(taxPolicyPO.getLevyWay())) {
            taxPolicyPO.setIncomeTaxableIncomeBreaks(0L);
            taxPolicyPO.setIncomeTaxReliefRatio(BigDecimal.ZERO);
        }

        Integer levyWay = taxPolicyEntity.getLevyWay();
        taxPolicyEntity.setVatBreaksAmount(taxPolicyPO.getVatBreaksAmount());
        taxPolicyEntity.setVatBreaksCycle(taxPolicyPO.getVatBreaksCycle());
        taxPolicyEntity.setSurchargeBreaksAmount(taxPolicyPO.getSurchargeBreaksAmount());
        taxPolicyEntity.setSurchargeBreaksCycle(taxPolicyPO.getVatBreaksCycle());
        taxPolicyEntity.setIncomeTaxBreaksAmount(taxPolicyPO.getIncomeTaxBreaksAmount());
        taxPolicyEntity.setIncomeTaxBreaksCycle(taxPolicyPO.getIncomeTaxBreaksCycle());
        if(taxPolicyPO.getCompanyType() == 1) {
            taxPolicyEntity.setLevyWay(taxPolicyPO.getLevyWay());
            taxPolicyEntity.setIncomeLevyType(taxPolicyPO.getIncomeLevyType());
            taxPolicyEntity.setIncomeTaxableIncomeBreaks(null == taxPolicyPO.getIncomeTaxableIncomeBreaks() ? 0L : taxPolicyPO.getIncomeTaxableIncomeBreaks());
            taxPolicyEntity.setIncomeTaxReliefRatio(null == taxPolicyPO.getIncomeTaxReliefRatio() ? BigDecimal.ZERO : taxPolicyPO.getIncomeTaxReliefRatio());
        }else{
            taxPolicyEntity.setStampDutyBreaksCycle(taxPolicyPO.getStampDutyBreaksCycle());
            taxPolicyEntity.setIsStampDutyHalved(taxPolicyPO.getIsStampDutyHalved());
            taxPolicyEntity.setWaterConservancyFundBreaksCycle(taxPolicyPO.getWaterConservancyFundBreaksCycle());
            taxPolicyEntity.setIsWaterConservancyFundHalved(taxPolicyPO.getIsWaterConservancyFundHalved());
            taxPolicyEntity.setIncomeLevyType(1);
        }
        taxPolicyEntity.setUpdateTime(new Date());
        taxPolicyEntity.setStatus(1);
        taxPolicyEntity.setUpdateUser(userAccount);
        taxPolicyEntity.setRemark("税费政策编辑");
        mapper.updateByPrimaryKeySelective(taxPolicyEntity);
        //更新税费政策规则表
        if (Objects.equals(levyWay, taxPolicyPO.getLevyWay())) {
            //删除行业非空所有税费政策
            taxRulesConfigMapper.deleteByPolicyId(taxPolicyEntity.getId(), null);
        } else {
            //删除当前所有税费政策
            taxRulesConfigMapper.deleteByPolicyId(taxPolicyEntity.getId(), -1L);
        }
        for (TaxRulesConfigEntity taxRulesConfigEntity:collect) {
            //所得税区间
            TaxRulesConfigEntity entity =new TaxRulesConfigEntity();
            entity.setParkId(taxPolicyEntity.getParkId());
            entity.setPolicyId(taxPolicyEntity.getId());
            entity.setCompanyType(taxPolicyEntity.getCompanyType());
            entity.setRate(taxRulesConfigEntity.getRate());
            entity.setMinAmount(taxRulesConfigEntity.getMinAmount());
            if(taxRulesConfigEntity.getMaxAmount() == null){
                entity.setMaxAmount(Long.MAX_VALUE);
            }else{
                entity.setMaxAmount(taxRulesConfigEntity.getMaxAmount());
            }
            entity.setTaxType(1);
            entity.setAddTime(new Date());
            entity.setAddUser(userAccount);
            entity.setTaxpayerType(taxPolicyEntity.getTaxpayerType());
            taxRulesConfigMapper.insertSelective(entity);
        }

        //附加税
        List<TaxRulesConfigEntity> vfjCollect = list.stream().filter(u -> u.getTaxType() == 3).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(vfjCollect)) {
            throw  new BusinessException("请配置附加税");
        }
        for(int i=0;i<vfjCollect.size();i++){
            TaxRulesConfigEntity  taxRulesConfigEntity=vfjCollect.get(i);
            if(taxRulesConfigEntity.getTaxType()!=3){
                continue;
            }
            if((taxRulesConfigEntity.getUrbanConstructionTaxRate().add(taxRulesConfigEntity.getEducationSurchargeTaxRate()).add(taxRulesConfigEntity.getLocalEducationSurchargeRate())).compareTo(new BigDecimal(100))>0){
                throw  new BusinessException("城建税税率,教育费附加税税率,地方教育附加税税率之和不能大于100");
            }
            TaxRulesConfigEntity fjEntity =new TaxRulesConfigEntity();
            fjEntity.setParkId(taxPolicyEntity.getParkId());
            fjEntity.setMinAmount(0L);
            fjEntity.setMaxAmount(0L);
            fjEntity.setPolicyId(taxPolicyEntity.getId());
            fjEntity.setCompanyType(taxPolicyEntity.getCompanyType());
            fjEntity.setRate(taxRulesConfigEntity.getUrbanConstructionTaxRate().add(taxRulesConfigEntity.getLocalEducationSurchargeRate()).add(taxRulesConfigEntity.getEducationSurchargeTaxRate()));
            fjEntity.setTaxType(3);
            fjEntity.setAddTime(new Date());
            fjEntity.setAddUser(userAccount);
            fjEntity.setEducationSurchargeTaxRate(taxRulesConfigEntity.getEducationSurchargeTaxRate());
            fjEntity.setLocalEducationSurchargeRate(taxRulesConfigEntity.getLocalEducationSurchargeRate());
            fjEntity.setUrbanConstructionTaxRate(taxRulesConfigEntity.getUrbanConstructionTaxRate());
            fjEntity.setIsOpenPp(taxRulesConfigEntity.getIsOpenPp());
            fjEntity.setIsOpenZp(taxRulesConfigEntity.getIsOpenZp());
            fjEntity.setTaxpayerType(taxPolicyEntity.getTaxpayerType());
            taxRulesConfigMapper.insertSelective(fjEntity);
        }

        //增值税
        if(taxPolicyEntity.getTaxpayerType() == 1) {
            List<TaxRulesConfigEntity> vatCollect = list.stream().filter(u -> u.getTaxType() == 2).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(vatCollect)) {
                throw new BusinessException("请配置增值税");
            }
            for (TaxRulesConfigEntity taxRulesConfigEntity : vatCollect) {
                if (taxRulesConfigEntity.getRate() == null) {
                    throw new BusinessException("增值税税率不能为空");
                }
                if (taxRulesConfigEntity.getRate().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("增值税税率必须大于或等于0%");
                }
                if (taxRulesConfigEntity.getRate().compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessException("增值税税率必须小于或等于100%");
                }
                TaxRulesConfigEntity vatEntity = new TaxRulesConfigEntity();
                vatEntity.setParkId(taxPolicyEntity.getParkId());
                vatEntity.setMinAmount(0L);
                vatEntity.setMaxAmount(0L);
                vatEntity.setPolicyId(taxPolicyEntity.getId());
                vatEntity.setCompanyType(taxPolicyEntity.getCompanyType());
                vatEntity.setRate(taxRulesConfigEntity.getRate());
                vatEntity.setTaxType(2);
                vatEntity.setAddTime(new Date());
                vatEntity.setAddUser(userAccount);
                vatEntity.setIsOpenZp(taxRulesConfigEntity.getIsOpenZp());
                vatEntity.setIsOpenPp(taxRulesConfigEntity.getIsOpenPp());
                vatEntity.setTaxpayerType(taxPolicyEntity.getTaxpayerType());
                taxRulesConfigMapper.insertSelective(vatEntity);
                continue;
            }
        }
        //非个体企业类型 需要添加印花税和水利建设基金税
        if(taxPolicyPO.getCompanyType()!=1){
            //印花税
            List<TaxRulesConfigEntity> yhsCollect = list.stream().filter(u -> u.getTaxType() == 4).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(yhsCollect)) {
                throw  new BusinessException("请配置印花税");
            }
            for(int i=0;i<yhsCollect.size();i++){
                TaxRulesConfigEntity  taxRulesConfigEntity=yhsCollect.get(i);
                if(taxRulesConfigEntity.getTaxType()!=4){
                    continue;
                }
                if (taxRulesConfigEntity.getRate() == null) {
                    throw new BusinessException("印花税税率不能为空");
                }
                if (taxRulesConfigEntity.getRate().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("印花税税率必须大于或等于0%");
                }
                if (taxRulesConfigEntity.getRate().compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessException("印花税税率必须小于或等于100%");
                }

                TaxRulesConfigEntity yhsEntity =new TaxRulesConfigEntity();
                yhsEntity.setParkId(taxPolicyEntity.getParkId());
                yhsEntity.setMinAmount(0L);
                yhsEntity.setMaxAmount(0L);
                yhsEntity.setPolicyId(taxPolicyEntity.getId());
                yhsEntity.setCompanyType(taxPolicyEntity.getCompanyType());
                yhsEntity.setRate(taxRulesConfigEntity.getRate());
                yhsEntity.setTaxType(4);
                yhsEntity.setAddTime(new Date());
                yhsEntity.setAddUser(userAccount);
                yhsEntity.setTaxpayerType(taxPolicyEntity.getTaxpayerType());
                taxRulesConfigMapper.insertSelective(yhsEntity);
            }

            //水利建设基金税
            List<TaxRulesConfigEntity> sljsjjCollect = list.stream().filter(u -> u.getTaxType() == 5).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(sljsjjCollect)) {
                throw  new BusinessException("请配置水利建设基金税");
            }
            for(int i=0;i<sljsjjCollect.size();i++){
                TaxRulesConfigEntity  taxRulesConfigEntity=sljsjjCollect.get(i);
                if(taxRulesConfigEntity.getTaxType()!=5){
                    continue;
                }
                if (taxRulesConfigEntity.getRate() == null) {
                    throw new BusinessException("水利建设基金税率不能为空");
                }
                if (taxRulesConfigEntity.getRate().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("水利建设基金税税率必须大于或等于0%");
                }
                if (taxRulesConfigEntity.getRate().compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessException("水利建设基金税税率必须小于或等于100%");
                }
                TaxRulesConfigEntity sljsjjEntity =new TaxRulesConfigEntity();
                sljsjjEntity.setParkId(taxPolicyEntity.getParkId());
                sljsjjEntity.setMinAmount(0L);
                sljsjjEntity.setMaxAmount(0L);
                sljsjjEntity.setPolicyId(taxPolicyEntity.getId());
                sljsjjEntity.setCompanyType(taxPolicyEntity.getCompanyType());
                sljsjjEntity.setRate(taxRulesConfigEntity.getRate());
                sljsjjEntity.setTaxType(5);
                sljsjjEntity.setAddTime(new Date());
                sljsjjEntity.setAddUser(userAccount);
                sljsjjEntity.setTaxpayerType(taxPolicyEntity.getTaxpayerType());
                taxRulesConfigMapper.insertSelective(sljsjjEntity);
            }
        }
        //保存历史数据
        saveTaxPolicyChangeData(parkId,taxPolicyEntity.getCompanyType(),taxPolicyEntity.getTaxpayerType(),userAccount);
    }

    /**
     * 编辑其他税费政策
     */
    @Override
    @Transactional
    public void  updateOtherTaxRules(Long policyId,Long totalInvoiceAmount,Long quarterInvoiceAmount,String parkPolicyDesc,String specialConsiderations,String userAccount,Long monthInvoiceAmount){
        TaxPolicyEntity entity = this.findById(policyId);
        if(entity == null){
            throw new BusinessException("未找到园区政策信息数据");
        }
        if(entity.getTaxpayerType()==1 && (totalInvoiceAmount == null || totalInvoiceAmount<0 || totalInvoiceAmount>Long.MAX_VALUE) ){
            throw new BusinessException("近12个月可开票额度只能为大于0的数字");
        }
        if(totalInvoiceAmount!=null){
            entity.setTotalInvoiceAmount(totalInvoiceAmount);
        }
        if(quarterInvoiceAmount!=null){
            entity.setQuarterInvoiceAmount(quarterInvoiceAmount);
        }else{
            entity.setQuarterInvoiceAmount(null);
        }
        if (totalInvoiceAmount != null && quarterInvoiceAmount !=null && quarterInvoiceAmount> totalInvoiceAmount){
            throw new BusinessException("季开票额度不能大于年开票额度");
        }
        if (monthInvoiceAmount!=null){
            entity.setMonthInvoiceAmount(monthInvoiceAmount);
        }else {
            entity.setMonthInvoiceAmount(null);
        }
        if (totalInvoiceAmount != null && monthInvoiceAmount !=null && monthInvoiceAmount> totalInvoiceAmount){
            throw new BusinessException("月开票额度不能大于近12个月可开票额度");
        }
        if(StringUtil.isNotBlank(parkPolicyDesc)){
            entity.setParkPolicyDesc(parkPolicyDesc);
        }else{
            entity.setParkPolicyDesc(null);
        }
        if(StringUtil.isNotBlank(specialConsiderations)){
            entity.setSpecialConsiderations(specialConsiderations);
        }else{
            entity.setSpecialConsiderations(null);
        }
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userAccount);
        entity.setRemark("其他政策编辑");
        mapper.updateByPrimaryKey(entity);
        saveTaxPolicyChangeData(entity.getParkId(),entity.getCompanyType(),entity.getTaxpayerType(),userAccount);
    }

    /**
     * 保存历史数据
     * @param parkId
     * @param companyType
     * @param userAccount
     */
    @Override
    public void saveTaxPolicyChangeData(Long parkId,Integer companyType,Integer taxpayerType,String userAccount){
        List<TaxPolicyVO> taxPolicyVO=queryTaxPolicyByParkIdAndCompanyType(parkId,companyType,taxpayerType);
        //保存历史数据
        if (CollectionUtil.isNotEmpty(taxPolicyVO)){
            TaxPolicyChangeEntity taxPolicyChangeEntity = new TaxPolicyChangeEntity();
            TaxPolicyVO vo = taxPolicyVO.get(0);
            BeanUtils.copyProperties(vo,taxPolicyChangeEntity);
            taxPolicyChangeEntity.setId(null);
            taxPolicyChangeEntity.setPolicyId(vo.getId());
            taxPolicyChangeEntity.setParkId(parkId);
            taxPolicyChangeEntity.setCompanyType(vo.getCompanyType());
            taxPolicyChangeEntity.setLevyWay(vo.getLevyWay());
            taxPolicyChangeEntity.setVatBreaksAmount(vo.getVatBreaksAmount());
            if (vo.getVatBreaksCycle() !=null){
                taxPolicyChangeEntity.setVatBreaksCycle(vo.getVatBreaksCycle());
            }
            if (vo.getIncomeTaxBreaksCycle() !=null){
                taxPolicyChangeEntity.setIncomeTaxBreaksCycle( vo.getIncomeTaxBreaksCycle().intValue());
            }
            if (vo.getSurchargeBreaksCycle() !=null){
                taxPolicyChangeEntity.setSurchargeBreaksCycle(vo.getSurchargeBreaksCycle());
            }
            taxPolicyChangeEntity.setIncomeTaxBreaksAmount(vo.getIncomeTaxBreaksAmount());
            taxPolicyChangeEntity.setSurchargeBreaksAmount(vo.getSurchargeBreaksAmount());
            taxPolicyChangeEntity.setTransactRequire(vo.getTransactRequire());
            taxPolicyChangeEntity.setStatus(vo.getStatus());
            taxPolicyChangeEntity.setTotalInvoiceAmount(vo.getTotalInvoiceAmount());
            taxPolicyChangeEntity.setQuarterInvoiceAmount(vo.getQuarterInvoiceAmount());
            taxPolicyChangeEntity.setMonthInvoiceAmount(vo.getMonthInvoiceAmount());
            taxPolicyChangeEntity.setPolicyFileUrl(vo.getPolicyFileUrl());
            taxPolicyChangeEntity.setIncomeLevyType(vo.getIncomeLevyType());
            taxPolicyChangeEntity.setReadContent(vo.getReadContent());
            taxPolicyChangeEntity.setTaxRulesConfigJson(JSONObject.toJSONString(vo));
            taxPolicyChangeEntity.setAddTime(new Date());
            taxPolicyChangeEntity.setAddUser(userAccount);
            taxPolicyChangeEntity.setUpdateTime(new Date());
            taxPolicyChangeEntity.setUpdateUser(userAccount);
            taxPolicyChangeService.insertSelective(taxPolicyChangeEntity);
        }
}

    @Override
    public  List<TaxPolicySelectVO> queryTaxPolicySelectByParkId(Long parkId){
        return mapper.queryTaxPolicySelectByParkId(parkId);
    }

    @Override
    public PageInfo<IndustryPolicyVO> listPageIndustryRules(IndustryPolicyQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<IndustryPolicyVO> rules = mapper.listIndustryRules(query);
        if (CollectionUtil.isEmpty(rules)) {
            return new PageInfo(rules);
        }
        for (IndustryPolicyVO rule : rules) {
            List<TaxRatesRulesVO> list = taxRulesConfigMapper.queryByIndustryId(rule.getPolicyId(), rule.getIndustryId(), 1);
            rule.setRules(list);
        }
        return new PageInfo(rules);
    }

    @Override
    public TaxPolicyEntity queryTaxPolicyByParkId(Long parkId, Integer companyType,Integer taxpayerType) {
        return mapper.queryTaxPolicyByParkId(parkId, companyType,taxpayerType);
    }

    @Override
    public List<TaxPolicyEntity> queryByIncomeLevyType(String oemCode, Integer incomeLevyType) {
        return mapper.queryByIncomeLevyType(oemCode, incomeLevyType);
    }

}

