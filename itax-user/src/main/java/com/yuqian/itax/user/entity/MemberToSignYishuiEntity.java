package com.yuqian.itax.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 会员签约易税表
 * 
 * @Date: 2023/02/20
 * @author lmh
 */
@Getter
@Setter
@Table(name="t_e_member_to_sign_yishui")
public class MemberToSignYishuiEntity implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 签约id
	 */
	private Long enterpriseProfessionalFacilitatorId;

	/**
	 * 人员id
	 */
	private Long professionalId;

	/**
	 * 人员编号
	 */
	private String professionalSn;

	/**
	 * 身份证号
	 */
	private String idCardNo;

	/**
	 * 签约状态 0未签约 1已签约 2已解约
	 */
	private Integer isContract;

	/**
	 * 签约开始时间
	 */
	private Date contractStartTime;

	/**
	 * 签约结束时间
	 */
	private Date contractEndTime;

	/**
	 * 是否认证 0未认证 1已认证
	 */
	private Integer isAuth;

	/**
	 * 认证时间
	 */
	private Date authTime;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 新增时间
	 */
	private Date createTime;

	/**
	 * 创建人账号
	 */
	private String createUser;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 修改人账号
	 */
	private String updateUser;

	/**
	 * 备注
	 */
	private String remark;
	
}
