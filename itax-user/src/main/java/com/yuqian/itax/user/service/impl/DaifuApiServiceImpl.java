package com.yuqian.itax.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.capital.entity.vo.WithdrawDetailVO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.dto.PaidOrderDTO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.DaifuApiService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("daifuApiService")
public class DaifuApiServiceImpl implements DaifuApiService {

    @Override
    public JSONArray queryCardTransDetail(OemParamsEntity paramsEntity, CorporateAccountCollectionRecordQuery query) throws BusinessException {
        log.info("查询代付账户明细:{}", JSON.toJSONString(query));


        JSONArray jsonArray = null;
        // 调用渠道接口实时查询账户明细信息
        try {
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("productCode",productCode);
            dataMap.put("txnStffId",query.getTxnStffId());
            dataMap.put("draweeAccountNo",query.getDraweeAccountNo());
            dataMap.put("dbtCrDrcCode","0");
            if(StringUtils.isNotBlank(query.getStartDate())&&StringUtils.isNotBlank(query.getEndDate())){
                String startDate = query.getStartDate();
                dataMap.put("startDate",DateUtil.format(DateUtil.parseDate(startDate,"yyyy-MM-dd HH:mm:ss"),"yyyyMMdd"));
                String endDate = query.getEndDate();
                dataMap.put("endDate",DateUtil.format(DateUtil.parseDate(endDate,"yyyy-MM-dd HH:mm:ss"),"yyyyMMdd"));
            }
            if(query.getPageNumber()>0&&query.getPageSize()>0){
                dataMap.put("pageSize",100);
                dataMap.put("pageNo",query.getPageNumber());
            }


            //dataMap.put("cntprtAccNo",query.getOtherPartyBankNumber());
            JSONObject resultObj = ChannelUtils.callApi2Md5(dataMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
            if (null != resultObj && "00".equals(resultObj.getString("bizCode"))) {
                jsonArray = JSONArray.parseArray(resultObj.getString("flowList"));
            }
        } catch (Exception e) {
            log.error("查询代付账户明细发生异常:" + e.getMessage());
            throw new BusinessException("查询代付账户明细发生异常:" + e.getMessage());
        }
        return jsonArray;
    }

    @Override
    public JSONObject queryCardBalance(OemParamsEntity paramsEntity, ComCorpAccQuery query) throws BusinessException {
        log.info("查询代付账户余额:{}", JSON.toJSONString(query));

        JSONObject resultObj = null;
        try {
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("productCode",productCode);
            dataMap.put("txnStffId",query.getTxnStffId());
            dataMap.put("draweeAccountNo",query.getDraweeAccountNo());

            resultObj = ChannelUtils.callApi2Md5(dataMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
        } catch (Exception e) {
            log.error("查询账户余额发生异常:" + e.getMessage());
            throw new BusinessException("查询账户余额发生异常:" + e.getMessage());
        }
        return resultObj;
    }

    @Override
    public JSONObject paidOrder(OemParamsEntity paramsEntity, PaidOrderDTO paidOrderDto) throws BusinessException {
        log.info("代付银行卡请求:{},{}",JSON.toJSONString(paramsEntity), JSON.toJSONString(paidOrderDto));

        JSONObject jsonObj = null;
        try {
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("orderNo",paidOrderDto.getOrderNo());
            dataMap.put("amount",paidOrderDto.getAmount());
            dataMap.put("productCode",productCode);
            dataMap.put("payeeName",paidOrderDto.getPayeeName());
            dataMap.put("payeeCardno",paidOrderDto.getPayeeCardNo());
            dataMap.put("payeeBankName",paidOrderDto.getPayeeBankName());
            dataMap.put("draweeAccountNo",paidOrderDto.getDraweeAccountNo());
            dataMap.put("txnStffId",paidOrderDto.getTxnStffId());
            dataMap.put("entrstPrjId",paidOrderDto.getEntrstPrjId());
            dataMap.put("prjUseId",paidOrderDto.getPrjUserId());
            jsonObj = ChannelUtils.callApi2Md5(dataMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
        } catch (Exception e) {
            log.error("代付银行卡请求发生异常:" + e.getMessage());
            throw new BusinessException("代付银行卡请求发生异常:" + e.getMessage());
        }
        return jsonObj;
    }

    @Override
    public JSONObject queryPaidOrder(OemParamsEntity paramsEntity, String orderNo,String txnStffId) throws BusinessException {
        log.info("代付银行卡订单查询:{},{}", JSON.toJSONString(paramsEntity),orderNo);
        JSONObject resultObj = null;
        try {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("txnStffId",txnStffId);
            dataMap.put("orderNo",orderNo);
            resultObj = ChannelUtils.callApi2Md5(dataMap,paramsEntity.getAccount(),paramsEntity.getPublicKey(),paramsEntity.getSecKey(),paramsEntity.getUrl());
        } catch (Exception e) {
            log.error("代付银行卡订单查询发生异常:" + e.getMessage());
            throw new BusinessException("代付银行卡订单查询发生异常:" + e.getMessage());
        }
        return resultObj;
    }

    @Override
    public JSONObject applyInvoice(OemParamsEntity paramsEntity, Map<String, Object> map) throws BusinessException {
        log.info("渠道申请开票:{}", JSON.toJSONString(map));

        String resultObj = null;
        JSONObject json=null;
        try {
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("productCode",productCode);
            dataMap.put("orderNo",map.get("orderNo"));
            dataMap.put("type",map.get("type"));
            dataMap.put("companyName",map.get("companyName"));
            dataMap.put("amt",map.get("amt"));
            dataMap.put("taxfreeAmt",map.get("taxfreeAmt"));
            dataMap.put("buyer",map.get("buyer"));
            dataMap.put("taxnum",map.get("taxnum"));
            dataMap.put("email",map.get("email"));
            dataMap.put("clerk",map.get("clerk"));
            dataMap.put("billType",map.get("billType"));
            dataMap.put("details",map.get("details"));
            resultObj = ChannelUtils.callApi(paramsEntity.getParamsValues(),dataMap, paramsEntity.getAccount(), paramsEntity.getSecKey(),paramsEntity.getUrl(),"INVOICE");
            json= JSONObject.parseObject(resultObj);
        } catch (Exception e) {
            log.error("渠道申请开票发生异常:" + e.getMessage());
            throw new BusinessException("渠道申请开票发生异常:" + e.getMessage());
        }

        return json;
    }

    @Override
    public JSONObject queryInvoiceInfo(OemParamsEntity paramsEntity, Map<String, Object> map) throws BusinessException {
        log.info("渠道开票查询:{}", JSON.toJSONString(map));

        String resultObj = null;
        JSONObject json=null;
        try {
            JSONObject jsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
            String productCode = jsonObject.getString("productCode");
            if(StringUtils.isBlank(productCode)){
                throw new BusinessException("渠道产品编码配置错误！");
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("orderNo",map.get("orderNo"));
            resultObj = ChannelUtils.callApi(paramsEntity.getParamsValues(),dataMap, paramsEntity.getAccount(), paramsEntity.getSecKey(),paramsEntity.getUrl(),"INVOICEQUERY");
            json= JSONObject.parseObject(resultObj);
        } catch (Exception e) {
            log.error("渠道开票查询发生异常:" + e.getMessage());
            throw new BusinessException("渠道开票查询发生异常:" + e.getMessage());
        }
        return json;
    }


}
