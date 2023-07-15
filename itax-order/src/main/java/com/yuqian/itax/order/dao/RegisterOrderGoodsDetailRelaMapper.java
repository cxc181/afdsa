package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import com.yuqian.itax.system.entity.vo.UnmatchedBusinessScopeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 注册订单与商品明细的关系表dao
 * 
 * @Date: 2022年12月29日 13:51:42 
 * @author 蒋匿
 */
@Mapper
public interface RegisterOrderGoodsDetailRelaMapper extends BaseMapper<RegisterOrderGoodsDetailRelaEntity> {

    /**
     * 获取赋码后新增经营范围
     * @param orderNo
     * @return
     */
    List<String> getAddedBusinessScope(@Param("orderNo") String orderNo, @Param("orderTime") Date orderTime);


    /**
     * 获取未匹配到的经营范围
     * @param orderNo
     * @return
     */
    List<UnmatchedBusinessScopeVO> getUnmatchedBusinessScopeByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 批量添加
     * @param list
     */
    int batchAdd(@Param("list") List<RegisterOrderGoodsDetailRelaEntity> list);

    /**
     * 根据订单编号查询订单与商品关系列表
     * @param orderNo
     * @return
     */
    List<RegisterOrderGoodsDetailRelaEntity> queryByOrderNo(String orderNo);
}

