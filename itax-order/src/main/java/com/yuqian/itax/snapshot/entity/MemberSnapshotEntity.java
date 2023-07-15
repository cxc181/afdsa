package com.yuqian.itax.snapshot.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 会员快照表
 * 
 * @Date: 2020年10月26日 11:24:47 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_snapshot")
public class MemberSnapshotEntity implements Serializable {
	
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
	 * 上级钻石id
	 */
	private Long upDiamondId;
	
	/**
	 * 所属员工id
	 */
	private Long attributionEmployeesId;
	
	/**
	 * 上上级钻石id
	 */
	private Long superDiamondId;
	
	/**
	 * 上上级员工id
	 */
	private Long superEmployeesId;
	
	/**
	 * 当前等级  0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5城市服务商
	 */
	private Integer levelNo;
	
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
