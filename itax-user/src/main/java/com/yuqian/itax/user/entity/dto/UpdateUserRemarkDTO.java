package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 佣金开票个体户
 *
 * @Date: 2021年4月29日 14:48:40
 * @author shudu
 */
@Getter
@Setter
public class UpdateUserRemarkDTO {
    /**
     * 云财用户id
     */
    @ApiModelProperty(value = "云财用户id")
    private String userId;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
