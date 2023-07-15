package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.CompanyInvoiceRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 企业开票记录dao
 * 
 * @Date: 2019年12月10日 11:35:24 
 * @author 蒋匿
 */
@Mapper
public interface CompanyInvoiceRecordMapper extends BaseMapper<CompanyInvoiceRecordEntity> {

    /**
     * 开票金额回滚
     * @param companyId
     * @param addTime
     * @param amount
     * @param updateUser
     * @param updateTime
     */
    void refund(@Param("companyId")Long companyId, @Param("addTime")Date addTime, @Param("amount")Long amount,
                @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);

    /**
     * 根据企业id查询企业开票记录
     * @return
     */
    CompanyInvoiceRecordEntity findByCompanyId(Long companyId);

    /**
     * 根据企业id查询企业累计已完成开票订单额度
     * @return
     */
    Long sumUseInvoiceAmount(Long companyId);
}

