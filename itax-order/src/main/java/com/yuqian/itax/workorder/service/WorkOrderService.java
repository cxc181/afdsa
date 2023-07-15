package com.yuqian.itax.workorder.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.dao.WorkOrderMapper;
import com.yuqian.itax.workorder.entity.dto.WorkOrderDTO;
import com.yuqian.itax.workorder.entity.query.WorkOrderQuery;
import com.yuqian.itax.workorder.entity.vo.WorkOrderListVO;

import java.util.List;
import java.util.Map;

/**
 * 工单service
 *
 * @Date: 2019年12月07日 20:00:45
 * @author 蒋匿
 */
public interface WorkOrderService extends IBaseService<WorkOrderEntity,WorkOrderMapper> {
    /**
     * 工单分页查询
     * @param workOrderQuery
     * @return
     */
    PageInfo<WorkOrderListVO> queryWorkOrderPageInfo(WorkOrderQuery workOrderQuery);

    /**
     * 更新工单状态
     * @param entity
     * @param serEntity
     * @param status
     * @param remark
     * @param obj
     */
    void updateStatus(WorkOrderEntity entity, CustomerServiceWorkNumberEntity serEntity, Integer status, String remark, Object obj);

    /**
     * 更新开票审核工单
     * @param workEntity
     * @param invEntity
     * @param orderEntity
     * @param dto
     * @param serEntity
     * @param accEntity
     * @param isNeedSendSms 是否需要发送短信
     */
    void updateInvOrderStatus(WorkOrderEntity workEntity, InvoiceOrderEntity invEntity, OrderEntity orderEntity, WorkOrderDTO dto, CustomerServiceWorkNumberEntity serEntity, MemberAccountEntity accEntity, boolean isNeedSendSms);

    /**
     * 更新办理核名工单
     * @param workEntity
     * @param regEntity
     * @param orderEntity
     * @param dto
     * @param serEntity
     * @param accEntity
     */
    Map<String, Object> updateRegOrderStatus(WorkOrderEntity workEntity, RegisterOrderEntity regEntity, OrderEntity orderEntity, WorkOrderDTO dto, CustomerServiceWorkNumberEntity serEntity, MemberAccountEntity accEntity);

    /**
     * @Description 取消工单
     * @Author  Kaven
     * @Date   2019/12/26 16:25
     * @Param  userAccount orderNo oemCode
     * @Exception BusinessException
    */
    void cancelWorkOrder(String userAccount, String orderNo,String oemCode) throws BusinessException;

    /**
     * 更新办理核名订单信息
     * @param regEntity
     * @param dto
     * @param serEntity
     */
    void updateRegOrder(RegisterOrderEntity regEntity, WorkOrderDTO dto, CustomerServiceWorkNumberEntity serEntity);

    /**
     * 保存开票历史记录
     * @param entity
     * @param invEntity
     * @param histCopyEntity
     * @param orderStatus
     * @param userAccount
     * @param hisRemark
     */
    void editAndSaveInvOrderHistory(WorkOrderEntity entity, InvoiceOrderEntity invEntity, InvoiceOrderEntity histCopyEntity, Integer orderStatus, String userAccount, String hisRemark);

    /**
     * 工单转派
     * @param id 工单id
     * @param customerServiceId 转派坐席id
     * @param remark 备注
     * @param updateUserId 操作人id
     * @param updateUser 操作人
     */
    void forward(Long id,Long customerServiceId,String remark,Long updateUserId,String updateUser);

    /**
     * 根据订单号查询工单信息
     * @param orderNo
     * @return
     */

    List<WorkOrderEntity> queryWorkOrderByOrderNo(String  orderNo);

    /**
     * 查询用户工单
     * @param workOrderQuery
     * @return
     */
    List<WorkOrderEntity> queryByUserId(WorkOrderQuery workOrderQuery);
}

