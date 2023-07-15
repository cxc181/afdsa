package com.yuqian.itax.system.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.EncryptUtil;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.error.service.ErrorInfoService;
import com.yuqian.itax.system.entity.CreditCodeEntity;
import com.yuqian.itax.system.service.CreditCodeService;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 税务登记号查询service impl
 * @author yejian
 * @Date   2020/02/20 12:01
 */
@Service("creditCodeService")
@Slf4j
public class CreditCodeServiceImpl implements CreditCodeService {

    @Autowired
    private OemParamsService oemParamsService;

    /**
     * @Description 税务登记号查询
     * @author yejian
     * @Date   2020/02/20 12:01
     * @param keyWord 查询关键字
     * @Return String
     */
    @SneakyThrows
    @Override
    public CreditCodeEntity getCreditCode(String oemCode, String keyWord) throws BusinessException {
        CreditCodeEntity creditCode = new CreditCodeEntity();
        //读取税务登记号相关配置
        OemParamsEntity paramsEntity = oemParamsService.getParams(oemCode,9);
        if(null == paramsEntity){
            throw new BusinessException("未配置税务登记号相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // sendMsgUrl
        String sendMsgUrl = paramsEntity.getUrl();
        JSONObject jsonObject=JSONObject.parseObject(paramsEntity.getParamsValues());
        // 请求参数
        Map<String, Object> reqParamMap = new HashMap<>();
        if(jsonObject!=null && jsonObject.containsKey("productCode")) {
            reqParamMap.put("productCode", jsonObject.getString("productCode"));
        }
        reqParamMap.put("keyWord", keyWord);
        reqParamMap.put("requestId", System.currentTimeMillis() + "");
        log.info("data数据：" + JSONUtil.toJsonPrettyStr(reqParamMap));

        // 请求渠道接口
        String result = ChannelUtils.callApi(paramsEntity.getParamsValues(), reqParamMap, agentNo, signKey, sendMsgUrl, "CREDITCODE");

        // 处理返回结果
        if (StringUtils.isBlank(result)) {
            throw new BusinessException("税务登记号查询失败");
        }
        JSONObject resultObj = JSONObject.parseObject(result);

        if ("00".equals(resultObj.getString("code"))) {
            // 解析data
            JSONObject resultData = (JSONObject) resultObj.get("data");
            creditCode.setEname(resultData.getString("ename"));
            creditCode.setCreditCode(resultData.getString("creditCode"));
            creditCode.setAddress(resultData.getString("address"));
            creditCode.setEconKind(resultData.getString("econKind"));
            creditCode.setTel(resultData.getString("tel"));
            creditCode.setStatus(resultData.getString("status"));
            creditCode.setBank(resultData.getString("bank"));
            creditCode.setBankAccount(resultData.getString("bankAccount"));
            return creditCode;
        }else if("4064".equals(resultObj.getString("code"))){
            throw new BusinessException("没有查到该企业税号，若确认公司名称无误，请手动输入！");
        }else{
            throw new BusinessException("税号信息查询失败，请手动输入！");
        }
    }

}
