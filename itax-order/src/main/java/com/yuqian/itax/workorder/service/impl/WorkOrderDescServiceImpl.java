package com.yuqian.itax.workorder.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.workorder.dao.WorkOrderDescMapper;
import com.yuqian.itax.workorder.entity.WorkOrderDescEntity;
import com.yuqian.itax.workorder.entity.query.WordOrderDescQuery;
import com.yuqian.itax.workorder.entity.vo.WorkOrderDescVO;
import com.yuqian.itax.workorder.service.WorkOrderDescService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("workOrderDescService")
public class WorkOrderDescServiceImpl extends BaseServiceImpl<WorkOrderDescEntity,WorkOrderDescMapper> implements WorkOrderDescService {

    @Override
    public List<WorkOrderDescVO> queryByWorkOrderNo(String workOrderNo) {
        return mapper.queryByWorkOrderNo(workOrderNo);
    }

    @Override
    public PageInfo<WorkOrderDescVO> queryByWorkOrderNoPageInfo(WordOrderDescQuery wordOrderDescQuery) {
        PageHelper.startPage(wordOrderDescQuery.getPageNumber(), wordOrderDescQuery.getPageSize());
        return new PageInfo<>(this.mapper.queryByWorkOrderNo(wordOrderDescQuery.getWorkOrderNo()));
    }
}

