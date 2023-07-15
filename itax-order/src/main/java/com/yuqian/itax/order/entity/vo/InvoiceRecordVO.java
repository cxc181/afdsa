package com.yuqian.itax.order.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  @Author: ni.jiang
 *  @Date: 2020/12/31 14:19
 *  @Description: 开票记录返回结果
 */
@Getter
@Setter
public class InvoiceRecordVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 开票记录id
	 */
	@ApiModelProperty(value = "开票记录id")
	private Long id;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	@Excel(name="订单号")
	private String orderNo;

	/**
	 * 开票公司id
	 */
	@ApiModelProperty(value = "开票公司id")
	private Long companyId;

	/**
	 * 开票公司名称
	 */
	@ApiModelProperty(value = "开票公司名称")
	@Excel(name="开票公司名称")
	private String companyName;

	/**
	 * 用户姓名
	 */
	@ApiModelProperty(value = "用户姓名")
	@Excel(name="用户姓名")
	private String userName;

	/**
	 * 注册手机号
	 */
	@ApiModelProperty(value = "注册手机号")
	@Excel(name="注册手机号")
	private String regPhone;

	/**
	 * 开票记录编号
	 */
	@ApiModelProperty(value = "开票记录编号")
	@Excel(name="开票记录编号")
	private String invoiceRecordNo;

	/**
	 * 开票记录状态: 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中
	 */
	@ApiModelProperty(value = "开票记录状态: 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中")
	@Excel(name = "状态", replace = { "-_null","待提交_0","人工出票_1","待补票_2","出票中断_3","出票失败_4","推送失败_5","待确认_6","已完成_7","已取消_8","出票中_9"}, height = 10, width = 22)
	private Integer status;

	/**
	 * 处理方式 1-线下、2-托管
	 */
	@ApiModelProperty(value = "处理方式 1-线下、2-托管")
	@Excel(name = "处理方式", replace = { "-_null","线下_1","托管_2"}, height = 10, width = 22)
	private Integer handlingWay;

	/**
	 * 开票方式
	 */
	@ApiModelProperty(value = "开票方式")
	@Excel(name = "开票方式", replace = { "-_null","自助开票_1","集团代开_2","佣金开票_3","消费开票_4"}, height = 10, width = 22)
	private Integer createWay;

	/**
	 * 发票抬头公司名称
	 */
	@ApiModelProperty(value = "发票抬头公司名称")
	@Excel(name="抬头公司")
	private String invHeadCompanyName;

	/**
	 * 开票类型 1-增值税普通发票 2-增值税专用发票
	 */
	@ApiModelProperty(value = "开票类型 1-增值税普通发票 2-增值税专用发票")
	@Excel(name = "处理方式", replace = { "-_null","增值税普通发票_1","增值税专用发票_2"}, height = 10, width = 30)
	private Integer invoiceType;

	/**
	 * 发票类型 1-纸质发票 2-电子发票
	 */
	@ApiModelProperty(value = "发票方式 1-纸质发票 2-电子发票")
	@Excel(name = "发票类型", replace = { "-_null","纸质发票_1","电子发票_2"}, height = 10, width = 30)
	private Integer invoiceWay;

	/**
	 * 开票金额（分）
	 */
	@ApiModelProperty(value = "开票金额")
	private Long invoiceAmount;

	/**
	 * 导出的开票金额（元）
	 */
	@Excel(name="开票金额")
	private BigDecimal exportInvoiceAmount;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	@Excel(name="园区")
	private String parkName;

	/**
	 * 描述
	 */
	@ApiModelProperty(value = "描述")
	@Excel(name="描述")
	private String invoiceDesc;

	/**
	 * 集团开票订单号
	 */
	@ApiModelProperty(value = "集团开票订单号")
	@Excel(name="批量开票订单编号")
	private String groupOrderNo;

	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	@Excel(name = "创建时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
	private Date addTime;

	/**
	 * 出票日期
	 */
	@ApiModelProperty(value = "出票日期")
	@JSONField(format = "yyyy-MM-dd")
	@Excel(name = "出票日期", replace = {
			"-_null" }, databaseFormat = "yyyyMMdd", format = "yyyy-MM-dd", height = 10, width = 20)
	private Date ticketTime;

	/**
	 * 完成时间
	 */
	@ApiModelProperty(value = "完成时间")
	@Excel(name = "完成时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
	private Date completeTime;

	/**
	 * 客服备注
	 */
	@Excel(name="客服备注")
	private String remark;

	/**
	 * 1-线上支付 2-线下支付
	 */
	private String payType;

}
