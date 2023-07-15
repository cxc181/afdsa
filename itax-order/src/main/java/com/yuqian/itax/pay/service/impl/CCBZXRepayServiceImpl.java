package com.yuqian.itax.pay.service.impl;

import cn.duobeila.channel.ccb.CCBYinqncSupport;
import cn.duobeila.channel.ccb.model.*;
import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.pay.entity.RepayDetailVO;
import com.yuqian.itax.pay.service.CCBRepayService;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 建行专线代付接口
 * @author：pengwei
 * @Date：2019/10/22 14:20
 * @version：1.0
 */
@Slf4j
@Service("ccbZXRepayService")
public class CCBZXRepayServiceImpl implements CCBRepayService {

    @Autowired
    private UserBankCardService userBankCardService;

    @Override
    public JSONObject repay(String oemCode, String batchNo, String totalAmount, RepayDetailVO repayDetailVO,OemParamsEntity paramsEntity) throws BusinessException {
        try {
            if (paramsEntity == null) {
                throw new BusinessException("机构参数未配置");
            }
            if(repayDetailVO == null ){
                throw new BusinessException("收款人信息不可用");
            }
            if(StringUtils.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().indexOf("\"channel\":\"ccbzx\"")>-1){
                JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
                String productCode = jsonObject.getString("productCode");
                if(StringUtils.isBlank(productCode)){
                    throw new BusinessException("渠道产品编码配置错误！");
                }
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("amount", new BigDecimal(totalAmount).multiply(new BigDecimal("100")).longValue());
                orderMap.put("orderNo", batchNo);
                orderMap.put("productCode",productCode);
                orderMap.put("payeeName", repayDetailVO.getAccountName());
                orderMap.put("payeeCardno", repayDetailVO.getBankAccount());
//                orderMap.put("payeeMobile", "13975117651");
                orderMap.put("payeeBankName", repayDetailVO.getBankName());
                String bankNo = userBankCardService.getBankNoByBankAccount( repayDetailVO.getBankAccount(),oemCode);
                orderMap.put("payeeBankCode", bankNo);
                orderMap.put("postscript", "");
                orderMap.put("notifyUrl", "");
                return ChannelUtils.callApi2Md5(orderMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), "建行专线代付出款："+e);
        }
        return null;
    }

    @Override
    public JSONObject repayQuery(String oemCode, String batchNo,OemParamsEntity paramsEntity) {
        try {
            if (paramsEntity == null) {
                throw new BusinessException("机构参数未配置");
            }
            if(StringUtils.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().indexOf("\"channel\":\"ccbzx\"")>-1) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("orderNo", batchNo);

                return ChannelUtils.callApi2Md5(orderMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), "建行专线代付查询："+e);
        }
        return null;
    }

    @Override
    public JSONObject merchantBalance(String oemCode,OemParamsEntity paramsEntity) {
        try {
            if (paramsEntity == null) {
                throw new BusinessException("机构参数未配置");
            }
            if(StringUtils.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().indexOf("\"channel\":\"ccbzx\"")>-1) {
                JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
                String productCode = jsonObject.getString("productCode");
                if(StringUtils.isBlank(productCode)){
                    throw new BusinessException("渠道产品编码配置错误！");
                }
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("productCode",productCode);

               return ChannelUtils.callApi2Md5(orderMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), "建行专线商户账户查询："+e);
        }
        return null;
    }
}