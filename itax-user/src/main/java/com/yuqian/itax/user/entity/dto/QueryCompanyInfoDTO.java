package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 佣金开票个体户
 *
 * @Date: 2021年4月29日 14:48:40
 * @author shudu
 */
@Getter
@Setter
public class QueryCompanyInfoDTO {
    /**
     * 渠道用户映射id
     */
    @ApiModelProperty(value = "渠道用户映射id")
    @NotNull(message = "渠道用户映射id不能为空")
    private Long channelUserId;

    /**
     * 渠道编码
     */
    @ApiModelProperty(value = "渠道编码")
    @NotBlank(message = "渠道编码d不能为空")
    private String channelCode;

    /**
     * 经营者身份证号
     */
    @ApiModelProperty(value = "经营者身份证号")
    @NotBlank(message = "经营者身份证号不能为空")
    private String operatorIdCardNo;
    /**
     * 开票类目列表
     */
    @ApiModelProperty(value = "开票类目列表")
    @NotBlank(message = "开票类目列表不能为空")
    private String category;
}
