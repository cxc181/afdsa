package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.RegisterOrderGoodsDetailRelaMapper;
import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import com.yuqian.itax.order.service.RegisterOrderGoodsDetailRelaService;
import com.yuqian.itax.system.entity.vo.UnmatchedBusinessScopeVO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("registerOrderGoodsDetailRelaService")
public class RegisterOrderGoodsDetailRelaServiceImpl extends BaseServiceImpl<RegisterOrderGoodsDetailRelaEntity,RegisterOrderGoodsDetailRelaMapper> implements RegisterOrderGoodsDetailRelaService {


    @Override
    public List<String> getAddedBusinessScope(String orderNo, Date orderTime) {
        return mapper.getAddedBusinessScope(orderNo,orderTime);
    }

    @Override
    public List<UnmatchedBusinessScopeVO> getUnmatchedBusinessScopeByOrderNo(String orderNo) {
        return mapper.getUnmatchedBusinessScopeByOrderNo(orderNo);
    }

    @Override
    public int batchAdd(List<RegisterOrderGoodsDetailRelaEntity> merchandises) {
        return mapper.batchAdd(merchandises);
    }

    @Override
    public List<RegisterOrderGoodsDetailRelaEntity> findByOrderNo(String orderNo) {
        return mapper.queryByOrderNo(orderNo);
    }
}

