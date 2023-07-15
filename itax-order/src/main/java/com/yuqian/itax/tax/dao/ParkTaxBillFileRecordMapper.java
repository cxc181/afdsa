package com.yuqian.itax.tax.dao;

import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.tax.entity.ParkTaxBillFileRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.query.ParkTaxBillFileRecordQuery;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillUploadVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区上传税单记录表dao
 * 
 * @Date: 2020年12月03日 10:36:31 
 * @author 蒋匿
 */
@Mapper
public interface ParkTaxBillFileRecordMapper extends BaseMapper<ParkTaxBillFileRecordEntity> {

    /**
     * 批量插入
     */
    void addBatch(@Param("lists") List<ParkTaxBillUploadVO> lists, @Param("account") String account);
    /**
     *
     */
    List<ParkTaxBillUploadVO> queryparkTaxBillFileRecordByParkTaxBillId(ParkTaxBillFileRecordQuery query);
}

