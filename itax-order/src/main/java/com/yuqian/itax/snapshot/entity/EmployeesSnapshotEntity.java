package com.yuqian.itax.snapshot.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 员工快照
 * 
 * @Date: 2020年10月26日 11:24:55 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_employees_snapshot")
public class EmployeesSnapshotEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 会员账号
	 */
	private String memberAccount;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 上级会员id
	 */
	private Long parentMemberId;
	
	/**
	 * 新增收益(分)
	 */
	private Long addRevenue;
	
	/**
	 * 会员状  1-正常 0-禁用 2-注销
	 */
	private Integer status;
	
	/**
	 * 注册时间
	 */
	private Date registTime;
	
	/**
	 * 快照时间
	 */
	private Date snapshotTime;
	
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
