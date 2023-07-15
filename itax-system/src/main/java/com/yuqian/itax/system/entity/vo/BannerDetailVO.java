package com.yuqian.itax.system.entity.vo;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.system.entity.BannerEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * banner图管理
 * 
 * @Date: 2019年12月08日 20:37:18 
 * @author 蒋匿
 */
@Getter
@Setter
public class BannerDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
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
	 *  所属OEM
	 */
	private String oemName;

	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
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

	public BannerDetailVO() {
	}

	public BannerDetailVO(BannerEntity entity, OemEntity oemEntity) {
		this.id = entity.getId();
		this.bannerAddress = entity.getBannerAddress();
		this.title = entity.getTitle();
		this.orderNum = entity.getOrderNum();
		this.isShare = entity.getIsShare();
		this.bannerLink = entity.getBannerLink();
		this.position = entity.getPosition();
		this.oemCode = entity.getOemCode();
		if (oemEntity != null) {
			this.oemName = oemEntity.getOemName();
		}
		this.addTime = entity.getAddTime();
		this.addUser = entity.getAddUser();
		this.remark = entity.getRemark();
		this.shareTitle = entity.getShareTitle();
		this.shareImageAddress = entity.getShareImageAddress();
	}
}
