package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 我的企业
 * 
 * @Date: 2019年12月06日 10:42:12 
 * @author yejian
 */
@Getter
@Setter
public class MemberCompanySimpleVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private Long memberId;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 企业类型 1-个体户 2-个人独资企业 3-有限合伙公司 4-有限责任公司
	 */
	@ApiModelProperty(value = "状态：1->个体户；2->个人独资企业；3->有限合伙公司；4->有限责任公司")
	private Integer companyType;

	/**
	 * 经营者名称
	 */
	@ApiModelProperty(value = "经营者名称")
	private String operatorName;

	/**
	 * 状态 1-正常 2-禁用 3-过期 4-已注销 5-注销中
	 */
	@ApiModelProperty(value = "状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中")
	private Integer status;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;

	public MemberCompanySimpleVO() {

	}

	public MemberCompanySimpleVO(MemberCompanyEntity entity) {
		this.id = entity.getId();
		this.memberId = entity.getMemberId();
		this.companyName = entity.getCompanyName();
		this.companyType = entity.getCompanyType();
		this.operatorName = entity.getOperatorName();
		this.status = entity.getStatus();
		this.oemCode = entity.getOemCode();
	}

	public static List<MemberCompanySimpleVO> getList(List<MemberCompanyEntity> list) {
		List<MemberCompanySimpleVO> result = Lists.newArrayList();
		if (CollectionUtil.isEmpty(list)) {
			return result;
		}
		for (MemberCompanyEntity entity : list) {
			result.add(new MemberCompanySimpleVO(entity));
		}
		return result;
	}
}
