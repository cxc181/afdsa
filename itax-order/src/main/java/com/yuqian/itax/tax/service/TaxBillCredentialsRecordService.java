package com.yuqian.itax.tax.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.entity.TaxBillCredentialsRecordEntity;
import com.yuqian.itax.tax.dao.TaxBillCredentialsRecordMapper;
import com.yuqian.itax.tax.entity.vo.TaxBillCredentialsRecordVO;

import java.util.List;

/**
 * 税单完税凭证解析记录表service
 * 
 * @Date: 2020年12月25日 11:34:04 
 * @author 蒋匿
 */
public interface TaxBillCredentialsRecordService extends IBaseService<TaxBillCredentialsRecordEntity,TaxBillCredentialsRecordMapper> {
    /**
     * 下载失败文件
     */
    List<TaxBillCredentialsRecordVO>  queryTaxBillCredentialsRecordByStatus(Integer status,Long parkTaxBillId);
}

