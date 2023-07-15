package com.yuqian.itax.agreement.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 协议模板表
 * 
 * @Date: 2022年02月11日 17:13:24 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_agreement_template")
public class AgreementTemplateEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 模板编码
	 */
	private String templateCode;
	
	/**
	 * 模板名称
	 */
	private String templateName;
	
	/**
	 * 模板类型  1-收费标准 2-委托注册协议  3-园区办理协议
	 */
	private Integer templateType;
	
	/**
	 * 模板内容
	 */
	private String templateContent;
	
	/**
	 * 模板html地址(相对地址，公域)
	 */
	private String templateHtmlUrl;
	
	/**
	 * 模板状态  1-启用 2-禁用
	 */
	private Integer templateStatus;
	
	/**
	 * 模板说明
	 */
	private String templateDesc;
	
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
	 * 模板显示名称
	 */
	private String templateShowName;
}
