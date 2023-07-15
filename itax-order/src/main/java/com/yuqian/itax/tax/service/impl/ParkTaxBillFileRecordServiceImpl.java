package com.yuqian.itax.tax.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.tax.dao.ParkTaxBillFileRecordMapper;
import com.yuqian.itax.tax.entity.ParkTaxBillFileRecordEntity;
import com.yuqian.itax.tax.entity.query.ParkTaxBillFileRecordQuery;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillUploadVO;
import com.yuqian.itax.tax.service.ParkTaxBillFileRecordService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("parkTaxBillFileRecordService")
public class ParkTaxBillFileRecordServiceImpl extends BaseServiceImpl<ParkTaxBillFileRecordEntity,ParkTaxBillFileRecordMapper> implements ParkTaxBillFileRecordService {


    /**
     * 批量插入
     */
    public void addBatch(List<ParkTaxBillUploadVO> vo,String account){
        mapper.addBatch(vo,account);
    }

    @Override
    public List<ParkTaxBillUploadVO> queryparkTaxBillFileRecordByParkTaxBillId(ParkTaxBillFileRecordQuery query) {
        return mapper.queryparkTaxBillFileRecordByParkTaxBillId(query);
    }
}

