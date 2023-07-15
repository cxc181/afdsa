package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.LogisticsInfoEntity;
import com.yuqian.itax.order.dao.LogisticsInfoMapper;

import java.util.List;

/**
 * 开票快递信息记录service
 * 
 * @Date: 2020年02月13日 15:33:18 
 * @author 蒋匿
 */
public interface LogisticsInfoService extends IBaseService<LogisticsInfoEntity,LogisticsInfoMapper> {

    /**
     * 查询快递100并保存数据库
     */
    List<LogisticsInfoEntity> queryLogisticsInfoList(String courierCompanyName,String courierNumber,String orderNo,String userName);

    /**
     * 根据orderNo删除快递信息
     * @param orderNo
     */
    void deleteByOrderNo(String orderNo);
}

