package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 会员收件地址表
 * 
 * @Date: 2020年12月25日 14:35:49 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_address")
public class MemberAddressEntity implements Serializable {
	
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
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 收件人
	 */
	private String recipient;
	
	/**
	 * 联系电话
	 */
	private String recipientPhone;
	
	/**
	 * 详细地址
	 */
	private String recipientAddress;
	
	/**
	 * 状态  1-可用 0-不可用
	 */
	private Integer status;
	
	/**
	 * 省编码
	 */
	private String provinceCode;
	
	/**
	 * 省名称
	 */
	private String provinceName;
	
	/**
	 * 市编码
	 */
	private String cityCode;
	
	/**
	 * 市名称
	 */
	private String cityName;
	
	/**
	 * 区编码
	 */
	private String districtCode;
	
	/**
	 * 区名称
	 */
	private String districtName;
	
	/**
	 * 是否默认 0-不默认 1-默认
	 */
	private Integer isDefault;
	
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
