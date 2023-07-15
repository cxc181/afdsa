package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class UserPageInfoVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 账号
     */
    private String username;

    /**
     * 账号类型  账号类型  1-管理员  2-坐席客服 3-普通用户
     */
    private Integer accountType;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 名称
     */
    private String nickname;
    /**
     * 角色
     */
    private String roleName;
    /**
     * 角色
     */
    private String roleId;

    /**
     * 所属组织
     */
    private String orgName;
    /**
     * 所属组织id
     */
    private Long orgId;
    /**
     * 创建时间
     */
    private Date addTime;
    /**
     * 状态
     */
    private Integer status;
}