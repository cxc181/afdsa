package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 渠道信息表
 * 
 * @Date: 2021年04月27日 16:38:21 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_channel_info")
public class ChannelInfoEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 渠道编码
	 */
	private String channelCode;
	
	/**
	 * 渠道名称
	 */
	private String channelName;

	/**
	 * 渠道图标oss地址
	 */
	private String channelLogo;

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
	 * appid
	 */
	private String appId;
}
