package com.yuqian.itax.product.service.impl;

import com.google.common.collect.Lists;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.product.dao.ChargeStandardMapper;
import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ChargeStandardService;
import com.yuqian.itax.product.service.DiscountActivityChargeStandardRelaService;
import com.yuqian.itax.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service("chargeStandardService")
public class ChargeStandardServiceImpl extends BaseServiceImpl<ChargeStandardEntity,ChargeStandardMapper> implements ChargeStandardService {
    @Resource
    private ChargeStandardMapper chargeStandardMapper;

    @Autowired
    private ProductService productService;
    @Autowired
    private DiscountActivityChargeStandardRelaService discountActivityChargeStandardRelaService;

    @Override
    public List<ChargeStandardEntity> getChargeStandards(Long productId, Long parkId, int includeParkPricing) throws BusinessException {
        // 查询产品是否存在
        ProductEntity product = this.productService.findById(productId);
        if(null == product){
            throw new BusinessException("获取收费标准失败，产品不存在！");
        }
        return this.chargeStandardMapper.getChargeStandards(productId, parkId, includeParkPricing);
    }

    @Override
    public Map<String, Object> queryChargeStandards(Long productId, Long parkId, Long memberId, Long industryId) throws BusinessException {
        // 返回结果集
        Map<String,Object> dataMap = new HashMap<String,Object>();
        // 开票产品ID
        Long invoiceProdId = null;
        ProductEntity product = this.productService.findById(productId);
        if(null == product){
            throw new BusinessException("获取收费标准失败，产品不存在！");
        }
        // 产品转换
        ProductEntity prod = null;
        if(ProductTypeEnum.INDIVIDUAL.getValue().equals(product.getProdType())){
            ProductEntity t = new ProductEntity();
            t.setProdType(ProductTypeEnum.INDIVIDUAL_INVOICE.getValue());
            t.setOemCode(product.getOemCode());
            t.setStatus(ProductStatusEnum.ON_SHELF.getValue());
            prod = this.productService.selectOne(t);
            invoiceProdId = prod.getId();
        }else if(ProductTypeEnum.INDEPENDENTLY.getValue().equals(product.getProdType())){
            ProductEntity t = new ProductEntity();
            t.setProdType(ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue());
            t.setOemCode(product.getOemCode());
            t.setStatus(ProductStatusEnum.ON_SHELF.getValue());
            prod = this.productService.selectOne(t);
            invoiceProdId = prod.getId();
        }else if(ProductTypeEnum.LIMITED_PARTNER.getValue().equals(product.getProdType())){
            ProductEntity t = new ProductEntity();
            t.setProdType(ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue());
            t.setOemCode(product.getOemCode());
            t.setStatus(ProductStatusEnum.ON_SHELF.getValue());
            prod = this.productService.selectOne(t);
            invoiceProdId = prod.getId();
        }else if(ProductTypeEnum.LIMITED_LIABILITY.getValue().equals(product.getProdType())){
            ProductEntity t = new ProductEntity();
            t.setProdType(ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue());
            t.setOemCode(product.getOemCode());
            t.setStatus(ProductStatusEnum.ON_SHELF.getValue());
            prod = this.productService.selectOne(t);
            invoiceProdId = prod.getId();
        }

        // 查询开票收费标准
        List<ChargeStandardEntity> list = Lists.newArrayList();
        // 查询开票特价活动
        int productType = null == prod ? ProductTypeEnum.INDIVIDUAL_INVOICE.getValue() : prod.getProdType();
        List<DiscountActivityChargeStandardRelaEntity> chargeStandardList =  discountActivityChargeStandardRelaService.discountActivityInvoiceChargeStandard(memberId,parkId,industryId,productType,product.getOemCode());
        if(CollectionUtil.isNotEmpty(chargeStandardList)){
            ObjectUtil.copyListObject(chargeStandardList, list, ChargeStandardEntity.class);
        } else {
            list = this.getChargeStandards(null == invoiceProdId ? productId : invoiceProdId, parkId, 1);
        }
        ProductEntity productOfCancel = Optional.ofNullable(productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL_CANCEL.getValue(), product.getOemCode(), parkId)).orElseThrow(() -> new BusinessException("未查询到企业注销产品信息"));
        // 收费标准
        dataMap.put("systemUsageCharge",list);// 平台根据发票金额收取的系统使用费
        dataMap.put("openAccountCharge",product.getProdAmount());// 企业开户费
        dataMap.put("cancelAccountCharge", productOfCancel.getProdAmount()); // 企业注销费
        dataMap.put("cancelTotalLimit", productOfCancel.getCancelTotalLimit()); // 注销累计开票额度
        return dataMap;
    }

    @Override
    public List<ChargeStandardEntity> getChargeStandardsByParkProductId(Long parkProductId) {
        return mapper.getChargeStandardsByParkProductId(parkProductId);
    }
}

