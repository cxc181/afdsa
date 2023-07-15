package com.yuqian.itax.corporateaccount.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.corporateaccount.dao.CollectionWithdrawalAmountChangeRecordMapper;
import com.yuqian.itax.corporateaccount.entity.CollectionWithdrawalAmountChangeRecordEntity;

/**
 * 对公户银行收款核销记录表service
 * 
 * @Date: 2020年09月07日 17:48:04 
 * @author 蒋匿
 */
public interface CollectionWithdrawalAmountChangeRecordService extends IBaseService<CollectionWithdrawalAmountChangeRecordEntity,CollectionWithdrawalAmountChangeRecordMapper> {

    /**
     * 新增核销记录
     */
    void addCollectionWithdrawalAmountChangeRecordEntity(Long corporateAccountId,String bankCollectionRecordNo,Long arriveAmount,Long beforeAmount,Long changeAmount,String orderNo,String account ,String remark,String invoiceOrderNo);
}

