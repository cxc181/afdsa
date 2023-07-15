package com.yuqian.itax.agent.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.agent.entity.ApiRequestMessageRecordEntity;
import com.yuqian.itax.agent.dao.ApiRequestMessageRecordMapper;

/**
 * api接口请求报文记录service
 * 
 * @Date: 2020年07月20日 17:44:57 
 * @author 蒋匿
 */
public interface ApiRequestMessageRecordService extends IBaseService<ApiRequestMessageRecordEntity,ApiRequestMessageRecordMapper> {

    /**
     * @Description 保存api接口请求报文记录
     * @Author  Kaven
     * @Date   2020/7/21 09:52
     * @Param   oemCode
     * @Return
     * @Exception
    */
    void addRequestRecord(String oemCode, String urlAddress, String sign, String version, String resultMessage, String reqParams);
}

