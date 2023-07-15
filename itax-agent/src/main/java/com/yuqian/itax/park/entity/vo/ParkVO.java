package com.yuqian.itax.park.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 11:21
 *  @Description: 园区信息展示类
 */
@Getter
@Setter
public class ParkVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "产品ID",required = true)
	private Long parkId;
	
	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称",required = true)
	private String parkName;

	/**
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码",required = true)
	private String parkCode;
	
	/**
	 * 所属城市
	 */
	@ApiModelProperty(value = "所属城市",required = true)
	private String parkCity;
	
	/**
	 * 服务内容
	 */
	@ApiModelProperty(value = "服务内容",required = true)
	private String serviceContent;
	
	/**
	 * 园区简介
	 */
	@ApiModelProperty(value = "园区简介",required = true)
	private String parkRecommend;
	
	/**
	 * 状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
	 */
	@ApiModelProperty(value = "状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除",required = true)
	private Integer status;

	@ApiModelProperty(value = "办理要求",required = true)
	private String transactRequire;// 办理要求

	@ApiModelProperty(value = "增值税个税减免额度",required = true)
	private Long vatBreaksAmount;// 增值税个税减免额度

	@ApiModelProperty(value = "增值税个税减免周期",required = true)
	private Integer vatBreaksCycle;// 增值税个税减免周期

	@ApiModelProperty(value = "个税优惠政策",required = true)
	private String incomeTaxBreaksAmount;// 个税优惠政策

	@ApiModelProperty(value = "所得税减免周期",required = true)
	private Integer incomeTaxBreaksCycle;// 所得税减免周期

	@ApiModelProperty(value = "年度开票总额",required = true)
	private Long totalInvoiceAmount;//年度开票总额

	private String policyFileUrl;// 政策文件地址

	private String belongsCompanyName;// 园区运营主体

	private String authorizationFile;// 授权文件地址

	private String parkAddress;// 园区详细地址

	private String verifyDesc;// 核定说明

	private String parkPolicyDesc;// 园区政策说明

	/**
	 * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
	 */
	private Integer processMark;

	/**
	 * 流程描述
	 */
	private String processDesc;

	/**
	 * 所属地区 1-江西
	 */
	private Integer affiliatingArea;
}
