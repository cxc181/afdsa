package com.yuqian.itax.tax.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.entity.ParkTaxBillFileRecordEntity;
import com.yuqian.itax.tax.dao.ParkTaxBillFileRecordMapper;
import com.yuqian.itax.tax.entity.query.ParkTaxBillFileRecordQuery;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillUploadVO;

import java.util.List;

/**
 * 园区上传税单记录表service
 * 
 * @Date: 2020年12月03日 10:36:31 
 * @author 蒋匿
 */
public interface ParkTaxBillFileRecordService extends IBaseService<ParkTaxBillFileRecordEntity,ParkTaxBillFileRecordMapper> {
    /**
     * 批量插入
     */
    void addBatch(List<ParkTaxBillUploadVO> vo,String account);
    /**
     * 下载解析记录
     */
    List<ParkTaxBillUploadVO> queryparkTaxBillFileRecordByParkTaxBillId(ParkTaxBillFileRecordQuery query);
}

