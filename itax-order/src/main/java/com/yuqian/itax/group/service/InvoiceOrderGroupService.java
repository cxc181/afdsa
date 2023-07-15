package com.yuqian.itax.group.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.dao.InvoiceOrderGroupMapper;
import com.yuqian.itax.group.entity.query.InvoiceOrderGroupQuery;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupAuditVo;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupListVO;
import com.yuqian.itax.user.entity.UserEntity;

import java.util.List;

/**
 * 集团开票订单service
 * 
 * @Date: 2020年03月04日 09:25:55 
 * @author 蒋匿
 */
public interface InvoiceOrderGroupService extends IBaseService<InvoiceOrderGroupEntity,InvoiceOrderGroupMapper> {

    /**
     * 分页查询集团代开
     * @param query
     * @return
     */
    PageInfo<InvoiceOrderGroupListVO> listPage(InvoiceOrderGroupQuery query);

    /**
     * 根据条件查询集团代开列表
     * @param query
     * @return
     */
    List<InvoiceOrderGroupListVO> listInvoiceOrderGroup(InvoiceOrderGroupQuery query);

    /**
     * 根据订单编号查询审核页面信息
     * @param orderNo
     * @param
     * @return
     */
    InvoiceOrderGroupAuditVo queryInvoiceOrderByOrderNo(String orderNo);
    /**
     * 根据订单编号查询集团代开订单
     * @param orderNo
     * @param oemCode
     * @return
     */
    InvoiceOrderGroupEntity queryByOrderNo(String orderNo, String oemCode);

    /**
     * 修改状态
     * @param entity
     * @param status 1签收，2取消
     * @param userEntity
     */
    void updateStatus(InvoiceOrderGroupEntity entity, Integer status, UserEntity userEntity);

    /**
     * 根据状态查询集团开票订单
     * @param orderStatus 订单状态 0-流水解析中 1-出票中 2-已签收 3-已取消
     * @return
     */
    List<InvoiceOrderGroupEntity> queryByStatus(Integer orderStatus);

    /**
     * 集团订单修改状态为出票中
     * @param id
     */
    void ticketing(Long id);

    /**
     * 财务审核通过
     * @param query
     */
    void approved(InvoiceOrderGroupQuery query);
}

