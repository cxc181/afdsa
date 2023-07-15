package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.InvoiceRecordEntity;
import com.yuqian.itax.order.entity.query.InvoiceRecordQuery;
import com.yuqian.itax.order.entity.vo.ConfirmInvoiceRecordVo;
import com.yuqian.itax.order.entity.vo.InvoiceRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 开票记录表dao
 * 
 * @Date: 2020年12月25日 11:42:04 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceRecordMapper extends BaseMapper<InvoiceRecordEntity> {

    /**
     * 查询开票记录
     * @param query
     * @return
     */
    List<InvoiceRecordVO> querylistInvoiceRecord(InvoiceRecordQuery query);

    /**
     * 确认出票页面
     * @param invoiceRecordNo
     * @return
     */
    ConfirmInvoiceRecordVo gotoConfirmInvoiceRecord(@Param(value = "invoiceRecordNo") String invoiceRecordNo);

    /**
     * 根据集团开票得订单号查询子订单 未完成得开票记录
     * @param orderNo
     * @return
     */
    List<InvoiceRecordEntity> queryGroupInvoiceOrderByGroupOrderNo(@Param("orderNo")String orderNo,@Param("status")String status,@Param("notCompleted")String notCompleted);


    /**
     * 根据集团开票订单号更新状态
     * @param groupOrderNo
     * @param status
     * @param desc
     * @param updateUser
     * @param updateTime
     */
    void updateInvoiceRecordStatusByGroupOrderNoAndStatuss(@Param("groupOrderNo")String groupOrderNo, @Param("status")Integer status, @Param("desc")String desc, @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime,@Param("statuss")String statuss,@Param("notStatuss")String notStatuss);

    /**
     * 根据订单号和状态查询开票记录
     * @param orderNo
     * @param statuss
     * @return
     */
    List<InvoiceRecordEntity> queryInvoiceRecordByOrderNoAndStatus(@Param("orderNo")String orderNo,@Param("statuss")String statuss);

    /**
     * 根据订单号查询开票中的记录数
     * @param orderNo
     * @return
     */
    Integer countInvoicingRecordNumByOrderNo(@Param("orderNo")String orderNo);

    /**
     * 根据开票订单号获取发票地址信息
     * @param orderNo
     * @return
     */
    String getInvoiceDetailImgUrlsByOrder(@Param("orderNo")String orderNo);
}

