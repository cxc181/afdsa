package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 我的企业
 * 
 * @Date: 2019年12月06日 10:42:12 
 * @author yejian
 */
@Getter
@Setter
public class MemberCompanyBasicVo implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 企业id
	 */
	@ApiModelProperty(value = "企业id")
	private Long id;

	/**
	 * 企业名称
	 */
	@ApiModelProperty(value = "企业名称")
	private String companyName;

	/**
	 * 税号
	 */
	@ApiModelProperty(value = "税号")
	private String ein;

	/**
	 * 状态 1-正常 2-禁用 4-已注销 5-注销中
	 */
	@ApiModelProperty(value = "状态：1->正常；2->禁用 4->已注销 5->注销中")
	private Integer status;

	/**
	 * 过期状态 1-正常 2-即将过期 3-已过期
	 */
	@ApiModelProperty(value = "状态：1->正常；2->即将过期；3->已过期")
	private Integer overdueStatus;

	/**
	 * 有效截止时间
	 */
	@ApiModelProperty(value = "有效截止时间")
	@JSONField(format = "yyyy-MM-dd")
	private Date endTime;

	/**
	 * 经营者手机号
	 */
	@ApiModelProperty(value = "经营者手机号")
	private String operatorTel;

	/**
	 * 经营者名称
	 */
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	private Long parkId;

	/**
	 * 园区名称
	 */
	@ApiModelProperty(value = "园区名称")
	private String parkName;

	/**
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码")
	private String parkCode;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private Long memberId;

	/**
	 * 会员账号
	 */
	@ApiModelProperty(value = "会员账号")
	private String memberAccount;

	/**
	 * 所属会员名称 已实名取真实名称没有取昵称
	 */
	@ApiModelProperty(value = "所属会员名称")
	private String memberName;

	/**
	 * 实名认证状态 0-未认证 1-认证成功 2-认证失败
	 */
	@ApiModelProperty(value = "实名认证状态")
	private Integer authStatus;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

	/**
	 * 机构名称
	 */
	@ApiModelProperty(value = "机构名称")
	private String oemName;

	/**
	 * 是否存在未补传流水数
	 */
	private Integer shouldUploadWaterNum;
}
