package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.yuqian.itax.common.base.vo.PageResultVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/5 9:32
 *  @Description: 推广中心-会员个体企业列表展示VO
 */
@Getter
@Setter
public class MemberCoStatisticVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	// 所属用户账号
	private String memberAccount;

	// 用户真实姓名
	private String realName;

	// 用户昵称
	private String memberName;
	
	/**
	 * 个体总数
	 */
	private Long totalCount;
	
	/**
	 * 有效个体
	 */
	private Long availableCount;
	
	/**
	 * 已注销
	 */
	private Long cancelCount;

	/**
	 * 企业列表（带分页信息）
	 */
	private PageResultVo<MemberComVO> companyList;
}
