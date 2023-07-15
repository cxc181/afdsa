package com.yuqian.itax.tax.dao;

import com.yuqian.itax.tax.entity.TParkTaxBillEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.query.ParkTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillXXJOBVO;
import com.yuqian.itax.tax.entity.query.ParkTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 园区税单查询dao
 * 
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
@Mapper
public interface ParkTaxBillMapper extends BaseMapper<TParkTaxBillEntity> {

    /**
     * 查询园区税单初始化数据
     */
    List<ParkTaxBillXXJOBVO> queryParkTaxBillByTime(ParkTaxBillQuery query);
    /**
     *根据企业税单统计园区税单
     */
    List<ParkTaxBillXXJOBVO> queryParkTaxBillByCompanyTaxBill(ParkTaxBillQuery query);
    /**
     * 查询列表
     */
    List<ParkTaxBillVO> queryParkTaxBillPageInfo(ParkTaxBillQuery query);
}

