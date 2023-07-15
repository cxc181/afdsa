package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 推广海报
 * 
 * @Date: 2020年08月07日 10:38:14 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_promotion_poster")
public class PromotionPosterEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 海报名称
	 */
	private String posterName;
	
	/**
	 * 排序号
	 */
	private Integer posterSn;
	
	/**
	 * 海报地址
	 */
	private String posterAddress;
	
	/**
	 * 二维码图片左边距(px)
	 */
	private Integer qrLeftMargin;
	
	/**
	 * 二维码图片上边距(px)
	 */
	private Integer qrTopMargin;
	
	/**
	 * 二维码图片宽(px)
	 */
	private Integer qrWidth;
	
	/**
	 * 二维码图片高(px)
	 */
	private Integer qrHeight;
	
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
