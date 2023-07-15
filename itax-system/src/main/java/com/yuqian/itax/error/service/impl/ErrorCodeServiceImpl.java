package com.yuqian.itax.error.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.error.dao.ErrorCodeMapper;
import com.yuqian.itax.error.entity.ErrorCodeEntity;
import com.yuqian.itax.error.entity.query.ErrorCodeQuery;
import com.yuqian.itax.error.service.ErrorCodeService;
import org.springframework.stereotype.Service;


@Service("errorCodeService")
public class ErrorCodeServiceImpl extends BaseServiceImpl<ErrorCodeEntity,ErrorCodeMapper> implements ErrorCodeService {

    @Override
    public PageInfo<ErrorCodeEntity> page(ErrorCodeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listErrorCode(query));
    }

    @Override
    public ErrorCodeEntity selectByCode(String errorCode, Long notId) {
        return mapper.selectByCode(errorCode, notId);
    }
}

