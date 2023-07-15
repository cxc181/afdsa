package com.yuqian.itax.system.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.EncryptUtil;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OcrService;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度OCR识别service impl
 * @author yejian
 */
@Service("ocrService")
@Slf4j
public class OcrServiceImpl implements OcrService {

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public String idCardFront(OemParamsEntity paramsEntity, String imageBase64Str) {
        String result = "";
        try {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("requestId", System.currentTimeMillis());
            orderMap.put("image", imageBase64Str);
            orderMap.put("idCardSide", "front");
            log.info("data数据：" + JSONObject.toJSON(orderMap));

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", JSONUtil.toJsonStr(orderMap));
            pubParamMap.put("keyNum", "OCR");
            pubParamMap.put("timestamp", System.currentTimeMillis()+"");
            pubParamMap.put("version", "1.0.0");

            // 加解密方式
            String sign = null;
            if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
                pubParamMap.put("merNo", paramsEntity.getAccount());
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, paramsEntity.getSecKey());
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", paramsEntity.getAccount());
                sign = EncryptUtil.sm3sign(pubParamMap, paramsEntity.getSecKey());
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);
            log.info("平台请求数据:" + JSONObject.toJSON(pubParamMap));

            HttpResponse response = HttpRequest.post(paramsEntity.getUrl()).body(jsonParams, "application/json").charset("utf-8").execute();
            result = response.body();
            log.info("平台返回结果:{}",result);
        }catch (Exception e) {
            log.error(e.getMessage());
            log.error("身份证正面识别失败,请核实后提交!");
        }
        return result;
    }

    @Override
    public String idCardBack(OemParamsEntity paramsEntity, String imageBase64Str) {
        String result = "";
        try {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("requestId", System.currentTimeMillis());
            orderMap.put("image", imageBase64Str);
            orderMap.put("idCardSide", "back");
            log.info("data数据：" + JSONObject.toJSON(orderMap));

            // 请求渠道接口
            result = ChannelUtils.callApi(paramsEntity.getParamsValues(),orderMap,paramsEntity.getAccount(),paramsEntity.getSecKey(),paramsEntity.getUrl(),"ocr");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("身份证反面识别失败,请核实后提交!");
        }
        return result;
    }

    @Override
    public String ocrBusinessLicense(OemParamsEntity paramsEntity, String imageUrl) {
        String result = "";
        try {
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = params.getString("productCode");
            if (StringUtil.isEmpty(productCode)){
                throw  new BusinessException("营业执照ocr识别未配置productCode");
            }
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("requestId", System.currentTimeMillis());
            orderMap.put("productCode", productCode);
            orderMap.put("imageUrl", imageUrl);
            log.info("data数据：" + JSONObject.toJSON(orderMap));
            // 请求渠道接口
            result = ChannelUtils.callApi(paramsEntity.getParamsValues(),orderMap,paramsEntity.getAccount(),paramsEntity.getSecKey(),paramsEntity.getUrl(),"ocr");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("营业执照识别失败");
        }
        return result;
    }
}
