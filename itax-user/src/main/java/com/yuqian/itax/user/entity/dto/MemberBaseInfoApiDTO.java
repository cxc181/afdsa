package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员个人基本信息接收dto
 *
 * @author：yejian
 * @Date：2020/11/13 09:30
 */
@Getter
@Setter
public class MemberBaseInfoApiDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 会员id
     */
    @NotNull(message = "会员id不能为空")
    @ApiModelProperty(value = "会员id")
    private Long userId;

    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码")
    private String oemCode;

    /**
     * 会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人
     */
    @NotNull(message = "会员等级不能为空")
    @ApiModelProperty(value = "会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人")
    private Integer levelNo;

    /**
     * 是否需要token 1-不需要 2-需要
     */
    @NotNull(message = "是否需要token不能为空")
    @ApiModelProperty(value = "是否需要token 1-不需要 2-需要")
    private Integer isNeedToken;

}
