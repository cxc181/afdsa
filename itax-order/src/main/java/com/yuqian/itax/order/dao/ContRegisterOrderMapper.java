package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.ContRegisterOrderEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.query.ContRegisterOrderQuery;
import com.yuqian.itax.order.entity.vo.ContRegisterOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 续费订单表dao
 * 
 * @Date: 2021年02月03日 11:27:11 
 * @author 蒋匿
 */
@Mapper
public interface ContRegisterOrderMapper extends BaseMapper<ContRegisterOrderEntity> {

    /**
     * 查询续费订单列表
     * @param query
     * @return
     */
    List<ContRegisterOrderVO> listContRegOrder(ContRegisterOrderQuery query);

    /**
     * 根据orderNo查询续费订单
     * @param orderNo
     * @return
     */
    ContRegisterOrderEntity queryByOrderNo(@Param("orderNo") String orderNo);
}

