package com.yuqian.itax.yishui.service;


import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.entity.FastIssuingQueryResp;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;

import java.math.BigDecimal;

/**
 * @Description: 易税平台接口service
 * @Author: lmh
 * @Date: 2023/02/20
 */
public interface YiShuiService {

    /**
     * 获取易税接入参数配置
     * @param oemCode 机构编码
     * @param merchantCode 接入易税商户号
     * @return
     * @throws BusinessException
     */
    YsMerConfig getYsParamConfig(String oemCode, String merchantCode) throws BusinessException;

    /**
     * 获取token
     * @param oemCode
     * @return
     */
    String getToken(String oemCode) throws BusinessException;

    /**
     * 付款
     * @param oemCode
     * @return
     */
    YiShuiBaseResp pay(String oemCode, String orderNo, BigDecimal money, UserBankCardEntity userBankCardEntity, Long professionalId) throws BusinessException;

    /**
     * 付款结果查询
     * @param oemCode
     * @return
     */
    FastIssuingQueryResp payResultQuery(String oemCode, String orderNo) throws BusinessException;

    /**
     * 批次审核
     * @param oemCode
     * @param enterpriseOrderId
     */
    void changeOrderStatus(String oemCode, String enterpriseOrderId) throws BusinessException;

}
