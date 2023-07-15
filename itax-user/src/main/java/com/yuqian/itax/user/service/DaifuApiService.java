package com.yuqian.itax.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.user.entity.dto.PaidOrderDTO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;

import java.util.Map;

/**
 * @Description 代付API服务类
 * @Author  Kaven
 * @Date   2020/9/7 16:50
*/
public interface DaifuApiService {
    final static String PRODUCT_CODE_WJ  = "PRD00095";// 网金
    final static String PRODUCT_CODE_ZX  = "PRD00094";// 专线

    /**
     * @Description 代付账户明细查询
     * @Author  Kaven
     * @Date   2020/9/7 16:52
     * @Param   OemParamsEntity CorporateAccountCollectionRecordQuery
     * @Return  JSONArray
     * @Exception  BusinessException
    */
    JSONArray queryCardTransDetail(OemParamsEntity paramsEntity, CorporateAccountCollectionRecordQuery query) throws BusinessException;

    /**
     * @Description 代付账户余额查询
     * @Author  Kaven
     * @Date   2020/9/7 16:52
     * @Param   OemParamsEntity ComCorpAccQuery
     * @Return  JSONObject
     * @Exception  BusinessException
     */
    JSONObject queryCardBalance(OemParamsEntity paramsEntity, ComCorpAccQuery query) throws BusinessException;

    /**
     * @Description 代付银行卡(对公户提现)
     * @Author  Kaven
     * @Date   2020/9/9 10:24
     * @Param   paramsEntity PaidOrderDTO
     * @Return  JSONObject
     * @Exception  
    */
    JSONObject paidOrder(OemParamsEntity paramsEntity, PaidOrderDTO paidOrderDto) throws BusinessException;

    /**
     * @Description 代付银行卡订单查询
     * @Author  Kaven
     * @Date   2020/9/9 14:02
     * @Param   paramsEntity orderNo
     * @Return  JSONObject
     * @Exception  BusinessException
     */
    JSONObject queryPaidOrder(OemParamsEntity paramsEntity, String orderNo,String txnStffId) throws BusinessException;

    /**
     * @Description 电子发票申请开票
     * @Author  HZ
     * @Date   2020/9/27 14:02
     * @Param   paramsEntity map
     * @Return  JSONObject
     * @Exception  BusinessException
     */
    JSONObject applyInvoice(OemParamsEntity paramsEntity, Map<String,Object> map) throws BusinessException;

    /**
     * @Description 电子发票开票查询
     * @Author  HZ
     * @Date   2020/9/27 14:02
     * @Param   paramsEntity map
     * @Return  JSONObject
     * @Exception  BusinessException
     */
    JSONObject queryInvoiceInfo(OemParamsEntity paramsEntity, Map<String,Object> map) throws BusinessException;
}
