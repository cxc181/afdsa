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
public class MemberCompanyVo implements Serializable {
	
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
	 * 会员账号
	 */
	@Excel(name = "会员账号")
	@ApiModelProperty(value = "会员账号")
	private String memberAccount;

	/**
	 * 用户名称
	 */
	@Excel(name = "用户姓名")
	@ApiModelProperty(value = "用户名称")
	private String realName;

	/**
	 * 公司名称
	 */
	@Excel(name = "企业名称")
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 税号
	 */
	@Excel(name = "税号")
	@ApiModelProperty(value = "税号")
	private String ein;

	/**
	 * 纳税人类型
	 */
	@ApiModelProperty(value = "纳税人类型")
	@Excel(name = "纳税性质", replace = { "-_null","小规模纳税人_1","一般纳税人_2" }, height = 10, width = 22)
	private Integer taxpayerType;

	/**
	 * 经营者/法人
	 */
	@Excel(name = "经营者/法人")
	@ApiModelProperty(value = "经营者/法人")
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
	 * 出证时间
	 */
	@Excel(name = "出证时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
	@ApiModelProperty(value = "出证时间")
	private Date addTime;

	/**
	 * 年度总开票金额
	 */
//	@Excel(name = "年开票限额(元)")
	@ApiModelProperty(value = "年度总开票金额")
	private Long totalInvoiceAmount;

	/**
	 * 近12个月已开票金额（元）
	 */
	@Excel(name = "近12个月已开票金额（元）")
	@ApiModelProperty(value = "近12个月已开票金额（元）")
	private Long useInvoiceAmount;

	/**
	 * 开票类目id
	 */
	@ApiModelProperty(value = "开票类目id")
	private String categoryBaseId;

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
	 * 税务登记日期
	 */
	@Excel(name = "税务登记日期", replace = {
			"-_null" }, databaseFormat = "yyyyMMdd", format = "yyyy-MM-dd", height = 10, width = 20)
	@JSONField(format = "yyyy-MM-dd")
	private Date taxRegDate;
	/**
	 * 年费状态：1未过期，2已过期
	 */
	@Excel(name = "托管费状态", replace = { "-_null", "正常_1", "即将过期 _2","已过期_3" }, width = 22)
	@ApiModelProperty(value = "托管费状态")
	private Integer overdue;

	/**
	 * 状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	@Excel(name = "企业状态", replace = { "-_null", "正常_1", "禁用 _2","已税务注销_4","注销中_5","已工商注销_6" }, width = 22)
	@ApiModelProperty(value = "状态：1->正常；2->禁用 4->已税务注销 5->注销中 6->已工商注销")
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
	 * 所属会员账号
	 */
	@ApiModelProperty(value = "所属会员账号")
	private String memberPhone;

	/**
	 * 所属会员名称
	 */
	@ApiModelProperty(value = "所属会员名称")
	private String memberName;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

	/**
	 * 托管状态
	 */
	@Excel(name = "托管状态", replace = { "-_null", "未托管_0", "已托管_1" }, width = 22)
	private Integer hostingStatus;

	/**
	 * 机构名称
	 */
	@Excel(name = "所属OEM")
	@ApiModelProperty(value = "机构名称")
	private String oemName;

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
	 * 开票类目id
	 */
	private Long cateId;

	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

	/**
	 * 税务盘编号
	 */
	private String taxDiscCode;
	/**
	 * 税务盘类型 1-ukey 2-税控盘
	 */
	private Integer taxDiscType;
	/**
	 * 票面金额(分)
	 */
	private Long faceAmount;
	/**
	 * 票面金额类型 1-1w 2-10w 3-100w
	 */
	private Integer faceAmountType;
	/**
	 * 通道方 1-百旺
	 */
	private String channel;

	/**
	 * 过期状态 1-正常 2-即将过期 3-已过期
	 */
	//@Excel(name = "托管费状态", replace = { "-_null", "正常_1", "即将过期 _2","已过期_3" }, width = 22)
	@ApiModelProperty(value = "状态：1->正常；2->即将过期；3->已过期")
	private Integer overdueStatus;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private Integer incomeLevyType;

	/**
	 * 是否存在可用续费产品 0-否 1-是
	 */
	private Integer isExistRenewProduct;

	/**
	 * 委托注册协议地址
	 */
	private String userAgreementAddr;

	/**
	 * 续费产品状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer renewProductStatus;
}
