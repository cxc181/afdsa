package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 企业注销接收dto
 * @author：yejian
 * @Date：2020/07/17 09:39
 */
@Getter
@Setter
public class CompanyCancelApiDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 企业ID
     */
    @NotNull(message = "企业ID不能为空")
    @ApiModelProperty(value = "企业ID",required = true)
    private Long companyId;

    /**
     * 会员账号
     */
    @NotBlank(message = "会员账号不能为空")
    @ApiModelProperty(value = "会员账号",required = true)
    private String regPhone;

    /**
     *
     */
    @NotBlank(message = "外部订单号不能为空")
    @Size(max = 32, message = "外部订单号不能超过32位字符")
    @ApiModelProperty(value = "外部订单号",required = true)
    private String externalOrderNo;

}
