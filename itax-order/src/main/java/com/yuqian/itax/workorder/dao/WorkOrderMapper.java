package com.yuqian.itax.workorder.dao;

import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.workorder.entity.query.WorkOrderQuery;
import com.yuqian.itax.workorder.entity.vo.WorkOrderListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工单dao
 *
 * @Date: 2019年12月07日 20:00:45
 * @author 蒋匿
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrderEntity> {
    /**
     * 根据查询条件查询工单列表
     * @param workOrderQuery
     * @return
     */
	List<WorkOrderListVO> queryWorkOrderList (WorkOrderQuery workOrderQuery);

	/**
	 * @Description 查询可取消状态的工单
	 * @Author  Kaven
	 * @Date   2019/12/27 11:15
	 * @Param  WorkOrderEntity
	 * @Return WorkOrderEntity
	*/
    WorkOrderEntity queryOrderByStatus(WorkOrderEntity t);

	/**
	 * 根据订单号查询工单信息
	 * @param orderNo
	 * @return
	 */
	List<WorkOrderEntity> queryWorkOrderByOrderNo(@Param("orderNo") String orderNo);

	/**
	 * 查询用户工单
	 * @param workOrderQuery
	 * @return
	 */
    List<WorkOrderEntity> queryByUserId(WorkOrderQuery workOrderQuery);
}

