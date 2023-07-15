package com.yuqian.itax.order.service.impl;

import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.WithdrawOrderYishuiNotifyMapper;
import com.yuqian.itax.order.entity.WithdrawOrderYishuiNotifyEntity;
import com.yuqian.itax.order.service.WithdrawOrderYishuiNotifyService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("withdrawOrderYishuiNotifyService")
public class WithdrawOrderYishuiNotifyServiceImpl extends BaseServiceImpl<WithdrawOrderYishuiNotifyEntity, WithdrawOrderYishuiNotifyMapper> implements WithdrawOrderYishuiNotifyService {

    @Override
    public WithdrawOrderYishuiNotifyEntity save(String merchantCode, Map<String, String> reqData) {
        WithdrawOrderYishuiNotifyEntity entity = new WithdrawOrderYishuiNotifyEntity();
        entity.setMerchantCode(merchantCode);
        entity.setParamTotal(JSONUtil.toJsonStr(reqData));
        entity.setParamType(reqData.get("type"));
        entity.setParamResult(reqData.get("resoult"));
        mapper.insertSelective(entity);
        return entity;
    }
}

