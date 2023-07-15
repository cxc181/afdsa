package com.yuqian.itax.group.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.group.dao.GroupPaymentAnalysisRecordMapper;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.service.GroupPaymentAnalysisRecordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("groupPaymentAnalysisRecordService")
public class GroupPaymentAnalysisRecordServiceImpl extends BaseServiceImpl<GroupPaymentAnalysisRecordEntity,GroupPaymentAnalysisRecordMapper> implements GroupPaymentAnalysisRecordService {

    @Override
    public List<GroupPaymentAnalysisRecordEntity> queryByOrderNo(String groupOrderNo, String oemCode) {
        return mapper.queryByOrderNo(groupOrderNo, oemCode);
    }

    @Override
    public Map<String, Object> sumByGroupOrderNo(String groupOrderNo, String oemCode) {
        return mapper.sumByGroupOrderNo(groupOrderNo, oemCode);
    }

    @Override
    public void batchAdd(List<GroupPaymentAnalysisRecordEntity> list) {
        mapper.batchAdd(list);
    }
}

