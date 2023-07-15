package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: ni.jiang
 *  @Date: 2020/12/31 14:19
 *  @Description: 开票记录查询实体类
 */
@Getter
@Setter
public class InvoiceRecordQuery extends BaseQuery implements Serializable {

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    /**
     * 开票企业
     */
    @ApiModelProperty(value = "开票企业")
    private String companyName;

    /**
     * 用户姓名
     */
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;

    /**
     * 开票记录状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中
     */
    @ApiModelProperty(value = "开票记录状态: 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中")
    private Integer status;

    /**
     * 开票记录状态(多状态) 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中
     */
    private String statuss;

    /**
     * 园区名称
     */
    @ApiModelProperty(value = "园区名称")
    private String parkName;

    /**
     * 园区Id
     */
    private Long parkId;

    /**
     * 抬头公司
     */
    @ApiModelProperty(value = "抬头公司")
    private String headCompanyName;

    /**
     * 开票记录编码
     */
    @ApiModelProperty(value = "开票记录编码")
    private String invoiceRecordNo;

    /**
     * 开票金额最小值(分）
     */
    @ApiModelProperty(value = "开票金额最小值")
    private Long invoiceMountMin;

    /**
     * 开票金额最大值(分）
     */
    @ApiModelProperty(value = "开票金额最大值")
    private Long invoiceMountMax;

    /**
     * 发票类型 1-纸质发票 2-电子发票
     */
    @ApiModelProperty(value = "发票类型 1-纸质发票 2-电子发票")
    private Integer invoiceWay;

    /**
     * 开票类型 1-增值税普通发票 2-增值税专用发票
     */
    @ApiModelProperty(value = "开票类型 1-增值税普通发票 2-增值税专用发票")
    private Integer invoiceType;

    /**
     * 处理方式 1-线下、2-托管
     */
    @ApiModelProperty(value = "处理方式 1-线下、2-托管")
    private Integer handlingWay;

    /**
     * 开票方式 1-自助开票 2-集团代开 3-佣金开票
     */
    @ApiModelProperty(value = "开票方式 1-自助开票 2-集团代开 3-佣金开票 4-消费开票")
    private Integer createWay;

    /**
     * 集团开票订单编号
     */
    @ApiModelProperty(value = "集团开票订单编号")
    private String groupOrderNo;

    /**
     * 出票开始时间
     */
    @ApiModelProperty(value = "出票开始时间")
    private String ticketTimeStart;

    /**
     * 出票结束时间
     */
    @ApiModelProperty(value = "出票结束时间")
    private String ticketTimeEnd;


    /**
     * 数据权限
     */
    private String tree;
}