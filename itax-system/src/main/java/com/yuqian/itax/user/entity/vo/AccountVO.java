package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 外部根据手机号查询后台和小程序账号实体
 */
@Getter
@Setter
public class AccountVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 绑定账号id
     */
    @ApiModelProperty(value = "绑定账号id")
    private Long bindAccountId;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String account;

    /**
     * 等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人
     */
    @ApiModelProperty(value = "等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人")
    private int bindAccountLevel;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;
}
