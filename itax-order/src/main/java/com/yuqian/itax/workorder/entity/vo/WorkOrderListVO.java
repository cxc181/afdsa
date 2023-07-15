package com.yuqian.itax.workorder.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class WorkOrderListVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 工单号
     */
    private String workOrderNo;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 用户注册账号
     */
    private String memberAccount;

    /**
     * 订单类型 1- 工商开户 2-开票
     */
    private Integer orderType;

    /**
     * 工单类型 1- 办理核名 2-开票审核
     */
    private Integer workOrderType;

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
     * 处理人名称
     */
    private String processorName;

    /**
     * 处理人账号
     */
    private String processorAccount;
    /**
     * 坐席名称
     */
    private String customerServiceName;

    /**
     * 坐席客户账号
     */
    private String customerServiceAccount;

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
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

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
     * 经营者姓名
     */
    private String operatorName;

    /**
     * 园区名称
     */
    private String parkName;
    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * 纳税人类型  1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType;

}
