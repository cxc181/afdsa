package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.InvoiceorderGoodsdetailRelaEntity;
import com.yuqian.itax.order.entity.vo.InvoiceOrderGoodsDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 开票订单与商品的关系表dao
 * 
 * @Date: 2021年11月09日 17:24:24 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceorderGoodsdetailRelaMapper extends BaseMapper<InvoiceorderGoodsdetailRelaEntity> {

    /**
     * 根据orderNo查询商品明细
     * @param orderNo
     * @return
     */
    List<InvoiceOrderGoodsDetailVO> queryGoodsDetailByOrderNo(String orderNo);


    /**
     * 根据oderNo数组查询数据
     * @param list
     * @return
     */
    List<InvoiceOrderGoodsDetailVO> queryGoodsDetailByOrderNoList(@Param("list") List<String> list);
	
}

