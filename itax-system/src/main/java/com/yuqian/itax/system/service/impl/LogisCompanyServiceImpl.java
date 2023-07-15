package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.LogisCompanyMapper;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.LogisCompanyService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("logisCompanyService")
public class LogisCompanyServiceImpl extends BaseServiceImpl<LogisCompanyEntity,LogisCompanyMapper> implements LogisCompanyService {

    @Override
    public List<LogisCompanyEntity> logisCompanyList() {
        return mapper.logisCompanyList();
    }

    @Override
    public LogisCompanyEntity queryByCompanyName(String companyName) {
        return mapper.queryByCompanyName(companyName);
    }

    @Override
    public List<LogisCompanyEntity> queryByLikeCompanyName(String companyName) {
        return mapper.queryByLikeCompanyName(companyName);
    }
}

