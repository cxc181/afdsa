package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.query.InvoiceRecordDetailQuery;
import com.yuqian.itax.order.entity.vo.InvoiceDetailVO;
import com.yuqian.itax.order.entity.vo.InvoiceRecordDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 开票明细表dao
 * 
 * @Date: 2020年12月25日 11:42:18 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceRecordDetailMapper extends BaseMapper<InvoiceRecordDetailEntity> {

    /**
     * 根据订单编号查询开票记录
     * @param query
     * @return
     */
    List<InvoiceDetailVO> queryByOrderNo(InvoiceRecordDetailQuery query);

    /**
     * 查询开票记录明细
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    List<InvoiceRecordDetailVO> invoiceRecordDetailList(@Param(value = "invoiceRecordNo") String invoiceRecordNo, @Param(value = "orderNo")String orderNo);

    /**
     * 查询开票中的开票记录明细
     * @param invoiceRecordNo
     * @return
     */
    List<Map<String,Object>> queryDetailStatusNumByIng(@Param(value = "invoiceRecordNo") String invoiceRecordNo);

    /**
     * 根据开票记录编号修改开票明细状态
     * @param invoiceRecordNo
     * @param status
     */
    void updateByInvoiceRecordNo(@Param(value = "invoiceRecordNo")String invoiceRecordNo,@Param(value = "status")Integer status);

    /**
     * 根据开票记录编号查询开票明细
     * @param invoiceRecordNo
     * @return
     */
    List<InvoiceRecordDetailEntity> findByInvoiceRecordNo(@Param(value = "invoiceRecordNo")String invoiceRecordNo);
}

