package com.yuqian.itax.park.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区管理
 * 
 * @Date: 2019年12月07日 20:44:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park")
public class ParkEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 园区名称
	 */
	private String parkName;

	/**
	 * 园区编码
	 */
	private String parkCode;
	/**
	 * 所属城市
	 */
	private String parkCity;
	
	/**
	 * 服务内容
	 */
	private String serviceContent;
	
	/**
	 * 园区简介
	 */
	private String parkRecommend;
	
	/**
	 * 状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
	 */
	private Integer status;
	
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
	 * 邮寄费金额
	 */
	private Long postageFees;

	/**
	 * 所属企业名称
	 */
	private String belongsCompanyName;

	/**
	 * 所属企业地址
	 */
	private String belongsCompanyAddress;

	/**
	 * 统一社会信用代码
	 */
	private String ein;

	/**
	 * 收件人姓名
	 */
	private String recipient;

	/**
	 * 收件人手机号
	 */
	private String recipientPhone;

	/**
	 * 收件人省编码
	 */
	private String provinceCode;

	/**
	 * 收件人省名称
	 */
	private String provinceName;

	/**
	 * 收件人市编码
	 */
	private String cityCode;

	/**
	 * 收件人市名称
	 */
	private String cityName;

	/**
	 * 收件人区编码
	 */
	private String districtCode;

	/**
	 * 收件人区名称
	 */
	private String districtName;

	/**
	 * 收件人详细地址
	 */
	private String recipientAddress;

	/**
	 * 发票样例图片地址
	 */
	private String invoiceExample;

	/**
	 * 授权文件图片
	 */
	private String authorizationFile;

	/**
	 * 园区详细地址
	 */
	private String parkAddress;

	/**
	 * 核定说明
	 */
	private String verifyDesc;
	/**
	 * 开票人
	 */
	private String drawer;
	/**
	 * 收款人
	 */
	private String payee;
	/**
	 * 复核人
	 */
	private String reviewer;

	/**
	 * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
	 */
	private Integer processMark;

	/**
	 * 流程描述
	 */
	private String processDesc;

	/**
	 * 特殊事项说明
	 */
	private String specialConsiderations;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private Integer incomeLevyType;

	/**
	 * 公章图片地址
	 */
	private String officialSealImg;

	/**
	 * 园区类型 1-自营园区  2-合作园区 3-外部园区
	 */
	private Integer parkType;

	/**
	 * 用户评分
	 */
	private BigDecimal userRatings;

	/**
	 * 税收政策说明
	 */
	private String taxPolicyDesc;

	/**
	 * 园区预览图
	 */
	private String parkThumbnail;

	/**
	 * 园区详情顶部banner 图片
	 */
	private String parkImgs;

	/**
	 * 工商注册说明
	 */
	private String registerDesc;

	/**
	 * 税务办理说明
	 */
	private String taxHandleDesc;

	/**
	 * 对公户办理说明
	 */
	private String corporateAccountHandleDesc;

	/**
	 * 所属地区 1-江西
	 */
	private Integer affiliatingArea;

	/**
	 * 是否企业注册分润 0-否 1-是
	 */
	private Integer isRegisterProfit;

	/**
	 * 是否托管续费分润 0-否 1-是
	 */
	private Integer isRenewProfit;
}
