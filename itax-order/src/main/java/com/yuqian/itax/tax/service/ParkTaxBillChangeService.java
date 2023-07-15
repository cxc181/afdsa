package com.yuqian.itax.tax.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.entity.ParkTaxBillChangeEntity;
import com.yuqian.itax.tax.dao.ParkTaxBillChangeMapper;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillQueryAdmin;

/**
 * 园区税单变更表service
 * 
 * @Date: 2021年03月16日 14:47:22 
 * @author 蒋匿
 */
public interface ParkTaxBillChangeService extends IBaseService<ParkTaxBillChangeEntity,ParkTaxBillChangeMapper> {

    /**
     * 分页
     */
     PageInfo<ParkTaxBillChangeEntity> queryPageInfo(CompanyTaxBillQueryAdmin queryAdmin);
}

