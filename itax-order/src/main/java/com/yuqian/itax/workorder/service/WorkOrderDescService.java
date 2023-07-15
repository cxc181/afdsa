package com.yuqian.itax.workorder.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.workorder.dao.WorkOrderDescMapper;
import com.yuqian.itax.workorder.entity.WorkOrderDescEntity;
import com.yuqian.itax.workorder.entity.query.WordOrderDescQuery;
import com.yuqian.itax.workorder.entity.vo.WorkOrderDescVO;

import java.util.List;

/**
 * 工单备注表service
 * 
 * @Date: 2021年08月04日 17:17:26 
 * @author 蒋匿
 */
public interface WorkOrderDescService extends IBaseService<WorkOrderDescEntity,WorkOrderDescMapper> {

    /**
     * 根据工单号查询
     * @param workOrderNo
     * @return
     */
    List<WorkOrderDescVO> queryByWorkOrderNo(String workOrderNo);

    /**
     * 分页查询
     * @param wordOrderDescQuery
     * @return
     */
    PageInfo<WorkOrderDescVO>  queryByWorkOrderNoPageInfo(WordOrderDescQuery wordOrderDescQuery);
	
}

