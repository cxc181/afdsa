package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * banner图管理
 * 
 * @Date: 2019年12月08日 20:37:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_banner")
public class BannerEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * banner地址
	 */
	private String bannerAddress;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 排序
	 */
	private Integer orderNum;
	
	/**
	 * 是否显示 0-不显示 1-显示
	 */
	private Integer isShow;

	/**
	 * 是否分享 0-否 1-是
	 */
	private Integer isShare;
	
	/**
	 * 链接地址
	 */
	private String bannerLink;
	
	/**
	 * 显示位置 1-首页 2-我的 
	 */
	private Integer position;

	/**
	 *  机构编码
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

	/**
	 * 分享标题
	 */
	private String shareTitle;

	/**
	 * 分享图地址
	 */
	private String shareImageAddress;
}
