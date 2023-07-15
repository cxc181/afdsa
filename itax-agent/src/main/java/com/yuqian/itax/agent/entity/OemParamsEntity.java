package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 机构参数配置
 * 
 * @Date: 2019年12月06日 17:27:05 
 * @author Kaven
 */
@Getter
@Setter
@Table(name="sys_e_oem_params")
public class OemParamsEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 参数类型 1- 短信 2-微信支付 3-快捷支付
	 */
	private Integer paramsType;
	
	/**
	 * 账号
	 */
	private String account;
	
	/**
	 * 秘钥
	 */
	private String secKey;
	
	/**
	 * url地址
	 */
	private String url;
	
	/**
	 * 状态 0-不可用 1-可用
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
	 * 参数值
	 */
	private String paramsValues;
	
	/**
	 * 公钥
	 */
	private String publicKey;
	
	/**
	 * 私钥
	 */
	private String privateKey;
	
	
}
