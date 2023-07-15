package com.yuqian.itax.order.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.ContRegisterOrderEntity;
import com.yuqian.itax.order.dao.ContRegisterOrderMapper;
import com.yuqian.itax.order.entity.query.ContRegisterOrderQuery;
import com.yuqian.itax.order.entity.vo.ContRegisterOrderVO;

import java.util.List;

/**
 * 续费订单表service
 * 
 * @Date: 2021年02月03日 11:27:11 
 * @author 蒋匿
 */
public interface ContRegisterOrderService extends IBaseService<ContRegisterOrderEntity,ContRegisterOrderMapper> {

    /**
     * 分页查询续费订单
     * @param query
     * @return
     */
    PageInfo<ContRegisterOrderVO> listPageContRegOrder(ContRegisterOrderQuery query);

    /**
     * 查询续费订单列表
     * @param query
     * @return
     */
    List<ContRegisterOrderVO> listContRegOrder(ContRegisterOrderQuery query);

    /**
     * 取消未支付订单
     * @param contRegisterOrderEntity
     */
    void cancelOrder(ContRegisterOrderEntity contRegisterOrderEntity);


    /**
     * 创建托管费续费订单
     * @param currUserId
     * @param oemCode
     * @param companyId
     * @param
     * @return
     */
    String createContRegOrder(Long currUserId, String oemCode, Long companyId);
}

