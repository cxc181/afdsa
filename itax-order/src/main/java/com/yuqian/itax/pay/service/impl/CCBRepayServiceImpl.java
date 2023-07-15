package com.yuqian.itax.pay.service.impl;

import cn.duobeila.channel.ccb.CCBYinqncSupport;
import cn.duobeila.channel.ccb.model.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.pay.entity.*;
import com.yuqian.itax.pay.service.CCBRepayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 建行代付接口
 * @author：pengwei
 * @Date：2019/10/22 14:20
 * @version：1.0
 */
@Slf4j
@Service("ccbRepayService")
public class CCBRepayServiceImpl implements CCBRepayService {

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
            if(StringUtils.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().indexOf("\"channel\":\"ccb\"")>-1){
                JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
                _6W1303Req req = new _6W1303Req();
                req.setCustId(jsonObject.getString("custId"));
                req.setUserId(jsonObject.getString("userId"));
                req.setPassword(jsonObject.getString("password"));
                req.setAccNo1(paramsEntity.getAccount());

                req.setUseofCode(jsonObject.getString("useOfCode"));//  zh000009 = 其他

                String bankAccount = repayDetailVO.getBankAccount();
                String bankNo = userBankCardService.getBankNoByBankAccount(bankAccount,oemCode);
                int startInex = batchNo.length()-16; //截图订单号的最后16位来进行代发
                req.setRequestSn(batchNo.substring(startInex));
                req.setAccNo2(bankAccount); //收款人账号
                req.setOtherName(repayDetailVO.getAccountName());  //收款人姓名
                req.setAmount(totalAmount);
                req.setUbankNo(bankNo);//303100000006  105100000017-建设银行  301290000007-交行  102100099996-工行
                if("105100000017".equals(bankNo)){
                    req.setUbankNo("");
                    req.setBillCode(jsonObject.getString("billCode")); //本行提现
                }else{
                    req.setBillCode(jsonObject.getString("inter_billCode")); //跨行提现
                }

                _6W1303Resp resp = CCBYinqncSupport._6W1303(req, paramsEntity.getUrl());
                String resultJson = JSON.toJSONString(resp);
                log.info("====建行代付出款返回结果："+resultJson);
                return JSON.parseObject(resultJson);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), "建行代付出款："+e);
        }
        return null;
    }

    @Override
    public JSONObject repayQuery(String oemCode, String batchNo,OemParamsEntity paramsEntity) {
        try {
            if (paramsEntity == null) {
                throw new BusinessException("机构参数未配置");
            }
            if(StringUtils.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().indexOf("\"channel\":\"ccb\"")>-1) {
                JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
                _6W1503Req req = new _6W1503Req();
                req.setCustId(jsonObject.getString("custId"));
                req.setUserId(jsonObject.getString("userId"));
                req.setPassword(jsonObject.getString("password"));
                req.setRequestSn(System.currentTimeMillis() + "");
                int startInex = batchNo.length()-16; //截图订单号的最后16位来进行处理
                req.setRequestSn1(batchNo.substring(startInex));

                _6W1503Resp resp = CCBYinqncSupport._6W1503(req, paramsEntity.getUrl());
                String resultJson = JSON.toJSONString(resp);
                log.info("====建行代付查询返回结果："+resultJson);
                return JSON.parseObject(resultJson);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), "建行代付查询："+e);
        }
        return null;
    }

    @Override
    public JSONObject merchantBalance(String oemCode,OemParamsEntity paramsEntity) {
        try {
            if (paramsEntity == null) {
                throw new BusinessException("机构参数未配置");
            }
            if(StringUtils.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().indexOf("\"channel\":\"ccb\"")>-1) {
                JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
                _6W0100Req req = new _6W0100Req();
                req.setCustId(jsonObject.getString("custId"));
                req.setUserId(jsonObject.getString("userId"));
                req.setPassword(jsonObject.getString("password"));
                req.setRequestSn(System.currentTimeMillis() + "");
                req.setAccNo(paramsEntity.getAccount());

                _6W0100Resp resp = CCBYinqncSupport._6W0100(req, paramsEntity.getUrl());
                String resultJson = JSON.toJSONString(resp);
                log.info("====建行商户账户返回结果："+resultJson);
                return JSON.parseObject(resultJson);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), "建行商户账户查询："+e);
        }
        return null;
    }
}