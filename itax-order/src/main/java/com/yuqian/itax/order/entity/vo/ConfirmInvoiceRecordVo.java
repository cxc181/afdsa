package com.yuqian.itax.order.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConfirmInvoiceRecordDTO
 * @Description TODO
 * @Author jiangni
 * @Date 2021/1/4
 * @Version 1.0
 */
@Data
public class ConfirmInvoiceRecordVo implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 开票记录id
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 开票公司id
     */
    private Long companyId;

    /**
     * 开票公司名称
     */
    private String companyName;

    /**
     * 开票记录编号
     */
    private String invoiceRecordNo;

    /**
     * 开票记录状态: 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消
     */
    private Integer status;

    /**
     * 处理方式 1-线下、2-托管
     */
    private Integer handlingWay;

    /**
     * 开票类型 1-增值税普通发票 2-增值税专用发票
     */
    private Integer invoiceType;

    /**
     * 开票金额（分）
     */
    private Long invoiceAmount;

    /**
     * 开票方式 1-自助开票 2-集团代开 3-佣金开票
     */
    private Integer createWay;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 描述
     */
    private String invoiceDesc;

    /**
     * 集团开票订单号
     */
    private String groupOrderNo;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 开票订单创建时间
     */
    private Date invoiceOrderAddTime;

    /**
     * 完成时间
     */
    private Date completeTime;

    /**
     * 出票日期
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date ticketTime;

    /**
     * 所属季度周期
     */
    private List<Map<String,String>> taxSeasonal;
}
