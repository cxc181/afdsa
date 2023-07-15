package com.yuqian.itax.workorder.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class WorkOrderQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;
    /**
     * 工单编号
     */
    private String workOrderNo;
    /**
     * 订单编号
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
     * 工单类型 工单类型 1- 办理核名 2-开票审核
     */
    private Integer workOrderType;
    /**
     * 工单状态
     * 工单状态 类型为开户： 0-待接单 1-处理中 2-已完成 3-已取消
     * 类型为开票： 0-待接单 1-审核中 2-审核通过 3-审核未通过'
     */
    private Integer workOrderStatus;
    /**
     * 我的工单查询的工单状态
     */
    private String  workOrderStatuss;
    /**
     * 处理人
     */
    private String processorName;
    /**
     * 处理人账号
     */
    private String processorAccount;

    /**
     * 坐席客服名称
     */
    private String customerServiceName;
    /**
     * 坐席客服账号
     */
    private String customerServiceAccount;
    /**
     * 坐席客服账号ID
     */
    private Long customerServiceId;

    /**
     * 0- 所有工单 1-我的工单
     */
    private Integer type;
    /**
     * 经营者姓名
     */
    private String operatorName;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 是否补充资料  (办理核名下使用  2否  1是)
     */
    private Integer isSupplement;

    /**
     * 人群标签名称
     */
    private String crowdLabelName;

    /**
     * 是否已全部赋码 0-否 1-是
     */
    private Integer isAllCodes;

    /**
     * 注册名称
     */
    private String registeredName;

    /**
     * 发票类型 1-增值税普通发票 2-增值税专用发票
     */
    private Integer invoiceType;

    /**
     * 是否支付 1已支付  2未支付
     */
    private Integer isPay;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 需过滤订单号
     */
    private String filterableOrder;

    /**
     *  企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    /**
     *  纳税人类型  1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType;

}
