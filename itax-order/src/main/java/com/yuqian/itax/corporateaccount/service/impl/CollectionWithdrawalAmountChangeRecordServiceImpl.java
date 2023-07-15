package com.yuqian.itax.corporateaccount.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.corporateaccount.dao.CollectionWithdrawalAmountChangeRecordMapper;
import com.yuqian.itax.corporateaccount.entity.CollectionWithdrawalAmountChangeRecordEntity;
import com.yuqian.itax.corporateaccount.service.CollectionWithdrawalAmountChangeRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("collectionWithdrawalAmountChangeRecordService")
public class CollectionWithdrawalAmountChangeRecordServiceImpl extends BaseServiceImpl<CollectionWithdrawalAmountChangeRecordEntity,CollectionWithdrawalAmountChangeRecordMapper> implements CollectionWithdrawalAmountChangeRecordService {

    @Override
    public void addCollectionWithdrawalAmountChangeRecordEntity(Long corporateAccountId,String bankCollectionRecordNo,Long arriveAmount,Long beforeAmount,Long changeAmount,
                                                                String orderNo,String account ,String remark,String invoiceOrderNo) {
        CollectionWithdrawalAmountChangeRecordEntity collectionWithdrawalAmountChangeRecordEntity=new CollectionWithdrawalAmountChangeRecordEntity();
        collectionWithdrawalAmountChangeRecordEntity.setCorporateAccountId(corporateAccountId);
        collectionWithdrawalAmountChangeRecordEntity.setBankCollectionRecordNo(bankCollectionRecordNo);
        collectionWithdrawalAmountChangeRecordEntity.setArriveAmount(arriveAmount);
        collectionWithdrawalAmountChangeRecordEntity.setBeforeAmount(beforeAmount);
        collectionWithdrawalAmountChangeRecordEntity.setChangeAmount(changeAmount);
        collectionWithdrawalAmountChangeRecordEntity.setAfterAmount(beforeAmount-changeAmount);
        collectionWithdrawalAmountChangeRecordEntity.setChangeTime(new Date());
        collectionWithdrawalAmountChangeRecordEntity.setOrderNo(orderNo);
        collectionWithdrawalAmountChangeRecordEntity.setChangeType(2);
        collectionWithdrawalAmountChangeRecordEntity.setAddTime(new Date());
        collectionWithdrawalAmountChangeRecordEntity.setAddUser(account);
        collectionWithdrawalAmountChangeRecordEntity.setRemark(remark);
        collectionWithdrawalAmountChangeRecordEntity.setInvoiceOrderNo(invoiceOrderNo);
        mapper.insert(collectionWithdrawalAmountChangeRecordEntity);
    }
}

