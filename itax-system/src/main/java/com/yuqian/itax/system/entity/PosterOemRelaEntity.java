package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 推广海报与oem机构的关系表
 * 
 * @Date: 2020年08月07日 10:38:32 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_poster_oem")
public class PosterOemRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 海报id
	 */
	private Long posterId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
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
