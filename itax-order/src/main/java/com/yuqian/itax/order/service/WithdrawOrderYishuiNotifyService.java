package com.yuqian.itax.order.service;


import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.WithdrawOrderYishuiNotifyMapper;
import com.yuqian.itax.order.entity.WithdrawOrderYishuiNotifyEntity;

import java.util.Map;

/**
 * service
 * 
 * @Date: 2023年02月11日 10:04:49 
 * @author robb
 */
public interface WithdrawOrderYishuiNotifyService extends IBaseService<WithdrawOrderYishuiNotifyEntity, WithdrawOrderYishuiNotifyMapper> {

    /**
     * 保存易税回调订单
     * @param merchantCode
     * @param reqData
     */
    WithdrawOrderYishuiNotifyEntity save(String merchantCode, Map<String, String> reqData);
}

