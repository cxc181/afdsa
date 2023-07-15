package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.BankBinMapper;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.service.BankBinService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("bankBinService")
public class BankBinServiceImpl extends BaseServiceImpl<BankBinEntity,BankBinMapper> implements BankBinService {
    @Resource
    private BankBinMapper bankBinMapper;

    @Override
    public BankBinEntity findByCardNo(String bankNumber) {
        return this.bankBinMapper.findByCardNo(bankNumber);
    }
}

