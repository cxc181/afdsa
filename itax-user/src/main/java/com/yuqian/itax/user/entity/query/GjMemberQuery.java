package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description 渠道会员查询
 * @Author  Kaven
 * @Date   2020/9/7 11:09
*/
@Getter
@Setter
public class GjMemberQuery extends BaseQuery implements Serializable {

	private static final long serialVersionUID = -1L;

	@NotNull(message = "服务商id或者员工id不能为空")
	private Long channelServiceId;// 渠道服务商id

	private Integer isFission;// 是否裂变 0-直推 1-裂变

	private String oemCode; // 云财oemcode

	private String memberName;// 会员名称或账号

	private Long memberId;// 会员id

	private String status;// 状态

	private List<String> statusList; //状态列表

	private String channelOemCode; //国金oem机构

	private String memberAccount; //会员账号

	private Long channelUserId; //国金用户id
}