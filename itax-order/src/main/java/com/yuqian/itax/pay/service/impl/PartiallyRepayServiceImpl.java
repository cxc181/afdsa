package com.yuqian.itax.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.utils.SM2Utils;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.pay.entity.*;
import com.yuqian.itax.pay.service.PartiallyRepayService;

import com.yuqian.itax.util.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 北京代付接口
 * @author：pengwei
 * @Date：2019/10/22 14:20
 * @version：1.0
 */
@Slf4j
@Service("partiallyRepayService")
public class PartiallyRepayServiceImpl implements PartiallyRepayService {

    public static final String URL_REPAY = "repay";
    public static final String URL_REPAY_QUERY = "repayQuery";
    public static final String URL_APPLY_LIMIT = "applyLimit";
    public static final String URL_MERCHANT_BALANCE = "merchantBalance";

    @Autowired
    private OemParamsService oemParamsService;

    @Override
    public JSONObject repay(String oemCode, String batchNo, String totalAmount, String totalCount, String orderTime, List<RepayDetailVO> detailList, String notifyUrl,OemParamsEntity paramsEntity) throws BusinessException {
        try {
            RepayRequestVO request = new RepayRequestVO();
            request.setP1_merchantNo(paramsEntity.getAccount());
            request.setP2_batchNo(batchNo);
            request.setP3_totalAmount(totalAmount);
            request.setP4_totalCount(totalCount);
            request.setP5_orderTime(orderTime);
            request.setP6_detailJson(JSONObject.toJSONString(detailList));
            request.setP7_notifyUrl(notifyUrl);
            request.setP8_hmac(SecretKeyUtil.sign(request, paramsEntity.getSecKey()));
            return doPost(paramsEntity, URL_REPAY, JSONObject.toJSONString(request), "代付出款");
        } catch (Exception e) {
            log.error(e.getMessage(), "代付出款："+e);
        }
        return null;
    }

    @Override
    public JSONObject repayQuery(String oemCode, String batchNo, String orderNo, String orderTime) {
        try {
            OemParamsEntity params = oemParamsService.getParams(oemCode, OemParamsTypeEnum.REPAY.getValue());
            if (params == null) {
                throw new BusinessException("机构参数未配置");
            }
            if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
                throw new BusinessException("机构参数不可用");
            }
            RepayQueryRequestVO request = new RepayQueryRequestVO();
            request.setP1_merchantNo(params.getAccount());
            request.setP2_batchNo(batchNo);
            request.setP3_orderNo(orderNo);
            request.setP4_orderTime(orderTime);
            request.setP5_hmac(SecretKeyUtil.sign(request, params.getSecKey()));
            return doPost(params, URL_REPAY_QUERY, JSONObject.toJSONString(request), "代付查询");
        } catch (Exception e) {
            log.error(e.getMessage(), "代付查询："+e);
        }
        return null;
    }

    @Override
    public JSONObject applyLimit(String oemCode, String amount) {
        try {
            OemParamsEntity params = oemParamsService.getParams(oemCode, OemParamsTypeEnum.REPAY.getValue());
            if (params == null) {
                throw new BusinessException("机构参数未配置");
            }
            if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
                throw new BusinessException("机构参数不可用");
            }
            ApplyLimitRequestVO request = new ApplyLimitRequestVO();
            request.setP1_merchantNo(params.getAccount());
            if (StringUtils.isBlank(amount)) {
                request.setP2_amount("10000");
            } else {
                request.setP2_amount(amount);
            }
            request.setP3_hmac(SecretKeyUtil.sign(request, params.getSecKey()));
            return doPost(params, URL_APPLY_LIMIT, JSONObject.toJSONString(request), "代付申请额度");
        } catch (Exception e) {
            log.error(e.getMessage(), "代付额度申请："+e);
        }
        return null;
    }

    @Override
    public JSONObject merchantBalance(String oemCode) {
        try {
            OemParamsEntity params = oemParamsService.getParams(oemCode, OemParamsTypeEnum.REPAY.getValue());
            if (params == null) {
                throw new BusinessException("机构参数未配置");
            }
            if (!Objects.equals(params.getStatus(), OemStatusEnum.YES.getValue())) {
                throw new BusinessException("机构参数不可用");
            }
            MerchantBalanceRequestVO request = new MerchantBalanceRequestVO();
            request.setP1_merchantNo(params.getAccount());
            request.setP2_hmac(SecretKeyUtil.sign(request, params.getSecKey()));
            return doPost(params, URL_MERCHANT_BALANCE, JSONObject.toJSONString(request), "商户账户查询");
        } catch (Exception e) {
            log.error(e.getMessage(), "商户账户查询："+e);
        }
        return null;
    }

    public JSONObject doPost(OemParamsEntity params, String url, String jsonStr, String msg) throws Exception {
        log.info("{}，代付请求参数：{}", msg, jsonStr);
        //Map<String, String> paramMap = new HashMap<>();
        TreeMap<String, String> paramMap = new TreeMap<>();
        paramMap.put("merchantNo", params.getAccount());
        paramMap.put("data", SM2Utils.encrypt(Utils.hexToByte(params.getPublicKey()), jsonStr.getBytes()));
        url = params.getUrl() + url;
        String res = null;
        if (url.contains("https")) {
            res = HttpsUtil.httpMethodPost(url, paramMap, "UTF-8", false);
        } else {
            res = HttpClientUtil.doPost(url, paramMap, "UTF-8");
        }
        if (res == null) {
            return null;
        }
        if (res.indexOf("p1_resCode") != -1) {
            //此情况为商户密钥信息在平台不存在，无法进行签名加密，故返回的是明文返回。
            return null;
        }
        String resJson = new String(SM2Utils.decrypt(Utils.hexToByte(params.getPrivateKey()), Utils.hexToByte(res.trim())));
        log.info("{}，返回参数：{}", msg, resJson);
        return JSONObject.parseObject(resJson);
    }
}