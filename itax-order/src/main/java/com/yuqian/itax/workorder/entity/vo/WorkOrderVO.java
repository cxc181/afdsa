package com.yuqian.itax.workorder.entity.vo;

import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class WorkOrderVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 工单编号
     */
    private String workOrderNo;

    /**
     * 工单类型 1- 办理核名 2-开票审核
     */
    private Integer workOrderType;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单类型 1- 工商开户 2-开票
     */
    private Integer orderType;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 所在省份
     */
    private String provinceName;

    /**
     * 所在城市
     */
    private String cityName;

    /**
     * 工单状态 类型为开户： 0-待接单 1-处理中 2-已完成 3-已取消
     类型为开票： 0-待接单 1-审核中 2-审核通过 3-审核未通过
     */
    private Integer workOrderStatus;

    /**
     * 工单备注
     */
    private String workOrderDesc;

    /**
     * 处理坐席
     */
    private String seatName;

    /**
     * 处理人名称
     */
    private String processorName;

    /**
     * 处理人账号
     */
    private String processorAccount;

    /**
     * 处理人类型 0-本人 1-客服 2-经办人 3-管理员 4-工号
     */
    private Integer processorType;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;

    /**
     * 备注
     */
    private String remark;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 机构名称
     */
    private String oemName;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * H5接入方名称
     */
    private String accessPartyName;

    public WorkOrderVO() {

    }
    public WorkOrderVO(WorkOrderEntity entity) {
        this.id = entity.getId();
        this.workOrderNo = entity.getWorkOrderNo();
        this.workOrderType = entity.getWorkOrderType();
        this.orderNo = entity.getOrderNo();
        this.orderType = entity.getOrderType();
        this.workOrderStatus = entity.getWorkOrderStatus();
        this.workOrderDesc = entity.getWorkOrderDesc();
        this.processorName = entity.getProcessorName();
        this.processorAccount = entity.getProcessorAccount();
        this.processorType = entity.getProcessorType();
        this.addTime = entity.getAddTime();
        this.addUser = entity.getAddUser();
        this.remark = entity.getRemark();
        this.oemCode = entity.getOemCode();
        this.oemName = entity.getOemName();
        this.seatName = entity.getCustomerServiceName();
    }
}
