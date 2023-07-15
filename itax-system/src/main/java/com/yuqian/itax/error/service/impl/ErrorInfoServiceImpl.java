package com.yuqian.itax.error.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.error.dao.ErrorInfoMapper;
import com.yuqian.itax.error.entity.ErrorCodeEntity;
import com.yuqian.itax.error.entity.ErrorInfoEntity;
import com.yuqian.itax.error.entity.query.ErrorInfoQuery;
import com.yuqian.itax.error.service.ErrorInfoService;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("errorInfoService")
public class ErrorInfoServiceImpl extends BaseServiceImpl<ErrorInfoEntity,ErrorInfoMapper> implements ErrorInfoService {

    @Override
    public PageInfo<ErrorInfoEntity> page(ErrorInfoQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listErrorInfo(query));
    }

    @Override
    public void addErrorInfo(Integer clientType, String modelName, String className, String errorContent, String errorParamsInfo, String oemCode, String addUser) {
        ErrorInfoEntity error = new ErrorInfoEntity();
        error.setClientType(clientType);
        error.setModelName(modelName);
        error.setClassName(className);
        error.setErrorContent(errorContent);
        error.setErrorParamsInfo(errorParamsInfo);
        error.setOemCode(oemCode);
        error.setAddTime(new Date());
        error.setAddUser(addUser);
        mapper.insertSelective(error);
    }
}

