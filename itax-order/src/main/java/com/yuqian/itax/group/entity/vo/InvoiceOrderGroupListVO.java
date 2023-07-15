package com.yuqian.itax.group.entity.vo;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 集团开票订单
 * 
 * @Date: 2020年03月04日 09:25:55 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceOrderGroupListVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	@Excel(name = "批量开票订单编号", width = 25)
	private String orderNo;

	/**
	 * 集团公司名称
	 */
	@Excel(name = "集团公司名称", width = 20)
	private String oemCompanyName;

	/**
	 * 抬头公司名称
	 */
	@Excel(name = "发票抬头公司", width = 20)
	private String companyName;

	/**
	 * 开票类目名称
	 */
	@Excel(name = "开票类目", width = 20)
	private String categoryGroupName;

	/**
	 * 开票类型名称
	 */
	@Excel(name = "开票类型", width = 15)
	private String invoiceTypeName;

	/**
	 * 发票类型 1-纸质发票 2-电子发票
	 */
	@Excel(name = "发票类型", replace = { "纸质版_1","电子版_2" })
	private Integer invoiceWay;

	/**
	 * 开票金额
	 */
	@Excel(name = "开票金额", width = 15)
	private BigDecimal invoiceAmount;

	/**
	 * 增值税税率
	 */
	@Excel(name = "增值税税率", suffix = "%")
	private String vatFeeRate;

	/**
	 * 增值税费
	 */
	@Excel(name = "增值税（元）")
	private BigDecimal vatFee;

	/**
	 * 所得税
	 */
	@Excel(name = "所得税（元）", width = 15)
	private BigDecimal personalIncomeTax;

	/**
	 * 附加税
	 */
	@Excel(name = "附加税（元）")
	private BigDecimal surcharge;

	/**
	 * 添加时间
	 */
	@Excel(name = "创建时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
	private Date addTime;

	/**
	 * 订单状态 0-流水解析中 1-出票中 2-已签收 3-已取消
	 */
	@Excel(name = "订单状态", replace = { "流水解析中_0","出票中_1","已签收_2","已取消_3","待财务审核_4" }, width = 15)
	private Integer orderStatus;

	/**
	 * 添加人
	 */
	@Excel(name = "创建账号")
	private String addUser;

	/**
	 * 机构名称
	 */
	@Excel(name = "OEM机构")
	private String oemName;

	/**
	 * 机构编码
	 */
	private String oemCode;
	/**
	 * 抬头收件人
	 */
	private String recipient;

	/**
	 * 抬头联系电话
	 */
	private String recipientPhone;

	/**
	 * 抬头详细地址
	 */
	private String recipientAddress;

	/**
	 * 抬头详细地址
	 */
	private String email;
	/**
	 * 增值税补缴
	 */
//	@Excel(name = "增值税补缴")
	private BigDecimal vatPayment;
	/**
	 * 附加税补缴
	 */
//	@Excel(name = "附加税补缴")
	private BigDecimal surchargePayment;
	/**
	 * 所得税补缴
	 */
//	@Excel(name = "所得税补缴")
	private BigDecimal incomeTaxPayment;

	/**
	 * 财务审核备注
	 */
	private String auditDesc;

    public InvoiceOrderGroupListVO() {

    }

    public InvoiceOrderGroupListVO(InvoiceOrderGroupEntity entity, OemEntity oem) {
    	this.orderNo = entity.getOrderNo();
    	this.orderStatus = entity.getOrderStatus();
    	this.addTime = entity.getAddTime();
    	this.addUser = entity.getAddUser();
    	this.companyName = entity.getCompanyName();
		this.categoryGroupName = entity.getCategoryGroupName();
		this.invoiceTypeName = entity.getInvoiceTypeName();
		this.recipient = entity.getRecipient();
		this.recipientPhone = entity.getRecipientPhone();
		this.recipientAddress = (entity.getProvinceName()==null?"":entity.getProvinceName()) + (entity.getCityName()==null?"":entity.getCityName())  + (entity.getDistrictName()==null?"":entity.getDistrictName()) + (entity.getRecipientAddress()==null?"":entity.getRecipientAddress());
		this.email=entity.getEmail();
		this.auditDesc = entity.getAuditDesc();
		if (oem != null) {
			oemCompanyName = oem.getCompanyName();
		}
    }
}
