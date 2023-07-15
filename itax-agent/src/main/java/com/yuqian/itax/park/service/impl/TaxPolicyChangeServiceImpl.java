package com.yuqian.itax.park.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.park.dao.TaxPolicyChangeMapper;
import com.yuqian.itax.park.entity.TaxPolicyChangeEntity;
import com.yuqian.itax.park.entity.query.TaxPolicyChangeQuery;
import com.yuqian.itax.park.entity.vo.TaxPolicyChangeVO;
import com.yuqian.itax.park.service.TaxPolicyChangeService;
import org.springframework.stereotype.Service;


@Service("taxPolicyChangeService")
public class TaxPolicyChangeServiceImpl extends BaseServiceImpl<TaxPolicyChangeEntity,TaxPolicyChangeMapper> implements TaxPolicyChangeService {

    @Override
    public PageInfo<TaxPolicyChangeVO> getTaxPolicyChangeList(TaxPolicyChangeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(this.mapper.getTaxPolicyChangeList(query.getParkId(),query.getPolicyId(),query.getType()));
    }
}

