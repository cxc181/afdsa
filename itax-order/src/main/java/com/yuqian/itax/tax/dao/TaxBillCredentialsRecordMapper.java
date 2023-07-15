package com.yuqian.itax.tax.dao;

import com.yuqian.itax.tax.entity.TaxBillCredentialsRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.vo.TaxBillCredentialsRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 税单完税凭证解析记录表dao
 * 
 * @Date: 2020年12月25日 11:34:04 
 * @author 蒋匿
 */
@Mapper
public interface TaxBillCredentialsRecordMapper extends BaseMapper<TaxBillCredentialsRecordEntity> {

    /**
     * 下载失败文件
     */
    List<TaxBillCredentialsRecordVO> queryTaxBillCredentialsRecordByStatus(@Param("status")Integer status,@Param("parkTaxBillId") Long parkTaxBillId);
}

