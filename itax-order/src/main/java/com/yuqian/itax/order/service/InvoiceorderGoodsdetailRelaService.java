package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.InvoiceorderGoodsdetailRelaMapper;
import com.yuqian.itax.order.entity.InvoiceorderGoodsdetailRelaEntity;
import com.yuqian.itax.order.entity.vo.InvoiceOrderGoodsDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 开票订单与商品的关系表service
 * 
 * @Date: 2021年11月09日 17:24:24 
 * @author 蒋匿
 */
public interface InvoiceorderGoodsdetailRelaService extends IBaseService<InvoiceorderGoodsdetailRelaEntity,InvoiceorderGoodsdetailRelaMapper> {

    /**
     * 根据orderNo查询商品明细
     * @param orderNo
     * @return
     */
    List<InvoiceOrderGoodsDetailVO> queryGoodsDetailByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据oderNo数组查询数据
     * @param orderNoList
     * @return
     */
    List<InvoiceOrderGoodsDetailVO> queryGoodsDetailByOrderNoList(List<String> orderNoList);
	
}

