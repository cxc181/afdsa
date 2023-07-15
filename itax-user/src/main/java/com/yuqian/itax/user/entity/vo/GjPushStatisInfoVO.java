package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 国金推广信息统计
 * 
 * @Date: 2019年12月06日 10:48:28 
 * @author Kaven
 */
@Getter
@Setter
public class GjPushStatisInfoVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 累计新增裂变个体
	 */
	private Integer totalFissionCompanyCount;

	/**
	 * 累计新增直推个体
	 */
	private Integer totalDirectCompanyCount;

	/**
	 * 累计新增裂变用户
	 */
	private Integer totalFissionUserCount;

	/**
	 * 累计新增直推用户
	 */
	private Integer totalDirectUserCount;

	/**
	 * 本月新增裂变个体
	 */
	private Integer monthFissionCompanyCount;

	/**
	 * 本月新增直推个体
	 */
	private Integer monthDirectCompanyCount;

	/**
	 * 本月新增裂变用户
	 */
	private Integer monthFissionUserCount;

	/**
	 * 本月新增直推用户
	 */
	private Integer monthDirectUserCount;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 渠道服务商id
	 */
	private Long channelServiceId;

	/**
	 * 渠道员工id
	 */
	private Long channelEmployeesId;

	/**
	 * 渠道code
	 */
	private String channelCode;

	/**
	 * 产品code
	 */
	private String channelProductCode;

}
