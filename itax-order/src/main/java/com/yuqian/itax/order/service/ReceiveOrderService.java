package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.ReceiveOrderMapper;
import com.yuqian.itax.order.entity.ReceiveOrderEntity;
import com.yuqian.itax.order.entity.vo.ReceiveServerVO;

/**
 * 接单表service
 * 
 * @Date: 2019年12月16日 20:20:33 
 * @author 蒋匿
 */
public interface ReceiveOrderService extends IBaseService<ReceiveOrderEntity,ReceiveOrderMapper> {
	/**
	 * @Description 派发接单客服（自动派单）
	 * 派单规则：
	 * 1、查询所有状态为1和同一个机构的接单表数据
	 * 2、统计全部的接单数量
	 * 3、用（（接单数量+1）% 坐席数量） 获取坐席集合下标
	 * 4、根据结算接口取集合相对于下标的坐席数据
	 * @Author  Kaven
	 * @Date   2019/12/24 14:31
	 * @Param oemCode orderNo orderType:1-开户 2-开票 workOrderType:工单类型 1- 办理核名 2-开票审核 3-流水审核
	 * @Return ReceiveServerVO
	 * @Exception BusinessException
	*/
	public ReceiveServerVO getReceiveServer(String oemCode, String orderNo, Integer orderType,Integer workOrderType) throws BusinessException;

	/**
	 * 根据userId查询信息
	 * @param userId
	 * @return
	 */
	ReceiveOrderEntity queryByUserId(Long userId);
}

