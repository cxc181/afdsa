package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.RegisterOrderGoodsDetailRelaMapper;
import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import com.yuqian.itax.system.entity.vo.UnmatchedBusinessScopeVO;

import java.util.Date;
import java.util.List;

/**
 * 注册订单与商品明细的关系表service
 * 
 * @Date: 2022年12月29日 13:51:42 
 * @author 蒋匿
 */
public interface RegisterOrderGoodsDetailRelaService extends IBaseService<RegisterOrderGoodsDetailRelaEntity,RegisterOrderGoodsDetailRelaMapper> {

    /**
     * 获取赋码后新增经营范围
     * @param orderNo
     * @return
     */
    List<String> getAddedBusinessScope(String orderNo, Date orderTime);

    /**
     * 获取未匹配到的经营范围
     * @param orderNo
     * @return
     */
    List<UnmatchedBusinessScopeVO> getUnmatchedBusinessScopeByOrderNo(String orderNo);

    /**
     * 批量添加
     */
    int batchAdd(List<RegisterOrderGoodsDetailRelaEntity> merchandises);

    /**
     * 根据订单编号查询订单与商品关系列表
     * @param orderNo
     * @return
     */
    List<RegisterOrderGoodsDetailRelaEntity> findByOrderNo(String orderNo);
}

