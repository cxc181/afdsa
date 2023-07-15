package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.vo.UnmatchedBusinessScopeVO;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import com.yuqian.itax.user.entity.vo.CompanyCorePersonnelVO;
import com.yuqian.itax.util.util.StringHandleUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/11 19:40
 *  @Description: 工商注册订单展示实体类
 */
@Getter
@Setter
public class RegisterDetailOrderVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 经营者姓名
	 */
	private String operatorName;

	/**
	 * 字号
	 */
	private String shopName;

	/**
	 * 备选字号1
	 */
	private String shopNameOne;

	/**
	 * 备选字号2
	 */
	private String shopNameTwo;

	/**
	 * 行业类型id
	 */
	private Long industryId;

	/**
	 * 行业类型
	 */
	private String industryName;

	/**
	 * 经营地址 网址
	 */
	private String businessAddress;

	/**
	 * 联系电话
	 */
	private String contactPhone;

	/**
	 * 电子邮箱
	 */
	private String email;

	/**
	 * 身份证正面
	 */
	private String idCardFront;

	/**
	 * 身份证反面
	 */
	private String idCardReverse;

	/**
	 * 监视姓名
	 */
	private String supervisorName;
	/**
	 * 监视联系电话
	 */
	private String supervisorPhone;
	/**
	 * 监事身份证正面
	 */
	private String supervisorIdCardFront;

	private String supervisorIdCardFrontUrl;

	/**
	 * 监事身份证反面
	 */
	private String supervisorIdCardReverse;

	private String supervisorIdCardReverseUrl;

	/**
	 * 财务姓名
	 */
	private String financeName;
	/**
	 * 财务联系电话
	 */
	private String financePhone;
	/**
	 * 财务身份证正面
	 */
	private String financeIdCardFront;

	private String financeIdCardFrontUrl;

	/**
	 * 财务身份证反面
	 */
	private String financeIdCardReverse;

	private String financeIdCardReverseUrl;

	/**
	 * 身份证号码
	 */
	private String idCardNumber;
	/**
	 * 身份证地址
	 */
	private String idCardAddr;
	/**
	 * 核定税种
	 */
	private String ratifyTax;

	/**
	 * 注册名称
	 */
	private String registeredName;

	/**
	 * 签名单
	 */
	private String signImg;

	/**
	 * 登记文件
	 */
	private String registFile;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
	 */
	private Integer companyType;

	/**
	 * 短视频地址
	 */
	private String videoAddr;

	/**
	 * 专属客服电话
	 */
	private String customerServicePhone;

	/**
	 * 订单状态
	 */
	private Integer orderStatus;

	/**
	 * 园区名称
	 */
	private String parkName;

	/**
	 * 企业开票类目列表
	 */
	List<CompanyInvoiceCategoryEntity> invoiceCategoryList;

	/**
	 * 经营范围列表
	 */
	List<BusinessScopeEntity> businessScopeList;

	/**
	 * 开票类目列表
	 */
	private List<String> categoryNames;

	/**
	 * 经营范围
	 */
	private String businessScope;

	/**
	 * 经办人账号
	 */
	private String agentAccount;

	/**
	 * 示例名称（个体名称后缀）
	 */
	private String exampleName;

	/**
	 * 园区属地
	 */
	private String parkCity;

	/**
	 * 园区编码
	 */
	private String parkCode;

	/**
	 * 是否已开启身份验证 0-未开启 1-已开启
	 */
	private Integer isOpenAuthentication;

	/**
	 * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
	 */
	private Integer processMark;

	/**
	 * 身份证正面(短)
	 */
	private String idCardFrontFileName;

	/**
	 * 身份证反面(短)
	 */
	private String idCardReverseFileName;

	/**
	 * 是否已全部赋码 0-否 1-是
	 */
	private Integer isAllCodes;

	/**
	 * 行业经验范围
	 */
	private String industryBusinessScope;

	/**
	 * 自选经验范围
	 */
	private String ownBusinessScope;

	/**
	 * 税费分类编码对应的经验范围
	 */
	private String taxcodeBusinessScope;

	/**
	 * 赋码后新增经营范围
	 */
	private String codeBusinessScope;

	/**
	 * 接入方id
	 */
	private Long accessPartyId;

	/**
	 * 未匹配到的经营范围
	 */
	List<UnmatchedBusinessScopeVO> unmatchedBusinessScopeVOS;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 股东信息
	 */
	List<CompanyCorePersonnelVO> shareholderInfoList;

	public RegisterDetailOrderVO(RegisterOrderEntity regEntity) {
		this.shopName = regEntity.getShopName();
		this.shopNameOne = regEntity.getShopNameOne();
		this.shopNameTwo = regEntity.getShopNameTwo();
		this.businessAddress = regEntity.getBusinessAddress();
		this.contactPhone = regEntity.getContactPhone();
		this.taxpayerType = regEntity.getTaxpayerType();
		this.email = regEntity.getEmail();
		this.idCardFront = regEntity.getIdCardFront();
		this.idCardReverse = regEntity.getIdCardReverse();
		this.idCardNumber = regEntity.getIdCardNumber();
		this.signImg = regEntity.getSignImg();
		this.registFile = regEntity.getRegistFile();
		this.remark = regEntity.getRemark();
		this.companyType = regEntity.getCompanyType();
		this.videoAddr = regEntity.getVideoAddr();
		this.customerServicePhone = regEntity.getCustomerServicePhone();
		this.orderNo = regEntity.getOrderNo();
		this.operatorName = regEntity.getOperatorName();
		this.registeredName = regEntity.getRegisteredName();
		this.industryId = regEntity.getIndustryId();
		this.agentAccount = regEntity.getAgentAccount();
		this.ratifyTax = regEntity.getRatifyTax();
		String scope = regEntity.getBusinessScope();
		scope = scope.replaceAll(",",";");
		this.businessScope = scope;
		this.idCardAddr = regEntity.getIdCardAddr();
		this.exampleName = StringHandleUtil.removeStar(regEntity.getExampleName());
		this.isOpenAuthentication = regEntity.getIsOpenAuthentication();
		this.isAllCodes = regEntity.getIsAllCodes();
		this.industryBusinessScope = regEntity.getIndustryBusinessScope();
		this.ownBusinessScope = regEntity.getOwnBusinessScope();
		this.taxcodeBusinessScope = regEntity.getTaxcodeBusinessScope();
		this.registeredCapital = regEntity.getRegisteredCapital();
	}

	public RegisterDetailOrderVO() {

	}
}
