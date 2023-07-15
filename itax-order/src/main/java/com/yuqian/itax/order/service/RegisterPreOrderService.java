package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.RegisterPreOrderMapper;
import com.yuqian.itax.order.entity.RegisterPreOrderEntity;
import com.yuqian.itax.order.entity.dto.RegisterPreOrderDTO;

/**
 * 工商注册预订单service
 * 
 * @Date: 2022年06月27日 17:54:10 
 * @author 蒋匿
 */
public interface RegisterPreOrderService extends IBaseService<RegisterPreOrderEntity,RegisterPreOrderMapper> {

    /**
     * 新增/编辑预订单
     * @param dto
     * @return
     */
    String addOrUpdate(RegisterPreOrderDTO dto);

    /**
     * 删除注册预订单
     * @param memberId
     */
    void deletePreOrder(Long memberId);

    /**
     * 查询用户预订单
     * @param memberId
     * @return
     */
    RegisterPreOrderEntity queryByMemberId(Long memberId);
}

