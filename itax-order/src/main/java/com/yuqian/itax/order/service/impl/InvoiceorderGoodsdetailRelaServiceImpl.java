package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.InvoiceorderGoodsdetailRelaMapper;
import com.yuqian.itax.order.entity.InvoiceorderGoodsdetailRelaEntity;
import com.yuqian.itax.order.entity.vo.InvoiceOrderGoodsDetailVO;
import com.yuqian.itax.order.service.InvoiceorderGoodsdetailRelaService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("invoiceorderGoodsdetailRelaService")
public class InvoiceorderGoodsdetailRelaServiceImpl extends BaseServiceImpl<InvoiceorderGoodsdetailRelaEntity,InvoiceorderGoodsdetailRelaMapper> implements InvoiceorderGoodsdetailRelaService {

    @Override
    public List<InvoiceOrderGoodsDetailVO> queryGoodsDetailByOrderNo(String orderNo) {
        return mapper.queryGoodsDetailByOrderNo(orderNo);
    }

    @Override
    public List<InvoiceOrderGoodsDetailVO> queryGoodsDetailByOrderNoList(List<String> orderNoList) {
        return mapper.queryGoodsDetailByOrderNoList(orderNoList);
    }
}

