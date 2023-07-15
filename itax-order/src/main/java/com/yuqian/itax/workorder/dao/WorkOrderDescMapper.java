package com.yuqian.itax.workorder.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.workorder.entity.WorkOrderDescEntity;
import com.yuqian.itax.workorder.entity.vo.WorkOrderDescVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工单备注表dao
 * 
 * @Date: 2021年08月04日 17:17:26 
 * @author 蒋匿
 */
@Mapper
public interface WorkOrderDescMapper extends BaseMapper<WorkOrderDescEntity> {

    /**
     * 根据工单号查询
     * @param workOrderNo
     * @return
     */
    List<WorkOrderDescVO> queryByWorkOrderNo(String workOrderNo);
	
}

