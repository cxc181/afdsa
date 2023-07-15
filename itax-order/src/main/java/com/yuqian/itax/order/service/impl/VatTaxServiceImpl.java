package com.yuqian.itax.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yuqian.itax.agent.entity.CalculateEntity;
import com.yuqian.itax.agent.entity.dto.VatIncomeDTO;
import com.yuqian.itax.agent.entity.vo.VatIncomeDetailVO;
import com.yuqian.itax.agent.entity.vo.VatIncomeVO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.service.VatTaxService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.park.enums.TaxPolicyStatusEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.system.entity.BusinessIncomeRuleEntity;
import com.yuqian.itax.system.service.BusinessIncomeRuleService;
import com.yuqian.itax.user.enums.CompanyTaxPayerTypeEnum;
import com.yuqian.itax.util.util.IntervalUtil;
import com.yuqian.itax.util.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * 小程序计算器service impl
 *
 * @author yejian
 * @Date: 2020年11月12日 11:45:12
 */
@Slf4j
@Service("vatTaxService")
public class VatTaxServiceImpl implements VatTaxService {

    @Autowired
    private ParkService parkService;
    @Autowired
    private TaxPolicyService taxPolicyService;
    @Autowired
    private TaxRulesConfigService taxRulesConfigService;
    @Autowired
    private BusinessIncomeRuleService businessIncomeRuleService;

    @Override
    public VatIncomeVO calculateIncomeTax(String oemCode, VatIncomeDTO dto) throws BusinessException {
        log.info("计算所得税service开始：{}", JSON.toJSONString(dto));

        // 参数校验
        paramsValidation(dto);

        // 判断园区是否存在
        ParkEntity park = this.parkService.findById(dto.getParkId());
        if (null == park) {
            throw new BusinessException("所得税计算失败，园区不存在");
        }

        VatIncomeVO incomeVO = new VatIncomeVO();
        BigDecimal totalTax = new BigDecimal(0);// 所得税总计
        List<VatIncomeDetailVO> detailList = Lists.newArrayList();// 所得税明细

        // 组装所得税计算参数对象
        CalculateEntity entity = new CalculateEntity();
        entity.setParkId(dto.getParkId());
        entity.setCalType(dto.getCalType());
        entity.setCompanyType(dto.getCompanyType());
        entity.setInvoiceType(2);// 增值税发票默认专票
        entity.setHistoryTaxableIncome(0L);
        entity.setHistoryTaxes(0L);
        if (dto.getCalType() == 1) {// 按月
            // 按月计算：根据月均开票金额是否为空来判断计算方式
            if (dto.getMonthAvgAmount() != null) {
                // 循环取值
                // 计算所得税
                entity.setMonthAmount(dto.getMonthAvgAmount());
                Map<String, Object> taxMap = this.calculateIncomeTax(entity);
                for (int i = 1; i <= 12; i++) {
                    VatIncomeDetailVO detailVO = new VatIncomeDetailVO();
                    detailVO.setRate((BigDecimal) taxMap.get("rate"));
                    detailVO.setIncomeTax((BigDecimal) taxMap.get("taxAmount"));
                    detailVO.setIndex(i);
                    detailVO.setAmount(dto.getMonthAvgAmount());
                    detailList.add(detailVO);
                    totalTax = totalTax.add(detailVO.getIncomeTax());// 累计所得税
                }
            } else {
                // 月均开票金额为空，按具体每月的金额计算
                //第1个月
                VatIncomeDetailVO detail1 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getFirstMonthAmount());
                Map<String, Object> taxMap1 = this.calculateIncomeTax(entity);
                detail1.setIncomeTax((BigDecimal) taxMap1.get("taxAmount"));
                detail1.setRate((BigDecimal) taxMap1.get("rate"));
                detail1.setAmount(dto.getFirstMonthAmount());
                detail1.setIndex(1);
                detailList.add(detail1);

                //第2个月
                VatIncomeDetailVO detail2 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getSecondMonthAmount());
                Map<String, Object> taxMap2 = this.calculateIncomeTax(entity);
                detail2.setIncomeTax((BigDecimal) taxMap2.get("taxAmount"));
                detail2.setRate((BigDecimal) taxMap2.get("rate"));
                detail2.setAmount(dto.getSecondMonthAmount());
                detail2.setIndex(2);
                detailList.add(detail2);

                //第3个月
                VatIncomeDetailVO detail3 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getThirdMonthAmount());
                Map<String, Object> taxMap3 = this.calculateIncomeTax(entity);
                detail3.setIncomeTax((BigDecimal) taxMap3.get("taxAmount"));
                detail3.setRate((BigDecimal) taxMap3.get("rate"));
                detail3.setAmount(dto.getThirdMonthAmount());
                detail3.setIndex(1);
                detailList.add(detail3);

                //第4个月
                VatIncomeDetailVO detail4 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getFourthMonthAmount());
                Map<String, Object> taxMap4 = this.calculateIncomeTax(entity);
                detail4.setIncomeTax((BigDecimal) taxMap4.get("taxAmount"));
                detail4.setRate((BigDecimal) taxMap4.get("rate"));
                detail4.setAmount(dto.getFourthMonthAmount());
                detail4.setIndex(1);
                detailList.add(detail4);

                //第5个月
                VatIncomeDetailVO detail5 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getFifthMonthAmount());
                Map<String, Object> taxMap5 = this.calculateIncomeTax(entity);
                detail5.setIncomeTax((BigDecimal) taxMap5.get("taxAmount"));
                detail5.setRate((BigDecimal) taxMap5.get("rate"));
                detail5.setAmount(dto.getFifthMonthAmount());
                detail5.setIndex(5);
                detailList.add(detail5);

                //第6个月
                VatIncomeDetailVO detail6 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getSixthMonthAmount());
                Map<String, Object> taxMap6 = this.calculateIncomeTax(entity);
                detail6.setIncomeTax((BigDecimal) taxMap6.get("taxAmount"));
                detail6.setRate((BigDecimal) taxMap6.get("rate"));
                detail6.setAmount(dto.getSixthMonthAmount());
                detail6.setIndex(6);
                detailList.add(detail6);

                //第7个月
                VatIncomeDetailVO detail7 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getSeventhMonthAmount());
                Map<String, Object> taxMap7 = this.calculateIncomeTax(entity);
                detail7.setIncomeTax((BigDecimal) taxMap7.get("taxAmount"));
                detail7.setRate((BigDecimal) taxMap7.get("rate"));
                detail7.setAmount(dto.getSeventhMonthAmount());
                detail7.setIndex(7);
                detailList.add(detail7);

                //第8个月
                VatIncomeDetailVO detail8 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getEighthMonthAmount());
                Map<String, Object> taxMap8 = this.calculateIncomeTax(entity);
                detail8.setIncomeTax((BigDecimal) taxMap8.get("taxAmount"));
                detail8.setRate((BigDecimal) taxMap8.get("rate"));
                detail8.setAmount(dto.getEighthMonthAmount());
                detail8.setIndex(8);
                detailList.add(detail8);

                //第9个月
                VatIncomeDetailVO detail9 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getNinthMonthAmount());
                Map<String, Object> taxMap9 = this.calculateIncomeTax(entity);
                detail9.setIncomeTax((BigDecimal) taxMap9.get("taxAmount"));
                detail9.setRate((BigDecimal) taxMap9.get("rate"));
                detail9.setAmount(dto.getNinthMonthAmount());
                detail9.setIndex(9);
                detailList.add(detail9);

                //第10个月
                VatIncomeDetailVO detail10 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getTenthMonthAmount());
                Map<String, Object> taxMap10 = this.calculateIncomeTax(entity);
                detail10.setIncomeTax((BigDecimal) taxMap10.get("taxAmount"));
                detail10.setRate((BigDecimal) taxMap10.get("rate"));
                detail10.setAmount(dto.getTenthMonthAmount());
                detail10.setIndex(10);
                detailList.add(detail10);

                //第11个月
                VatIncomeDetailVO detail11 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getEleventhMonthAmount());
                Map<String, Object> taxMap11 = this.calculateIncomeTax(entity);
                detail11.setIncomeTax((BigDecimal) taxMap11.get("taxAmount"));
                detail11.setRate((BigDecimal) taxMap11.get("rate"));
                detail11.setAmount(dto.getEleventhMonthAmount());
                detail11.setIndex(11);
                detailList.add(detail11);

                //第12个月
                VatIncomeDetailVO detail12 = new VatIncomeDetailVO();
                // 计算所得税
                entity.setMonthAmount(dto.getTwelvethMonthAmount());
                Map<String, Object> taxMap12 = this.calculateIncomeTax(entity);
                detail12.setIncomeTax((BigDecimal) taxMap12.get("taxAmount"));
                detail12.setRate((BigDecimal) taxMap12.get("rate"));
                detail12.setAmount(dto.getTwelvethMonthAmount());
                detail12.setIndex(12);
                detailList.add(detail12);

                // 计算所得税总和
                totalTax = MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(detail1.getIncomeTax(), detail2.getIncomeTax()), MoneyUtil.moneyAdd(detail3.getIncomeTax(), detail4.getIncomeTax())), MoneyUtil.moneyAdd(detail5.getIncomeTax(), detail6.getIncomeTax())), MoneyUtil.moneyAdd(detail7.getIncomeTax(), detail8.getIncomeTax())), MoneyUtil.moneyAdd(detail9.getIncomeTax(), detail10.getIncomeTax())), MoneyUtil.moneyAdd(detail11.getIncomeTax(), detail12.getIncomeTax()));
            }
        } else if (dto.getCalType() == 2) {// 按季
            // 按季计算
            // 第一季度
            VatIncomeDetailVO detailFirst = new VatIncomeDetailVO();
            // 计算所得税
            entity.setSeasonAmount(dto.getFirstSeasonAmount());
            Map<String, Object> taxMap1 = this.calculateIncomeTax(entity);
            detailFirst.setIncomeTax((BigDecimal) taxMap1.get("taxAmount"));
            detailFirst.setRate((BigDecimal) taxMap1.get("rate"));
            detailFirst.setAmount(dto.getFirstSeasonAmount());
            detailFirst.setIndex(1);
            detailList.add(detailFirst);

            // 第二季度
            VatIncomeDetailVO detailSecond = new VatIncomeDetailVO();
            // 计算所得税
            entity.setSeasonAmount(dto.getSecondSeasonAmount());
            Map<String, Object> taxMap2 = this.calculateIncomeTax(entity);
            detailSecond.setIncomeTax((BigDecimal) taxMap2.get("taxAmount"));
            detailSecond.setRate((BigDecimal) taxMap2.get("rate"));
            detailSecond.setAmount(dto.getSecondSeasonAmount());
            detailSecond.setIndex(2);
            detailList.add(detailSecond);

            // 第三季度
            VatIncomeDetailVO detailThird = new VatIncomeDetailVO();
            // 计算所得税
            entity.setSeasonAmount(dto.getThirdSeasonAmount());
            Map<String, Object> taxMap3 = this.calculateIncomeTax(entity);
            detailThird.setIncomeTax((BigDecimal) taxMap3.get("taxAmount"));
            detailThird.setRate((BigDecimal) taxMap3.get("rate"));
            detailThird.setAmount(dto.getThirdSeasonAmount());
            detailThird.setIndex(3);
            detailList.add(detailThird);

            // 第四季度
            VatIncomeDetailVO detailFourth = new VatIncomeDetailVO();
            // 计算所得税
            entity.setSeasonAmount(dto.getFourthSeasonAmount());
            Map<String, Object> taxMap4 = this.calculateIncomeTax(entity);
            detailFourth.setIncomeTax((BigDecimal) taxMap4.get("taxAmount"));
            detailFourth.setRate((BigDecimal) taxMap4.get("rate"));
            detailFourth.setAmount(dto.getFourthSeasonAmount());
            detailFourth.setIndex(4);
            detailList.add(detailFourth);

            // 计算所得税总和
            totalTax = detailFirst.getIncomeTax().add(detailSecond.getIncomeTax()).add(detailThird.getIncomeTax()).add(detailFourth.getIncomeTax());
        } else {
            throw new BusinessException("所得税计算失败，未知的计算类型：【" + dto.getCalType() + "】");
        }

        // 设值并返回
        incomeVO.setTotalTax(totalTax);
        incomeVO.setVatIncomeDetailList(detailList);

        log.info("所得税计算结束：{}", JSON.toJSONString(incomeVO));
        return incomeVO;
    }

    /**
     * @Description 所得税计算参数校验
     * @Author Kaven
     * @Date 2020/3/30 11:45
     * @Param VatIncomeDTO
     * @Return
     * @Exception BusinessException
     */
    private void paramsValidation(VatIncomeDTO dto) throws BusinessException {
        if (dto.getCalType() == 1) {// 按月
            if (null == dto.getMonthAvgAmount()) {// 月均开票金额为空时，才校验具体每月开票金额
                if (null == dto.getFirstMonthAmount()) {
                    throw new BusinessException("第1月份预计开票金额不能为空");
                }
                if (null == dto.getSecondMonthAmount()) {
                    throw new BusinessException("第2月份预计开票金额不能为空");
                }
                if (null == dto.getThirdMonthAmount()) {
                    throw new BusinessException("第3月份预计开票金额不能为空");
                }
                if (null == dto.getFourthMonthAmount()) {
                    throw new BusinessException("第4月份预计开票金额不能为空");
                }
                if (null == dto.getFifthMonthAmount()) {
                    throw new BusinessException("第5月份预计开票金额不能为空");
                }
                if (null == dto.getSixthMonthAmount()) {
                    throw new BusinessException("第6月份预计开票金额不能为空");
                }
                if (null == dto.getSeventhMonthAmount()) {
                    throw new BusinessException("第7月份预计开票金额不能为空");
                }
                if (null == dto.getEighthMonthAmount()) {
                    throw new BusinessException("第8月份预计开票金额不能为空");
                }
                if (null == dto.getNinthMonthAmount()) {
                    throw new BusinessException("第9月份预计开票金额不能为空");
                }
                if (null == dto.getTenthMonthAmount()) {
                    throw new BusinessException("第10月份预计开票金额不能为空");
                }
                if (null == dto.getEleventhMonthAmount()) {
                    throw new BusinessException("第11月份预计开票金额不能为空");
                }
                if (null == dto.getTwelvethMonthAmount()) {
                    throw new BusinessException("第12月份预计开票金额不能为空");
                }
            }
        } else if (dto.getCalType() == 2) {// 按季
            if (null == dto.getFirstSeasonAmount()) {
                throw new BusinessException("第一季度预计开票金额不能为空");
            }
            if (null == dto.getSecondSeasonAmount()) {
                throw new BusinessException("第二季度预计开票金额不能为空");
            }
            if (null == dto.getThirdSeasonAmount()) {
                throw new BusinessException("第三季度预计开票金额不能为空");
            }
            if (null == dto.getFourthSeasonAmount()) {
                throw new BusinessException("第四季度预计开票金额不能为空");
            }
        } else {
            throw new BusinessException("计算失败，未知的计算类型");
        }
    }

    @Override
    public VatIncomeVO calculateVatTax(String oemCode, VatIncomeDTO dto) throws BusinessException {

        // 增值税发票校验发票类型
        if (null == dto.getInvoiceType()) {
            throw new BusinessException("发票类型不能为空");
        }
        // 参数校验
        paramsValidation(dto);

        // 判断园区是否存在
        ParkEntity park = this.parkService.findById(dto.getParkId());
        if (null == park) {
            throw new BusinessException("所得税计算失败，园区不存在");
        }

        VatIncomeVO incomeVO = new VatIncomeVO();
        BigDecimal totalTax = new BigDecimal(0);// 所得税总计
        List<VatIncomeDetailVO> detailList = Lists.newArrayList();// 所得税明细

        // 组装增值税计算参数对象
        CalculateEntity entity = new CalculateEntity();
        entity.setParkId(dto.getParkId());
        entity.setCalType(dto.getCalType());
        entity.setCompanyType(dto.getCompanyType());
        entity.setInvoiceType(dto.getInvoiceType());

        if (dto.getCalType() == 1) {// 按月
            // 按月计算：根据月均开票金额是否为空来判断计算方式
            if (dto.getMonthAvgAmount() != null) {
                // 循环取值
                // 计算增值税
                entity.setMonthAmount(dto.getMonthAvgAmount());
                Map<String, Object> taxMap = this.calculateVatTax(entity);
                for (int i = 1; i <= 12; i++) {
                    VatIncomeDetailVO detailVO = new VatIncomeDetailVO();
                    detailVO.setRate((BigDecimal) taxMap.get("rate"));
                    detailVO.setIncomeTax((BigDecimal) taxMap.get("taxAmount"));
                    detailVO.setIndex(i);
                    detailVO.setAmount(dto.getMonthAvgAmount());
                    detailVO.setInvoiceType(dto.getInvoiceType());
                    detailList.add(detailVO);
                    totalTax = totalTax.add(detailVO.getIncomeTax());// 累计所得税
                }
            } else {
                // 月均开票金额为空，按具体每月的金额计算
                //第1个月
                VatIncomeDetailVO detail1 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getFirstMonthAmount());
                Map<String, Object> taxMap1 = this.calculateVatTax(entity);
                detail1.setIncomeTax((BigDecimal) taxMap1.get("taxAmount"));
                detail1.setRate((BigDecimal) taxMap1.get("rate"));
                detail1.setAmount(dto.getFirstMonthAmount());
                detail1.setIndex(1);
                detail1.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail1);

                //第2个月
                VatIncomeDetailVO detail2 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getSecondMonthAmount());
                Map<String, Object> taxMap2 = this.calculateVatTax(entity);
                detail2.setIncomeTax((BigDecimal) taxMap2.get("taxAmount"));
                detail2.setRate((BigDecimal) taxMap2.get("rate"));
                detail2.setAmount(dto.getSecondMonthAmount());
                detail2.setIndex(2);
                detail2.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail2);

                //第3个月
                VatIncomeDetailVO detail3 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getThirdMonthAmount());
                Map<String, Object> taxMap3 = this.calculateVatTax(entity);
                detail3.setIncomeTax((BigDecimal) taxMap3.get("taxAmount"));
                detail3.setRate((BigDecimal) taxMap3.get("rate"));
                detail3.setAmount(dto.getThirdMonthAmount());
                detail3.setIndex(1);
                detail3.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail3);

                //第4个月
                VatIncomeDetailVO detail4 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getFourthMonthAmount());
                Map<String, Object> taxMap4 = this.calculateVatTax(entity);
                detail4.setIncomeTax((BigDecimal) taxMap4.get("taxAmount"));
                detail4.setRate((BigDecimal) taxMap4.get("rate"));
                detail4.setAmount(dto.getFourthMonthAmount());
                detail4.setIndex(1);
                detail4.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail4);

                //第5个月
                VatIncomeDetailVO detail5 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getFifthMonthAmount());
                Map<String, Object> taxMap5 = this.calculateVatTax(entity);
                detail5.setIncomeTax((BigDecimal) taxMap5.get("taxAmount"));
                detail5.setRate((BigDecimal) taxMap5.get("rate"));
                detail5.setAmount(dto.getFifthMonthAmount());
                detail5.setIndex(5);
                detail5.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail5);

                //第6个月
                VatIncomeDetailVO detail6 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getSixthMonthAmount());
                Map<String, Object> taxMap6 = this.calculateVatTax(entity);
                detail6.setIncomeTax((BigDecimal) taxMap6.get("taxAmount"));
                detail6.setRate((BigDecimal) taxMap6.get("rate"));
                detail6.setAmount(dto.getSixthMonthAmount());
                detail6.setIndex(6);
                detail6.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail6);

                //第7个月
                VatIncomeDetailVO detail7 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getSeventhMonthAmount());
                Map<String, Object> taxMap7 = this.calculateVatTax(entity);
                detail7.setIncomeTax((BigDecimal) taxMap7.get("taxAmount"));
                detail7.setRate((BigDecimal) taxMap7.get("rate"));
                detail7.setAmount(dto.getSeventhMonthAmount());
                detail7.setIndex(7);
                detail7.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail7);

                //第8个月
                VatIncomeDetailVO detail8 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getEighthMonthAmount());
                Map<String, Object> taxMap8 = this.calculateVatTax(entity);
                detail8.setIncomeTax((BigDecimal) taxMap8.get("taxAmount"));
                detail8.setRate((BigDecimal) taxMap8.get("rate"));
                detail8.setAmount(dto.getEighthMonthAmount());
                detail8.setIndex(8);
                detail8.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail8);

                //第9个月
                VatIncomeDetailVO detail9 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getNinthMonthAmount());
                Map<String, Object> taxMap9 = this.calculateVatTax(entity);
                detail9.setIncomeTax((BigDecimal) taxMap9.get("taxAmount"));
                detail9.setRate((BigDecimal) taxMap9.get("rate"));
                detail9.setAmount(dto.getNinthMonthAmount());
                detail9.setIndex(9);
                detail9.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail9);

                //第10个月
                VatIncomeDetailVO detail10 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getTenthMonthAmount());
                Map<String, Object> taxMap10 = this.calculateVatTax(entity);
                detail10.setIncomeTax((BigDecimal) taxMap10.get("taxAmount"));
                detail10.setRate((BigDecimal) taxMap10.get("rate"));
                detail10.setAmount(dto.getTenthMonthAmount());
                detail10.setIndex(10);
                detail10.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail10);

                //第11个月
                VatIncomeDetailVO detail11 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getEleventhMonthAmount());
                Map<String, Object> taxMap11 = this.calculateVatTax(entity);
                detail11.setIncomeTax((BigDecimal) taxMap11.get("taxAmount"));
                detail11.setRate((BigDecimal) taxMap11.get("rate"));
                detail11.setAmount(dto.getEleventhMonthAmount());
                detail11.setIndex(11);
                detail11.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail11);

                //第12个月
                VatIncomeDetailVO detail12 = new VatIncomeDetailVO();
                // 计算增值税
                entity.setMonthAmount(dto.getTwelvethMonthAmount());
                Map<String, Object> taxMap12 = this.calculateVatTax(entity);
                detail12.setIncomeTax((BigDecimal) taxMap12.get("taxAmount"));
                detail12.setRate((BigDecimal) taxMap12.get("rate"));
                detail12.setAmount(dto.getTwelvethMonthAmount());
                detail12.setIndex(12);
                detail12.setInvoiceType(dto.getInvoiceType());
                detailList.add(detail12);

                // 计算增值税总和
                totalTax = MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(MoneyUtil.moneyAdd(detail1.getIncomeTax(), detail2.getIncomeTax()), MoneyUtil.moneyAdd(detail3.getIncomeTax(), detail4.getIncomeTax())), MoneyUtil.moneyAdd(detail5.getIncomeTax(), detail6.getIncomeTax())), MoneyUtil.moneyAdd(detail7.getIncomeTax(), detail8.getIncomeTax())), MoneyUtil.moneyAdd(detail9.getIncomeTax(), detail10.getIncomeTax())), MoneyUtil.moneyAdd(detail11.getIncomeTax(), detail12.getIncomeTax()));
            }
        } else if (dto.getCalType() == 2) {// 按季
            // 按季计算
            // 第一季度
            VatIncomeDetailVO detailFirst = new VatIncomeDetailVO();
            // 计算增值税
            entity.setSeasonAmount(dto.getFirstSeasonAmount());
            Map<String, Object> taxMap1 = this.calculateVatTax(entity);
            detailFirst.setIncomeTax((BigDecimal) taxMap1.get("taxAmount"));
            detailFirst.setRate((BigDecimal) taxMap1.get("rate"));
            detailFirst.setAmount(dto.getFirstSeasonAmount());
            detailFirst.setIndex(1);
            detailFirst.setInvoiceType(dto.getInvoiceType());
            detailList.add(detailFirst);

            // 第二季度
            VatIncomeDetailVO detailSecond = new VatIncomeDetailVO();
            // 计算增值税
            entity.setSeasonAmount(dto.getSecondSeasonAmount());
            Map<String, Object> taxMap2 = this.calculateVatTax(entity);
            detailSecond.setIncomeTax((BigDecimal) taxMap2.get("taxAmount"));
            detailSecond.setRate((BigDecimal) taxMap2.get("rate"));
            detailSecond.setAmount(dto.getSecondSeasonAmount());
            detailSecond.setIndex(2);
            detailFirst.setInvoiceType(dto.getInvoiceType());
            detailList.add(detailSecond);

            // 第三季度
            VatIncomeDetailVO detailThird = new VatIncomeDetailVO();
            // 计算增值税
            entity.setSeasonAmount(dto.getThirdSeasonAmount());
            Map<String, Object> taxMap3 = this.calculateVatTax(entity);
            detailThird.setIncomeTax((BigDecimal) taxMap3.get("taxAmount"));
            detailThird.setRate((BigDecimal) taxMap3.get("rate"));
            detailThird.setAmount(dto.getThirdSeasonAmount());
            detailThird.setIndex(3);
            detailFirst.setInvoiceType(dto.getInvoiceType());
            detailList.add(detailThird);

            // 第四季度
            VatIncomeDetailVO detailFourth = new VatIncomeDetailVO();
            // 计算增值税
            entity.setSeasonAmount(dto.getFourthSeasonAmount());
            Map<String, Object> taxMap4 = this.calculateVatTax(entity);
            detailFourth.setIncomeTax((BigDecimal) taxMap4.get("taxAmount"));
            detailFourth.setRate((BigDecimal) taxMap4.get("rate"));
            detailFourth.setAmount(dto.getFourthSeasonAmount());
            detailFourth.setIndex(4);
            detailFirst.setInvoiceType(dto.getInvoiceType());
            detailList.add(detailFourth);

            // 计算增值税总和
            totalTax = detailFirst.getIncomeTax().add(detailSecond.getIncomeTax()).add(detailThird.getIncomeTax()).add(detailFourth.getIncomeTax());
        } else {
            throw new BusinessException("增值税计算失败，未知的计算类型：【" + dto.getCalType() + "】");
        }

        // 设值并返回
        incomeVO.setTotalTax(totalTax);
        incomeVO.setVatIncomeDetailList(detailList);
        return incomeVO;
    }

    @Override
    public Map<String, Object> calculateIncomeTax(CalculateEntity entity) {
        Map<String, Object> result = new HashMap<String, Object>();

        // 查询园区税费政策
        TaxPolicyEntity taxPolicy = new TaxPolicyEntity();
        taxPolicy.setParkId(entity.getParkId());
        taxPolicy.setCompanyType(entity.getCompanyType());
        taxPolicy.setStatus(TaxPolicyStatusEnum.ON_SHELF.getValue());
        taxPolicy.setTaxpayerType(CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue()); // 税费计算器税费政策默认小规模纳税人类型
        taxPolicy = taxPolicyService.selectOne(taxPolicy);
        if (null == taxPolicy) {
            throw new BusinessException("未查询到园区税费政策");
        }

        // 查询园区个人所得税税费规则
        Example example = new Example(TaxRulesConfigEntity.class);
        example.createCriteria().andEqualTo("parkId", entity.getParkId()).andEqualTo("taxType", 1).
                andEqualTo("companyType", entity.getCompanyType()).andIsNull("industryId");
        List<TaxRulesConfigEntity> incomeTaxRuleList = taxRulesConfigService.selectByExample(example);
        if (CollectionUtil.isEmpty(incomeTaxRuleList)) {
            throw new BusinessException("未查询到园区个人所得税税费规则");
        }

        // 个人所得税减免周期 1-按月 2-按季度
        BigDecimal invoiceAmount = new BigDecimal(0);
        if (Objects.equals(taxPolicy.getIncomeTaxBreaksCycle(), 1L)) {
            invoiceAmount = new BigDecimal(entity.getMonthAmount());
        } else if (Objects.equals(taxPolicy.getIncomeTaxBreaksCycle(), 2L)) {
            invoiceAmount = new BigDecimal(entity.getSeasonAmount());
        }

        // 计算增值税
        Map<String, Object> taxMap = calculateVatTax(entity);

        // 征收方式（1：核定征收率，2：核定应税所得率）[V2.5新增核定应税所得率]
        if (Objects.equals(taxPolicy.getLevyWay(), 1)) {
            // 初始化个人所得税金额阶梯区间和税率区间
            List<BigDecimal> charge = new LinkedList<BigDecimal>();
            List<BigDecimal> price = new LinkedList<BigDecimal>();

            //设置减免政策为第一区间
            charge.add(new BigDecimal(taxPolicy.getIncomeTaxBreaksAmount()));
            price.add(new BigDecimal(0));
            for (TaxRulesConfigEntity level : incomeTaxRuleList) {
                charge.add(MoneyUtil.moneyAdd(new BigDecimal(level.getMaxAmount()), new BigDecimal(taxPolicy.getIncomeTaxBreaksAmount())));
                price.add(MoneyUtil.moneydiv(level.getRate(), new BigDecimal("100")));
            }

            // 本次开票应纳税所得额(本次开票金额-本次开票增值税)
            BigDecimal incomeTaxable = MoneyUtil.moneySub(invoiceAmount, (BigDecimal) taxMap.get("taxAmount"));

            // 查询累计开票金额的费率区间
            BigDecimal incomeTaxableRate = IntervalUtil.getMoneyRate(incomeTaxable, charge, price);

            // 计算个人所得税费
            BigDecimal personalIncomeTax = MoneyUtil.moneyMul(incomeTaxable, incomeTaxableRate);

            // 获取税率、个人所得税费向上取整
            result.put("rate", incomeTaxableRate.multiply(new BigDecimal("100")));
            result.put("taxAmount", personalIncomeTax.setScale(0, BigDecimal.ROUND_UP));
        }
        if (Objects.equals(taxPolicy.getLevyWay(), 2)) {
            // 核定应税所得率只有一条，默认取第一条
            TaxRulesConfigEntity taxRule = incomeTaxRuleList.get(0);

            // 应税所得率
            BigDecimal taxableRate = MoneyUtil.moneydiv(taxRule.getRate(), new BigDecimal("100"));

            // 本次开票应纳税所得额 = (本次开票金额-本次开票增值税) * 应税所得率
            BigDecimal incomeTaxable = MoneyUtil.moneyMul(
                    MoneyUtil.moneySub(invoiceAmount, (BigDecimal) taxMap.get("taxAmount")), taxableRate);

//            // 年度应纳税所得额
//            Long yearIncomeTaxable = MoneyUtil.moneyMul(incomeTaxable, new BigDecimal(4)).longValue();
            // 年度应纳税所得额=本次开票应纳税所得额+本年历史累计应纳税所得额 V2.7
            Long yearIncomeTaxable = incomeTaxable.longValue() + entity.getHistoryTaxableIncome();
            // 根据经营所得税率表计算年度个人所得税
            Map<String, BigDecimal> calcResultMap = calcBussinessIncomeTax(yearIncomeTaxable);

//            // 计算本次开票所得税（年度个人所得税/4 - 累计历史存量所得税税费）
//            BigDecimal personalIncomeTax = MoneyUtil.moneydiv((BigDecimal) calcResultMap.get("personalIncomeTax"), new BigDecimal(4));
            // 本次开票所得税=(年度应纳税所得额*适用税率-速算扣除数)-年度已缴所得税税费 2.7
            BigDecimal personalIncomeTax = MoneyUtil.moneySub(calcResultMap.get("personalIncomeTax"), new BigDecimal(entity.getHistoryTaxes()));
            entity.setHistoryTaxableIncome(yearIncomeTaxable);
            entity.setHistoryTaxes((calcResultMap.get("personalIncomeTax")).setScale(0, BigDecimal.ROUND_UP).longValue());
            // 获取税率、个人所得税费向上取整
            result.put("rate", calcResultMap.get("rate"));
            result.put("taxAmount", personalIncomeTax.setScale(0, BigDecimal.ROUND_UP));
        }
        return result;
    }

    @Override
    public Map<String, Object> calculateVatTax(CalculateEntity entity) {
        Map<String, Object> result = new HashMap<String, Object>();

        // 查询园区税费政策
        TaxPolicyEntity taxPolicy = new TaxPolicyEntity();
        taxPolicy.setParkId(entity.getParkId());
        taxPolicy.setCompanyType(entity.getCompanyType());
        taxPolicy.setStatus(TaxPolicyStatusEnum.ON_SHELF.getValue());
        taxPolicy.setTaxpayerType(CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue()); // 税费计算器税费政策默认小规模纳税人类型
        taxPolicy = taxPolicyService.selectOne(taxPolicy);
        if (null == taxPolicy) {
            throw new BusinessException("未查询到园区税费政策");
        }

        // 查询园区增值税税率列表
        List<TaxRulesVatRateVO> vatRateList = taxRulesConfigService.queryTaxRulesVatRate(entity.getParkId(), entity.getCompanyType(), entity.getInvoiceType());
        if (CollectionUtils.isEmpty(vatRateList)) {
            throw new BusinessException("未查询到园区增值税税费规则");
        }

        // 增值税税率换算（默认取列表第一个）
        BigDecimal vatRate = MoneyUtil.moneydiv(vatRateList.get(0).getVatRate(), new BigDecimal("100"));

        // 增值税减免周期 1-按月 2-按季度
        BigDecimal invoiceAmount = new BigDecimal(0);
        if (Objects.equals(taxPolicy.getVatBreaksCycle(), 1)) {
            invoiceAmount = new BigDecimal(entity.getMonthAmount());
        } else if (Objects.equals(taxPolicy.getVatBreaksCycle(), 2)) {
            invoiceAmount = new BigDecimal(entity.getSeasonAmount());
        }

        // 判断历史存量开票金额是否在优惠政策内(大于则用本次开票金额乘以增值税费)
        if (MoneyUtil.moneyComp(invoiceAmount, new BigDecimal(taxPolicy.getVatBreaksAmount()))) {

            // 计算增值税费
            BigDecimal vatfee = MoneyUtil.moneyMul(MoneyUtil.moneydiv(invoiceAmount, MoneyUtil.moneyAdd(new BigDecimal(1), vatRate)), vatRate);

            // 获取税率、增值税费向上取整
            result.put("rate", vatRate);
            result.put("taxAmount", vatfee.setScale(0, BigDecimal.ROUND_UP));

        } else {

            //发票类型：1->普通发票；2-增值税发票(增值税发票无减免)
            if (entity.getInvoiceType() == 1) {
                result.put("rate", new BigDecimal(0));
                result.put("taxAmount", new BigDecimal(0));
            } else {

                // 计算增值税费
                BigDecimal vatfee = MoneyUtil.moneyMul(MoneyUtil.moneydiv(invoiceAmount, MoneyUtil.moneyAdd(new BigDecimal(1), vatRate)), vatRate);

                // 获取税率、增值税费向上取整
                result.put("rate", vatRate);
                result.put("taxAmount", vatfee.setScale(0, BigDecimal.ROUND_UP));
            }
        }
        return result;
    }

    /**
     * 经营所得个人所得税计算
     *
     * @param yearIncomeTaxable 年度应纳税所得额
     * @return Map
     */
    public Map<String, BigDecimal> calcBussinessIncomeTax(Long yearIncomeTaxable) throws BusinessException {
        Map<String, BigDecimal> resultMap = new HashMap<>();

        // 查询经营所得适用个人所得税税率规则列表
        BusinessIncomeRuleEntity busIncomeRule = new BusinessIncomeRuleEntity();
        List<BusinessIncomeRuleEntity> busIncomeRuleList = businessIncomeRuleService.select(busIncomeRule);
        if (CollectionUtil.isEmpty(busIncomeRuleList)) {
            throw new BusinessException("未查询到经营所得适用个人所得税税率规则");
        }

        // 根据税率规则进行计算（计算规则：应纳税所得额 * 适用税率 - 速算扣除数）
        if (yearIncomeTaxable >= busIncomeRuleList.get(0).getMinAmount() && yearIncomeTaxable <= busIncomeRuleList.get(0).getMaxAmount()) {
            resultMap.put("personalIncomeTax", MoneyUtil.moneyMul(new BigDecimal(yearIncomeTaxable), busIncomeRuleList.get(0).getRate()));
            resultMap.put("rate", busIncomeRuleList.get(0).getRate());

        } else if (yearIncomeTaxable > busIncomeRuleList.get(1).getMinAmount() && yearIncomeTaxable <= busIncomeRuleList.get(1).getMaxAmount()) {
            resultMap.put("personalIncomeTax", MoneyUtil.moneySub(
                    MoneyUtil.moneyMul(new BigDecimal(yearIncomeTaxable), busIncomeRuleList.get(1).getRate()),
                    new BigDecimal(busIncomeRuleList.get(1).getQuick())));
            resultMap.put("rate", busIncomeRuleList.get(1).getRate());

        } else if (yearIncomeTaxable > busIncomeRuleList.get(2).getMinAmount() && yearIncomeTaxable <= busIncomeRuleList.get(2).getMaxAmount()) {
            resultMap.put("personalIncomeTax", MoneyUtil.moneySub(
                    MoneyUtil.moneyMul(new BigDecimal(yearIncomeTaxable), busIncomeRuleList.get(2).getRate()),
                    new BigDecimal(busIncomeRuleList.get(2).getQuick())));
            resultMap.put("rate", busIncomeRuleList.get(2).getRate());

        } else if (yearIncomeTaxable > busIncomeRuleList.get(3).getMinAmount() && yearIncomeTaxable <= busIncomeRuleList.get(3).getMaxAmount()) {
            resultMap.put("personalIncomeTax", MoneyUtil.moneySub(
                    MoneyUtil.moneyMul(new BigDecimal(yearIncomeTaxable), busIncomeRuleList.get(3).getRate()),
                    new BigDecimal(busIncomeRuleList.get(3).getQuick())));
            resultMap.put("rate", busIncomeRuleList.get(3).getRate());

        } else if (yearIncomeTaxable > busIncomeRuleList.get(4).getMinAmount() && yearIncomeTaxable <= busIncomeRuleList.get(4).getMaxAmount()) {
            resultMap.put("personalIncomeTax", MoneyUtil.moneySub(
                    MoneyUtil.moneyMul(new BigDecimal(yearIncomeTaxable), busIncomeRuleList.get(4).getRate()),
                    new BigDecimal(busIncomeRuleList.get(4).getQuick())));
            resultMap.put("rate", busIncomeRuleList.get(4).getRate());
        }
        return resultMap;
    }
}
