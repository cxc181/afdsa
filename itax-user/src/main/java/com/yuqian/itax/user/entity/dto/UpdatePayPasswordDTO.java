package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName: UpdatePayPasswordDTO
 * @Description: 修改支付密码参数
 * @Author: yejian
 * @Date: Created in 2019/12/11
 * @Version: 1.0
 * @Modified By:
 */
@Getter
@Setter
public class UpdatePayPasswordDTO {

    @ApiModelProperty(value = "账号", required = true)
    @NotEmpty(message = "账号不能为空")
    private String memberAccount;
    @ApiModelProperty(value = "旧密码(首次可以为空)")
    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;
    @ApiModelProperty(value = "新密码", required = true)
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

}
