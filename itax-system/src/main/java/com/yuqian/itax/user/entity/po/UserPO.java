package com.yuqian.itax.user.entity.po;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UserPO  implements Serializable {
    private static final long serialVersionUID = -1L;

    private  Long id;

    /**
     * 账号
     */
    @NotBlank(message = "请输入用户账号")
    @Size(min = 6, max = 20, message = "账号不能小于6位字符，超过20位字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]{8,16}$", message = "请输入8~16位，支持字母/数字/下划线组合")
    private String username;
    /**
     * 绑定手机号
     */
    @NotBlank(message = "请输入用户绑定手机号")
    private String phone;
    /**
     * 名称
     */
    @NotBlank(message = "请输入用户名称")
    private String nickname;
    /**
     * 代理等级      * 账号类型  1-管理员  2-客服坐席 3-普通用户
     */
    private int accountType;


    /**
     * 备注
     */
    private String remark;


    /**
     * 是否设为客服坐席 0-否 1-是
     */
//    @NotNull(message = "请选择用户是否设为客服坐席")
    private Integer isCustomer;


    /**
     * 角色id
     */
    @NotNull(message = "角色")
    private Long roleId;

    /**
     * 组织ID
     */
    @NotNull(message = "请选择用户组织")
    private Long orgId;

    /**
     * 机构code
     */
    private String oemCode;


}

