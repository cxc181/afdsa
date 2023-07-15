package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.OperationLogEntity;
import com.yuqian.itax.system.dao.OperationLogMapper;

/**
 * 操作日志service
 * 
 * @Date: 2019年12月08日 20:36:31 
 * @author 蒋匿
 */
public interface OperationLogService extends IBaseService<OperationLogEntity,OperationLogMapper> {
    /**
     * 添加操作日志
     * @param oprType 客户端类型 1-接口 2-后台
     * @param ipAddr ip地址
     * @param className 类名称
     * @param method 方法
     * @param result 操作结果
     * @param oemCode
     * @param token
     */
    void addOperationLog(int oprType, String ipAddr, String className, String method, String result, String oemCode, String token);
}

