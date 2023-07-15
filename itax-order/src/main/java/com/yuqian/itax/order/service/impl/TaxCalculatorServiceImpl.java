package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.dao.InvoiceOrderMapper;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.ParkRewardQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.entity.vo.TaxCalculatorParkVO;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.enums.TaxTypeEnum;
import com.yuqian.itax.park.service.ParkTaxRefundPolicyService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.system.entity.BusinessIncomeRuleEntity;
import com.yuqian.itax.system.entity.CompanyIncomeRuleEntity;
import com.yuqian.itax.system.service.BusinessIncomeRuleService;
import com.yuqian.itax.system.service.CompanyIncomeRuleService;
import com.yuqian.itax.user.enums.CompanyTaxPayerTypeEnum;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static com.yuqian.itax.util.util.Utils.byteToHex;


/**
 * 开票订单service impl
 *
 * @author yejian
 * @Date: 2019年12月07日 20:05:12
 */
@Slf4j
@Service("TaxCalculatorService")
public class TaxCalculatorServiceImpl extends BaseServiceImpl<InvoiceOrderEntity, InvoiceOrderMapper> implements TaxCalculatorService {

    @Autowired
    private TaxRulesConfigService taxRulesConfigService;
    @Autowired
    private BusinessIncomeRuleService businessIncomeRuleService;
    @Autowired
    private CompanyIncomeRuleService companyIncomeRuleService;
    @Autowired
    private ParkService parkService;

    /**
     * 增值税纳税人类型额度
     */
    private final Long VAT_TAXPAYER_EARNING_LIMIT = 500000000L;

    @Override
    public CalculateTaxVO calculateTax(CalculateTaxDTO dto) {
        CalculateTaxVO vo = new CalculateTaxVO();
        // 确定默认园区
        TaxCalculatorParkVO parkVO = new TaxCalculatorParkVO();
        List<TaxCalculatorParkVO> allParkList = parkService.getAllParkList(dto.getCompanyType(), dto.getOemCode());
        if (CollectionUtil.isEmpty(allParkList)) {
            throw new BusinessException("未查询到园区");
        }
        if (null == dto.getParkId()) {
            parkVO = allParkList.get(0);
            dto.setParkId(parkVO.getParkId());
        } else {
            List<TaxCalculatorParkVO> collect = allParkList.stream().filter(x -> x.getParkId() == dto.getParkId()).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(collect)) {
                throw new BusinessException("所选园区不可用");
            }
            parkVO = collect.get(0);
        }
        vo.setDefaultParkId(parkVO.getParkId());
        vo.setDefaultParkName(parkVO.getParkName());
        // 确定增值税纳税人类型
        if (dto.getAllEarning() > VAT_TAXPAYER_EARNING_LIMIT) {
            dto.setTaxpayerType(CompanyTaxPayerTypeEnum.GENERAL_TAXPAYER.getValue());
        } else {
            dto.setTaxpayerType(CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
        }

        // 计算增值税
        this.calculateVatFee(dto, vo);
        // 计算附加税
        this.calculateSurchargeFee(dto, vo);
        // 计算所得税
        this.calculateIncomeTaxFee(dto, vo);
        // 计算总税费
        vo.setAllTax(vo.getVatTax() + vo.getIncomeTax() + vo.getSurchargeTax());
        return vo;
    }

    /**
     * 计算增值税
     */
    private void calculateVatFee(CalculateTaxDTO dto, CalculateTaxVO vo){
        // 增值税税率
        BigDecimal vatRate = dto.getVatRate().divide(new BigDecimal("100"));
        // 销项收入（小规模纳税人即总收入低于500W企业仅专票需要缴纳增值税，一般纳税人且为有限合伙企业类型仅经营收入需要交纳增值税）
        Long earning = dto.getAllEarning();
        if (CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue().equals(dto.getTaxpayerType())) {
            if (null == dto.getSpecialInvoiceEarning()) {
                throw new BusinessException("专票收入为空");
            }
            if (null == dto.getPlainInvoiceEarning()) {
                throw new BusinessException("普票收入为空");
            }
            if (dto.getAllEarning() != dto.getPlainInvoiceEarning() + dto.getSpecialInvoiceEarning()) {
                throw new BusinessException("数据有误，专票收入与普票收入之和不等于总收入");
            }
            earning = dto.getSpecialInvoiceEarning();
            vatRate = new BigDecimal("0.03"); // 小规模纳税人企业专票增值税按3%纳税
            dto.setVatRate(vatRate.multiply(new BigDecimal("100")));
        } else if (CompanyTaxPayerTypeEnum.GENERAL_TAXPAYER.getValue().equals(dto.getTaxpayerType())
                && MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())) {
            if (null == dto.getManageEarning()) {
                throw new BusinessException("经营收入为空");
            }
            if (null == dto.getDividendEarning()) {
                throw new BusinessException("分红收入为空");
            }
            if (dto.getAllEarning() != dto.getManageEarning() + dto.getDividendEarning()) {
                throw new BusinessException("数据有误，经营收入与分红收入之和不等于总收入");
            }
            earning = dto.getManageEarning();
        }
        // 销项税
        Long outputTax = new BigDecimal(earning).multiply(vatRate).divide(BigDecimal.ONE.add(vatRate), 0, BigDecimal.ROUND_UP).longValue();
        // 进项税（一般纳税人即总收入超过500W企业专票成本无需缴纳增值税）
        Long inputTax = 0L;
        if (dto.getAllEarning() > VAT_TAXPAYER_EARNING_LIMIT) {
            if (null == dto.getSixPCSpecialInvCosting()) {
                throw new BusinessException("专票成本(税率6%)为空");
            }
            if (null == dto.getNinePCSpecialInvCosting()) {
                throw new BusinessException("专票成本(税率9%)为空");
            }
            if (null == dto.getThirteenPCSpecialInvCosting()) {
                throw new BusinessException("专票成本(税率13%)为空");
            }
            if (null == dto.getPlainInvoiceCosting()) {
                throw new BusinessException("普票成本为空");
            }
            if (null == dto.getNoInvoiceCosting()) {
                throw new BusinessException("无票支出(人力成本)为空");
            }
            if (dto.getAllCosting() != dto.getSixPCSpecialInvCosting() + dto.getNinePCSpecialInvCosting()
                    + dto.getThirteenPCSpecialInvCosting() + dto.getPlainInvoiceCosting() + dto.getNoInvoiceCosting()) {
                throw new BusinessException("总成本不等于各成本之和");
            }
            final BigDecimal sixPCVatRate = new BigDecimal("0.06");
            final BigDecimal ninePCVatRate = new BigDecimal("0.09");
            final BigDecimal thirteenPCVatRate = new BigDecimal("0.13");
            Long sixPCInputTax = new BigDecimal(dto.getSixPCSpecialInvCosting()).multiply(sixPCVatRate).divide(BigDecimal.ONE.add(sixPCVatRate), 0, BigDecimal.ROUND_UP).longValue();
            Long ninePCInputTax = new BigDecimal(dto.getNinePCSpecialInvCosting()).multiply(ninePCVatRate).divide(BigDecimal.ONE.add(ninePCVatRate), 0, BigDecimal.ROUND_UP).longValue();
            Long thirteenPCInputTax = new BigDecimal(dto.getThirteenPCSpecialInvCosting()).multiply(thirteenPCVatRate).divide(BigDecimal.ONE.add(thirteenPCVatRate), 0, BigDecimal.ROUND_UP).longValue();
            inputTax = sixPCInputTax + ninePCInputTax + thirteenPCInputTax;
        }
        // 增值税税费
        Long vatTax = outputTax - inputTax;

        vo.setVatTax(vatTax);
        vo.setVatRate(vatRate.multiply(new BigDecimal("100")));
    }

    /**
     * 计算附加税
     */
    private void calculateSurchargeFee(CalculateTaxDTO dto, CalculateTaxVO vo){
        // 查询附加税税率
        List<TaxRulesConfigVO> taxRulesConfigVOS = taxRulesConfigService.queryTaxRules(dto.getParkId(), TaxTypeEnum.SURCHARGE.getValue(), dto.getCompanyType(), null, dto.getTaxpayerType());
        if (CollectionUtil.isEmpty(taxRulesConfigVOS)) {
            throw new BusinessException("未查询到附加税税率");
        }
        TaxRulesConfigVO taxRulesConfigVO = taxRulesConfigVOS.get(0);
        // 增值税税费
        BigDecimal vatTax = new BigDecimal(vo.getVatTax());
        // 城建税税费
        Long urbanConstructionTax = vatTax.multiply(taxRulesConfigVO.getUrbanConstructionTaxRate().divide(new BigDecimal("100"))).setScale(0, BigDecimal.ROUND_UP).longValue();
        // 教育附加税税费
        Long educationSurchargeTax = vatTax.multiply(taxRulesConfigVO.getEducationSurchargeTaxRate().divide(new BigDecimal("100"))).setScale(0, BigDecimal.ROUND_UP).longValue();
        // 地方教育附加税税费
        Long localEducationSurcharge = vatTax.multiply(taxRulesConfigVO.getLocalEducationSurchargeRate().divide(new BigDecimal("100"))).setScale(0, BigDecimal.ROUND_UP).longValue();
        // 附加税税费
        Long surchargeTax = urbanConstructionTax + educationSurchargeTax + localEducationSurcharge;
        vo.setUrbanConstructionTax(urbanConstructionTax);
        vo.setUrbanConstructionTaxRate(taxRulesConfigVO.getUrbanConstructionTaxRate());
        vo.setEducationSurchargeTax(educationSurchargeTax);
        vo.setEducationSurchargeTaxRate(taxRulesConfigVO.getEducationSurchargeTaxRate());
        vo.setLocalEducationSurcharge(localEducationSurcharge);
        vo.setLocalEducationSurchargeRate(taxRulesConfigVO.getLocalEducationSurchargeRate());
        vo.setSurchargeTax(surchargeTax);
        vo.setVatAndSurchargeTax(vo.getVatTax() + surchargeTax);
    }

    /**
     * 计算所得税
     */
    private void calculateIncomeTaxFee(CalculateTaxDTO dto, CalculateTaxVO vo){
        // 所得税税费
        Long incomeTax = 0L;

        // 总收入
        Long allEarning = dto.getAllEarning();
        // 总成本
        Long allCosting = dto.getAllCosting();
        // 含税所得
        BigDecimal taxIncome = new BigDecimal(allEarning - allCosting);
        // 有限合伙企业个人所得税占比
        if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())) {
            if (null == dto.getPersonageEquityRatio() || dto.getPersonageEquityRatio().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("个人股权占比为空或数据有误");
            }
            if (null == dto.getCompanyEquityRatio() || dto.getCompanyEquityRatio().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("企业股权占比为空或数据有误");
            }
            if (new BigDecimal("100").compareTo(dto.getPersonageEquityRatio().add(dto.getCompanyEquityRatio())) != 0) {
                throw new BusinessException("个人股权占比与企业股权占比之和不等于100%");
            }
            taxIncome = taxIncome.multiply(dto.getPersonageEquityRatio().divide(new BigDecimal("100")));
        }
        // 所得税应纳税所得额
        BigDecimal taxableIncome = taxIncome.divide(BigDecimal.ONE.add(dto.getVatRate().divide(new BigDecimal("100"))), 0, BigDecimal.ROUND_UP);
        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        // 个人所得税计算
        if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(dto.getCompanyType())
                || MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())) {
            // 查询全国统一个税配置
            BusinessIncomeRuleEntity businessIncomeRule = businessIncomeRuleService.queryBusinessIncomeRuleByAmount(taxableIncome.longValue());
            if (null == businessIncomeRule) {
                throw new BusinessException("未查询到个人所得税税率配置");
            }
            incomeTax = taxableIncome.multiply(businessIncomeRule.getRate()).setScale(0, BigDecimal.ROUND_UP).longValue() - businessIncomeRule.getQuick();
            vo.setIncomeTax(incomeTax);
            vo.setIncomeTaxRate(businessIncomeRule.getRate().multiply(new BigDecimal("100")));
            vo.setPersonageIncomeTax(incomeTax);
            vo.setPersonageIncomeTaxRate(businessIncomeRule.getRate().multiply(new BigDecimal("100")));
        }

        if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(dto.getCompanyType())) {
            return;
        }

        // 有限合伙企业企业所得税占比
        if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())) {
            taxIncome = new BigDecimal(allEarning - allCosting);
            taxIncome = taxIncome.multiply(dto.getCompanyEquityRatio().divide(new BigDecimal("100")));
            taxableIncome = taxIncome.divide(BigDecimal.ONE.add(dto.getVatRate().divide(new BigDecimal("100"))), 0, BigDecimal.ROUND_UP);
            if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                taxableIncome = BigDecimal.ZERO;
            }
        }

        // 企业所得税计算
        // 查询全国统一企业所得税配置
        CompanyIncomeRuleEntity companyIncomeRule = companyIncomeRuleService.queryCompanyIncomeRuleByAmount(taxableIncome.longValue());
        if (null == companyIncomeRule) {
            throw new BusinessException("未查询到企业所得税税率配置");
        }
        BigDecimal million = new BigDecimal("100000000");
        if (companyIncomeRule.getLevel() == 2) {
            taxableIncome = taxableIncome.subtract(million);
        }
        incomeTax = taxableIncome.multiply(companyIncomeRule.getRate()).setScale(0, BigDecimal.ROUND_UP).longValue();
        if (companyIncomeRule.getLevel() == 2) {
            incomeTax = incomeTax + million.multiply(new BigDecimal("0.025")).longValue();
        }

        vo.setCompanyIncomeTax(incomeTax);
        vo.setCompanyIncomeTaxRate(companyIncomeRule.getRate().multiply(new BigDecimal("100")));

        if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())) {
            incomeTax = incomeTax + vo.getIncomeTax();
        }
        vo.setIncomeTax(incomeTax);
        vo.setIncomeTaxRate(companyIncomeRule.getRate().multiply(new BigDecimal("100")));
    }

    @Override
    public List<ParkRewardVO> queryParkReward(ParkRewardQuery query) {
        query.setAllTax(query.getVatTax() + query.getIncomeTax());
        String[] split = query.getParkIds().split(",");
        query.setParkIdList(Lists.newArrayList(split));
        List<ParkRewardVO> list = mapper.queryParkReward(query);
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }
        ParkRewardVO parkRewardVO = new ParkRewardVO();
        parkRewardVO.setAllAward(0L);
        parkRewardVO.setIncomeTaxAward(0L);
        parkRewardVO.setVatAndSurchargeAward(0L);
        list.add(parkRewardVO);
        return list;
    }

    @Override
    public Map<String, String> customSharing(String jsApiTicket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonceStr = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String string1;
        String signature = "";

        //这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsApiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsApiTicket", jsApiTicket);
        ret.put("nonceStr", nonceStr);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

}