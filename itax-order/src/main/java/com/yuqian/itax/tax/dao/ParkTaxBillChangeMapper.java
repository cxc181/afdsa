package com.yuqian.itax.tax.dao;

import com.yuqian.itax.tax.entity.ParkTaxBillChangeEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillQueryAdmin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 园区税单变更表dao
 * 
 * @Date: 2021年03月16日 14:47:22 
 * @author 蒋匿
 */
@Mapper
public interface ParkTaxBillChangeMapper extends BaseMapper<ParkTaxBillChangeEntity> {
    /**
     * 园区税单历史记录
     */
    List<ParkTaxBillChangeEntity> queryParkTaxBillChange(CompanyTaxBillQueryAdmin queryAdmin);
	
}

