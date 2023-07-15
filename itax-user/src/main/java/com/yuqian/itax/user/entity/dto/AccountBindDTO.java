package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 国金账号绑定dto
 * @author：shudu
 * @Date：2021/04/28 09:39
 */
@Getter
@Setter
public class AccountBindDTO {
    private static final long serialVersionUID = -1L;
    /**
     * 云财手机号
     */
    @ApiModelProperty(value = "云财手机号")
    private String phone;
    /**
     * 服务商姓名
     */
    @ApiModelProperty(value = "服务商姓名")
    private String name;
    /**
     * 服务商身份证号
     */
    @ApiModelProperty(value = "服务商身份证号")
    private String idCard;
}
