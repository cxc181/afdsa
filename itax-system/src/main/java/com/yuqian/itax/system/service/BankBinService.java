package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.dao.BankBinMapper;

/**
 * 银行卡binservice
 * 
 * @Date: 2019年12月14日 11:39:30 
 * @author 蒋匿
 */
public interface BankBinService extends IBaseService<BankBinEntity,BankBinMapper> {
    /**
     * @Description 根据银行卡号反查卡bin信息
     * @Author  Kaven
     * @Date   2019/12/16 14:34
     * @Param  bankNumber
     * @Return BankBinEntity
    */
    BankBinEntity findByCardNo(String bankNumber);
}

