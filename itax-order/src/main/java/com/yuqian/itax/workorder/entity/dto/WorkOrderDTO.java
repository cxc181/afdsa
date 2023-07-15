package com.yuqian.itax.workorder.entity.dto;

import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import com.yuqian.itax.util.util.StringHandleUtil;
import com.yuqian.itax.util.validator.Phone;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
 * 工单
 * 
 * @Date: 2019年12月07日 20:00:45 
 * @author 蒋匿
 */
@Getter
@Setter
public class WorkOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@NotNull(message="主键不能为空")
	private Long id;

	/**
	 * 经营者手机号
	 */
	@Size(max = 11, message = "经营者手机号不能超过11位字符")
	@Phone
	private String phone;

	/**
	 * 操作类型 1-基础信息 2-名称确认 3-经营范围确认
	 */
	private Integer operType;
	/**
	 * 电子邮箱
	 */
	@Size(max = 30, message = "电子邮箱不能超过30位字符")
	@Email(message = "电子邮箱格式有误", regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
	private String email;

	/**
	 * 行业类型id
	 */
	@Min(value = 0, message = "行业类型主键有误")
	@Max(value = 10000, message = "行业类型主键有误")
	private Long industryId;

	/**
	 * 企业确认名称
	 */
	@Size(max = 30, message = "企业确认名称不能超过30位字符")
	private String registeredName;

	/**
	 * 字号
	 */
	@Size(max = 30, message = "字号不能超过30位字符")
	private String shopName;

	/**
	 * 备选字号1
	 */
	@Size(max = 30, message = "备选字号1不能超过30位字符")
	private String shopNameOne;

	/**
	 * 备选字号2
	 */
	@Size(max = 30, message = "备选字号2不能超过30位字符")
	private String shopNameTwo;

	/**
	 * 经办人账号
	 */
	@Size(max = 30, message = "经办人账号不能超过30位字符")
	private String agentAccount;

	/**
	 * 审核状态
	 */
	@NotNull(message="操作状态有误")
	@Min(value = 1, message = "操作状态有误")
	@Max(value = 3, message = "操作状态有误")
	private Integer status;

	/**
	 * 备注
	 */
	@Size(max = 200, message = "备注不能超过100位字符")
	private String remark;

	/**
	 * 订单状态
	 */
	private Integer orderStatus;

	/**
	 * 开票类目
	 */
	private String categoryName;

	/**
	 * 经营范围
	 */
	private String businessScope;

	/**
	 * 自选经营范围
	 */
	@Size(max = 300, message = "自选经营范围不能超过100位字符")
	private String ownBusinessScope;

	/**
	 * 开票类目集合
	 */
	private List<InvoiceCategoryBaseStringVO> categoryList;

	/**
	 * 示例名称（个体名称后缀）
	 */
	@Size(max = 61, message = "个体名称后缀不能超过64位字符")
	private String exampleName;

	/**
	 * 园区属地
	 */
	private String parkCity;

	/**
	 * 是否立即开票 1-立即出票 0-不立即出票
	 */
	private Integer isImmediatelyInvoiceStatus;

	/**
	 * 驳回项，多个项之间用逗号分割 1-字号 2-身份证 3-视频
	 */
	private String rejectedItem;

	/**
	 * 使用的字号
	 */
	private String userShopName;

/*	public void setBusinessScope(String businessScope) {
		this.businessScope = StringHandleUtil.replace(businessScope.replaceAll("[^,，*＊a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
	}*/

	public void setExampleName(String exampleName) {
		exampleName = StringHandleUtil.removeStar(exampleName);
		if (StringUtils.isBlank(exampleName)) {
			return;
		}
		this.exampleName = "***" + exampleName;
	}

}
