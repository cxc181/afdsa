package com.yuqian.itax.system.entity.dto;

import com.yuqian.itax.system.entity.BannerEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.*;
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
public class BannerDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@NotNull(message="主键不能为空", groups={Update.class})
	private Long id;
	
	/**
	 * 轮播图
	 */
	@NotBlank(message="轮播图不能为空", groups={Add.class, Update.class})
	private String bannerAddress;
	
	/**
	 * 标题
	 */
	@NotBlank(message="标题不能为空", groups={Add.class, Update.class})
	@Size(max = 20, message = "标题不能超过20个字", groups={Add.class, Update.class})
	private String title;
	
	/**
	 * 排序
	 */
	@NotNull(message="排序不能为空", groups={Add.class, Update.class})
	@Min(value = 1, message = "仅支持输入1~100正整数", groups={Add.class, Update.class})
	@Max(value = 100, message = "仅支持输入1~100正整数", groups={Add.class, Update.class})
	private Integer orderNum;
	
	/**
	 * 是否分享 0-否 1-是
	 */
	@NotNull(message="是否分享不能为空", groups={Add.class, Update.class})
	@Min(value = 0, message = "是否分享选择有误", groups={Add.class, Update.class})
	@Max(value = 1, message = "是否分享选择有误", groups={Add.class, Update.class})
	private Integer isShare;
	
	/**
	 * 链接地址
	 */
	@Size(max = 100, message = "链接地址不能超过100个字", groups={Add.class, Update.class})
	private String bannerLink;
	
	/**
	 * 显示位置 1-首页 2-我的 
	 */
	@NotNull(message="广告位置不能为空", groups={Add.class})
	@Min(value = 1, message = "广告位置选择有误", groups={Add.class})
	@Max(value = 2, message = "广告位置选择有误", groups={Add.class})
	private Integer position;

	/**
	 *  机构编码
	 */
	private String oemCode;

	/**
	 * 分享标题
	 */
	private String shareTitle;

	/**
	 * 分享图地址
	 */
	private String shareImageAddress;

	public BannerEntity toEntity(String userAccount) {
		BannerEntity entity = new BannerEntity();
		entity.setOemCode(oemCode);
		entity.setBannerAddress(bannerAddress);
		entity.setBannerLink(bannerLink);
		entity.setIsShare(isShare);
		entity.setShareImageAddress(shareImageAddress);
		entity.setShareTitle(shareTitle);
		entity.setOrderNum(orderNum);
		entity.setPosition(position);
		entity.setTitle(title);
		entity.setAddUser(userAccount);
		entity.setAddTime(new Date());
		return entity;
	}

	public BannerEntity toEntity(String userAccount, BannerEntity bannerEntity) {
		BannerEntity entity = new BannerEntity();
		entity.setId(bannerEntity.getId());
		entity.setOemCode(bannerEntity.getOemCode());
		entity.setIsShow(bannerEntity.getIsShow());
		entity.setBannerAddress(bannerAddress);
		entity.setBannerLink(bannerLink);
		entity.setIsShare(isShare);
		entity.setShareImageAddress(shareImageAddress);
		entity.setShareTitle(shareTitle);
		entity.setOrderNum(orderNum);
		entity.setPosition(position);
		entity.setTitle(title);
		entity.setUpdateUser(userAccount);
		entity.setUpdateTime(new Date());
		return entity;
	}
}
