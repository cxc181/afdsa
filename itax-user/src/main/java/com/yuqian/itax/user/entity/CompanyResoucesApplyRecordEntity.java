package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

/**
 * 企业资源申请记录
 *
 * @Date: 2020年03月25日 09:30:46 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_resouces_apply_record")
public class CompanyResoucesApplyRecordEntity implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 申请类型  1-领用  2-归还
	 */
	private Integer applyType;

	/**
	 * 资源类型 1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章 ，多个资源直接用 逗号分割
	 */
	private String applyResouces;

	/**
	 * 状态  1-待发货  2-出库中 3-待签收 4-已签收 5-已取消
	 */
	private Integer status;

	/**
	 * 快递费
	 */
	private Long postageFees;

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
	 * 快递单号
	 */
	private String courierNumber;

	/**
	 * 快递公司名称
	 */
	private String courierCompanyName;

	/**
	 * 签收人id
	 */
	private Long signUserId;

	/**
	 * 签收人类型 1-会员 2-系统用户
	 */
	private Integer signUserType;

	/**
	 * 签收资源 1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章  多个资源直接用 逗号 分割
	 */
	private String signResouces;

	/**
	 * 签收时间
	 */
	private Date signTime;

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


}
