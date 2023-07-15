package com.yuqian.itax.error.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.error.dao.ErrorInfoMapper;
import com.yuqian.itax.error.entity.ErrorInfoEntity;
import com.yuqian.itax.error.entity.query.ErrorInfoQuery;

/**
 * 错误信息service
 * 
 * @Date: 2019年12月08日 20:41:20 
 * @author 蒋匿
 */
public interface ErrorInfoService extends IBaseService<ErrorInfoEntity,ErrorInfoMapper> {

    /**
     * 分页查询错误信息
     * @param query
     * @return
     */
    PageInfo<ErrorInfoEntity> page(ErrorInfoQuery query);

    /**
     * 添加错误信息
     * @param clientType 客户端类型 1-接口  2-后台
     * @param modelName 模块名称
     * @param className 类名称
     * @param errorContent 错误信息
     * @param errorParamsInfo 错误参数信息
     * @param oemCode 机构编码
     * @param addUser 添加人
     */
    void addErrorInfo(Integer clientType, String modelName, String className, String errorContent, String errorParamsInfo, String oemCode, String addUser);
}

