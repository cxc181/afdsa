package com.yuqian.itax.tax.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.tax.dao.TaxBillCredentialsRecordMapper;
import com.yuqian.itax.tax.entity.TaxBillCredentialsRecordEntity;
import com.yuqian.itax.tax.entity.vo.TaxBillCredentialsRecordVO;
import com.yuqian.itax.tax.service.TaxBillCredentialsRecordService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("taxBillCredentialsRecordService")
public class TaxBillCredentialsRecordServiceImpl extends BaseServiceImpl<TaxBillCredentialsRecordEntity,TaxBillCredentialsRecordMapper> implements TaxBillCredentialsRecordService {

    @Override
    public List<TaxBillCredentialsRecordVO> queryTaxBillCredentialsRecordByStatus(Integer status,Long parkTaxBillId) {
        return mapper.queryTaxBillCredentialsRecordByStatus(status,parkTaxBillId);
    }
}

