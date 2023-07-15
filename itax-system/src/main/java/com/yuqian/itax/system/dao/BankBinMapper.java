package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 银行卡bindao
 * 
 * @Date: 2019年12月14日 11:39:30 
 * @author 蒋匿
 */
@Mapper
public interface BankBinMapper extends BaseMapper<BankBinEntity> {
    /**
     * @Description 根据银行卡号反查卡bin信息
     * @Author  Kaven
     * @Date   2019/12/16 14:36
     * @Param  bankNumber
     * @Return BankBinEntity
    */
    BankBinEntity findByCardNo(String bankNumber);
}

