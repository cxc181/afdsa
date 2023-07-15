package com.yuqian.itax.group.dao;

import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.group.entity.query.InvoiceOrderGroupQuery;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupAuditVo;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 集团开票订单dao
 * 
 * @Date: 2020年03月04日 09:25:55 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceOrderGroupMapper extends BaseMapper<InvoiceOrderGroupEntity> {

    /**
     * 查询集团开票集合
     * @param query
     * @return
     */
    List<InvoiceOrderGroupListVO> listInvoiceOrderGroup(InvoiceOrderGroupQuery query);

    /**
     * 根据订单编号查询集团代开订单
     * @param orderNo
     * @param oemCode
     * @return
     */
    InvoiceOrderGroupEntity queryByOrderNo(@Param("orderNo")String orderNo, @Param("oemCode")String oemCode);

    /**
     * 根据订单编号查询集团代开订单
     * @param orderNo
     * @return
     */
    InvoiceOrderGroupEntity queryOrderByOrderNo(@Param("orderNo")String orderNo);

    /**
     * 根据订单编号查询审核页面信息
     * @param orderNo
     * @param
     * @return
     */
    InvoiceOrderGroupAuditVo queryInvoiceOrderByOrderNo(String orderNo);

    /**
     * 根据订单编号修改状态
     * @param orderNo
     * @param oemCode
     * @param orderStatus
     * @param updateUser
     * @param updateTime
     * @param remark
     * @return
     */
    void updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("oemCode") String oemCode, @Param("orderStatus") Integer orderStatus,
                               @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime, @Param("remark") String remark);

    /**
     * 根据状态查询集团开票订单
     * @param orderStatus
     * @return
     */
    List<InvoiceOrderGroupEntity> queryByStatus(@Param("orderStatus")Integer orderStatus);
}

