package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.RegisterOrderChangeRecordEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工商注册订单变更记录
 * 
 * @Date: 2019年12月07日 19:54:45 
 * @author 蒋匿
 */
@Getter
@Setter
public class RegisterOrderChangeRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 订单状态 0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成
	 */
	private Integer orderStatus;
	
	/**
	 * 经营者姓名
	 */
	private String operatorName;
	
	/**
	 * 字号
	 */
	private String shopName;
	
	/**
	 * 组织形式  -- 暂时预留
	 */
	private Integer organizationForm;
	
	/**
	 * 行业类型id
	 */
	private Long industryId;
	
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
	 * 身份证号码
	 */
	private String idCardNumber;
	
	/**
	 * 核定税种
	 */
	private String ratifyTax;
	
	/**
	 * 经营范围
	 */
	private String businessScope;
	
	/**
	 * 注册名称
	 */
	private String registeredName;
	
	/**
	 * 支付订单编号
	 */
	private String payOrderNo;
	
	/**
	 * 签名单
	 */
	private String signImg;
	
	/**
	 * 订单金额
	 */
	private Long orderAmount;
	
	/**
	 * 优惠金额
	 */
	private Long discountAmount;
	
	/**
	 * 支付金额
	 */
	private Long payAmount;
	
	/**
	 * 登记文件
	 */
	private String registFile;
	
	/**
	 * 通知次数
	 */
	private Integer alertNumber;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
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
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;

	/**
	 * 图片地址
	 */
	private List<String> imgUrl;

}
