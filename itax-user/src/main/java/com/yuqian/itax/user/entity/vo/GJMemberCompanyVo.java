package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * 我的企业
 * 
 * @Date: 2019年12月06日 10:42:12 
 * @author yejian
 */
@Getter
@Setter
public class GJMemberCompanyVo implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private Long memberId;

	/**
	 * 公司名称
	 */
	@Excel(name = "企业名称")
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 经营者名称
	 */
	@Excel(name = "经营者姓名")
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	@Excel(name = "企业类型", replace = { "-_null","个体户_1","个人独资企业_2","有限合伙公司_3","有限责任公司_4" }, height = 10, width = 22)
	@ApiModelProperty(value = "状态：1->个体户；2->个人独资企业；3->有限合伙公司；4->有限责任公司")
	private Integer companyType;

	/**
	 * 园区名称
	 */
	@Excel(name = "所属园区")
	@ApiModelProperty(value = "园区名称")
	private String parkName;


	/**
	 * 年度总开票金额
	 */
	@Excel(name = "年开票限额(元)")
	@ApiModelProperty(value = "年度总开票金额")
	private Long totalInvoiceAmount;

	/**
	 * 年度已开票金额
	 */
	@Excel(name = "本周期已开票(元)")
	@ApiModelProperty(value = "年度已开票金额")
	private Long useInvoiceAmount;

	/**
	 * 开票类目名称
	 */
	@ApiModelProperty(value = "开票类目名称")
	private String categoryName;

	/**
	 * 有效时间
	 */
	@Excel(name = "托管费到期日", replace = {
			"-_null" }, databaseFormat = "yyyyMMdd", format = "yyyy-MM-dd", height = 10, width = 20)
	@ApiModelProperty(value = "有效时间")
	@JSONField(format = "yyyy-MM-dd")
	private Date endTime;

	/**
	 * 年费状态：1未过期，2已过期
	 */
	@Excel(name = "托管费状态", replace = { "-_null", "正常_1", "即将过期 _2","已过期_3" }, width = 22)
	@ApiModelProperty(value = "托管费状态")
	private Integer overdue;

	/**
	 * 状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	@Excel(name = "企业状态", replace = { "-_null", "正常_1", "禁用 _2","已注销_4","注销中_5" }, width = 22)
	@ApiModelProperty(value = "状态：1->正常；2->禁用 4->已注销 5->注销中")
	private Integer status;

	/**
	 * 是否满额 0-否 1-是
	 */
	@ApiModelProperty(value = "是否满额 0->否 1->是")
	private Integer isTopUp;

	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	private Long parkId;

	/**
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码")
	private String parkCode;

	/**
	 * 年度可开票金额
	 */
	@ApiModelProperty(value = "年度可开票金额")
	private Long remainInvoiceAmount;

	/**
	 * 增值税减免额度
	 */
	@ApiModelProperty(value = "增值税减免额度")
	private Long vatBreaksAmount;

	/**
	 * 增值税减免周期 1-按月 2-按季度
	 */
	@ApiModelProperty(value = "增值税减免周期 1-按月 2-按季度")
	private Integer vatBreaksCycle;

	/**
	 * 个人所得税减免额度
	 */
	@ApiModelProperty(value = "个人所得税减免额度")
	private Long incomeTaxBreaksAmount;

	/**
	 * 个人所得税减免周期 1-按月 2-按季度
	 */
	@ApiModelProperty(value = "个人所得税减免周期 1-按月 2-按季度")
	private Long incomeTaxBreaksCycle;

	/**
	 * 增值税减免剩余额度
	 */
	@ApiModelProperty(value = "增值税减免剩余额度")
	private Long vatBreaksRemainAmount;

	/**
	 * 个人所得税减免剩余额度
	 */
	@ApiModelProperty(value = "个人所得税减免剩余额度")
	private Long incomeTaxBreaksRemainAmount;


	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

}
