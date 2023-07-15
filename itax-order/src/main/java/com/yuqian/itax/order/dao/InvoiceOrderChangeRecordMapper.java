package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.InvoiceOrderChangeRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 开票订单变更记录dao
 * 
 * @Date: 2019年12月07日 19:54:14 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceOrderChangeRecordMapper extends BaseMapper<InvoiceOrderChangeRecordEntity> {
    /**
     * @Description 根据订单号查询订单
     * @Author  yejian
     * @Date   2019/12/11 16:55
     * @Param  orderNo
     * @Return InvoiceOrderChangeRecordEntity
     */
    InvoiceOrderChangeRecordEntity queryByOrderNo(String orderNo);

    /**
     * 批量添加历史表
     * @param ids
     * @param orderStatus
     * @param addUser
     * @param addTime
     */
    void batchAdd(@Param("ids")List<Long> ids, @Param("orderStatus")Integer orderStatus, @Param("addUser")String addUser, @Param("addTime")Date addTime, @Param("remark")String remark);

    /**
     * 批量
     * @param groupOrderNo
     * @param oemCode
     * @param orderStatus
     * @param addUser
     * @param addTime
     * @param remark
     */
    void batchAddByGroupOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode, @Param("orderStatus")Integer orderStatus,
                                @Param("addUser")String addUser, @Param("addTime")Date addTime, @Param("remark")String remark);
}

