package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 广告位列表页返回对象
 * @author：pengwei
 * @Date：2020/3/5 9:12
 * @version：1.0
 */
@Getter
@Setter
public class BannerListVO implements Serializable {
	
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
	 * 分享标题
	 */
	private String shareTitle;

	/**
	 * 分享图地址
	 */
	private String shareImageAddress;
	/**
	 *  该banner累计点击次数
	 */
	private Integer pv;
	/**
	 *  该banner累计点击用户数
	 */
	private Integer uv;
}
