package com.yuqian.itax.tax.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.tax.dao.ParkTaxBillChangeMapper;
import com.yuqian.itax.tax.entity.ParkTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.TParkTaxBillEntity;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillQueryAdmin;
import com.yuqian.itax.tax.service.ParkTaxBillChangeService;
import com.yuqian.itax.tax.service.ParkTaxBillService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Service("parkTaxBillChangeService")
public class ParkTaxBillChangeServiceImpl extends BaseServiceImpl<ParkTaxBillChangeEntity,ParkTaxBillChangeMapper> implements ParkTaxBillChangeService {

    @Resource
    ParkTaxBillService parkTaxBillService;
    @Override
    public PageInfo<ParkTaxBillChangeEntity> queryPageInfo(CompanyTaxBillQueryAdmin queryAdmin) {
        TParkTaxBillEntity tParkTaxBillEntity=parkTaxBillService.findById(queryAdmin.getParkTaxBillId());
        if(tParkTaxBillEntity==null){
            throw  new BusinessException("园区税单不存在");
        }
        queryAdmin.setTaxBillYear(tParkTaxBillEntity.getTaxBillYear());
        queryAdmin.setTaxBillSeasonal(tParkTaxBillEntity.getTaxBillSeasonal());
        queryAdmin.setParkId(tParkTaxBillEntity.getParkId());
        PageHelper.startPage(queryAdmin.getPageNumber(),queryAdmin.getPageSize());
        return new PageInfo<>(mapper.queryParkTaxBillChange(queryAdmin));
    }
}

