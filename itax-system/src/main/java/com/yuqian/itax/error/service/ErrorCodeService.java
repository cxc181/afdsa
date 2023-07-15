package com.yuqian.itax.error.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.error.entity.ErrorCodeEntity;
import com.yuqian.itax.error.dao.ErrorCodeMapper;
import com.yuqian.itax.error.entity.query.ErrorCodeQuery;

/**
 * 错误代码管理service
 * 
 * @Date: 2019年12月08日 20:41:09 
 * @author 蒋匿
 */
public interface ErrorCodeService extends IBaseService<ErrorCodeEntity,ErrorCodeMapper> {

    /**
     * 分页查询错误代码
     * @param query
     * @return
     */
    PageInfo<ErrorCodeEntity> page(ErrorCodeQuery query);

    /**
     * 根据code查询错误代码
     * @param errorCode
     * @param notId 不包含此主键
     * @return
     */
    ErrorCodeEntity selectByCode(String errorCode, Long notId);
}

