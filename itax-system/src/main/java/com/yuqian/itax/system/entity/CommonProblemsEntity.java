package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 常见问题
 * 
 * @Date: 2019年12月08日 21:40:04 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_common_problems")
public class CommonProblemsEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 问题
	 */
	private String problem;
	
	/**
	 * 简答
	 */
	private String answer;
	
	/**
	 * 机构编码
	 */
	private String oemCode;


	/**
	 * 排序序号
	 */
	private Integer orderNum;
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
