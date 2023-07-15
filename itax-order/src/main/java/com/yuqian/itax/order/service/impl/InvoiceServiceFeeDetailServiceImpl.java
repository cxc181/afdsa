package com.yuqian.itax.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.dao.InvoiceServiceFeeDetailMapper;
import com.yuqian.itax.order.entity.InvoiceServiceFeeDetailEntity;
import com.yuqian.itax.order.entity.vo.InvoiceServiceFeeDetailVO;
import com.yuqian.itax.order.service.InvoiceServiceFeeDetailService;
import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.service.ChargeStandardService;
import com.yuqian.itax.product.service.DiscountActivityChargeStandardRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service("invoiceServiceFeeDetailService")
public class InvoiceServiceFeeDetailServiceImpl extends BaseServiceImpl<InvoiceServiceFeeDetailEntity,InvoiceServiceFeeDetailMapper> implements InvoiceServiceFeeDetailService {
    @Resource
    private ChargeStandardService chargeStandardService;
    @Autowired
    private DiscountActivityChargeStandardRelaService discountActivityChargeStandardRelaService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private ProductService productService;

    @Override
    public List<InvoiceServiceFeeDetailVO> countServiceDetail(Long memberId, Long companyId, Long productId, Long invoiceAmount, Long periodInvoiceAmount, String oemCode) {
        ArrayList<InvoiceServiceFeeDetailVO> list = Lists.newArrayList();
        if(periodInvoiceAmount == null || periodInvoiceAmount <1){
            periodInvoiceAmount = invoiceAmount;
        }
        Long all = periodInvoiceAmount;
        Long history = periodInvoiceAmount - invoiceAmount;

        // 企业查询
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(companyId)).orElseThrow(() -> new BusinessException("未查询到企业信息"));
        // 查询产品
        ProductEntity product = productService.findById(productId);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        //查询收费标准
        // 根据产品查询收费标准
        Map<String, Object> map = chargeStandardService.queryChargeStandards(product.getId(), company.getParkId(), memberId, company.getIndustryId());
        List<ChargeStandardEntity> chargeStandardList = JSONArray.parseArray(JSONObject.toJSONString(map.get("systemUsageCharge")), ChargeStandardEntity.class);
        if(chargeStandardList == null || chargeStandardList.size()<1){
            throw new BusinessException("未找到服务费收费配置");
        }
        InvoiceServiceFeeDetailVO detailVO = null;
        Long min = null;
        Long max = null;
        BigDecimal feeRate = null;
        Long phaseAmount = 0L;
        for (int i = 0; i < chargeStandardList.size(); i++) {
            detailVO = new InvoiceServiceFeeDetailVO();
            max = chargeStandardList.get(i).getChargeMax();
            min = chargeStandardList.get(i).getChargeMin();
            feeRate = chargeStandardList.get(i).getChargeRate();
            phaseAmount = 0L;

            if (all > max) {
                if (history <= min) {
                    phaseAmount = max - min;
                } else if (history > max){
                    phaseAmount = 0L;
                } else {
                    phaseAmount = max - history;
                }
            } else if (all > min && all <= max) {
                if (history <= min) {
                    phaseAmount = all - min;
                } else {
                    phaseAmount = invoiceAmount;
                }
            } else if (all < min) {
                phaseAmount = 0L;

            }

            detailVO.setPhaseAmount(phaseAmount);
            detailVO.setFeeRate(feeRate);
            detailVO.setFeeAmount(feeRate.divide(new BigDecimal(100)).multiply(new BigDecimal(phaseAmount)).setScale(0, BigDecimal.ROUND_UP).longValue());
            list.add(detailVO);
        }
        return list;
    }

    public List<InvoiceServiceFeeDetailVO> countServiceDetail(Long memberId, Long industryId, Long parkId,Integer productType,Long invoiceAmount, Long periodInvoiceAmount, String oemCode) {
        ArrayList<InvoiceServiceFeeDetailVO> list = Lists.newArrayList();
        if(periodInvoiceAmount == null || periodInvoiceAmount <1){
            periodInvoiceAmount = invoiceAmount;
        }
        Long all = periodInvoiceAmount;
        Long history = periodInvoiceAmount - invoiceAmount;

        //查询收费标准
        List<DiscountActivityChargeStandardRelaEntity> chargeStandardList =  discountActivityChargeStandardRelaService.discountActivityInvoiceChargeStandard(memberId,parkId,industryId,productType,oemCode);
        if(chargeStandardList == null || chargeStandardList.size()<1){
            return null;
        }
        InvoiceServiceFeeDetailVO detailVO = null;
        Long min = null;
        Long max = null;
        BigDecimal feeRate = null;
        Long phaseAmount = 0L;
        for (int i = 0; i < chargeStandardList.size(); i++) {
            detailVO = new InvoiceServiceFeeDetailVO();
            max = chargeStandardList.get(i).getChargeMax();
            min = chargeStandardList.get(i).getChargeMin();
            feeRate = chargeStandardList.get(i).getChargeRate();
            phaseAmount = 0L;
            if (all > max) {
                if (history <= min) {
                    phaseAmount = max - min;
                } else if (history > max){
                    phaseAmount = 0L;
                } else {
                    phaseAmount = max - history;
                }
            } else if (all > min && all <= max) {
                if (history <= min) {
                    phaseAmount = all - min;
                } else {
                    phaseAmount = invoiceAmount;
                }
            } else if (all < min) {
                phaseAmount = 0L;
            }
            detailVO.setPhaseAmount(phaseAmount);
            detailVO.setFeeRate(feeRate);
            detailVO.setFeeAmount(feeRate.divide(new BigDecimal(100)).multiply(new BigDecimal(phaseAmount)).setScale(0, BigDecimal.ROUND_UP).longValue());
            list.add(detailVO);
        }
        return list;
    }

    @Override
    public List<InvoiceServiceFeeDetailVO> findDetailByOrderNo(String orderNo) {
        return mapper.queryDetailByOrderNo(orderNo);
    }

    @Override
    public BigDecimal findLeastServiceFeeRate(String orderNo) {
        return mapper.queryLeastServiceFeeRate(orderNo);
    }
}

