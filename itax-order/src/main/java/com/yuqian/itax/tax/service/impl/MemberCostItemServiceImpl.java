package com.yuqian.itax.tax.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.tax.dao.MemberCostItemMapper;
import com.yuqian.itax.tax.entity.MemberCostItemEntity;
import com.yuqian.itax.tax.entity.vo.CommonCostItemVO;
import com.yuqian.itax.tax.service.MemberCostItemService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("memberCostItemService")
public class MemberCostItemServiceImpl extends BaseServiceImpl<MemberCostItemEntity,MemberCostItemMapper> implements MemberCostItemService {

    @Override
    public List<CommonCostItemVO> findCommonCostItem(Long memberId) {
        return mapper.queryCommonCostItem(memberId);
    }
}

