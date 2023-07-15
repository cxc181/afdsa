package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.InvoiceServiceFeeDetailEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.vo.InvoiceServiceFeeDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务费收费阶段明细dao
 * 
 * @Date: 2021年03月16日 14:51:12 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceServiceFeeDetailMapper extends BaseMapper<InvoiceServiceFeeDetailEntity> {

    /**
     * 根据订单编号查询服务费明细
     * @param orderNo
     * @return
     */
    List<InvoiceServiceFeeDetailVO> queryDetailByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询订单最小阶梯服务费率
     * @param orderNo
     * @return
     */
    BigDecimal queryLeastServiceFeeRate(String orderNo);
}

