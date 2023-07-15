package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class GJLoginVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 额外信息
     */
    private String extraMsg;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请人账号
     */
    private String inviteUserAccount;

    /**
     * 会员昵称
     */
    private String nickName;

    /**
     * 小程序token
     */
    private String token;

    /**
     * oem机构编码
     */
    private String oemCode;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户主键id
     */
    private Long userId;

    /**
     * 用户联系电话
     */
    private String userPhone;

    /**
     * 用户角色，1：用户，2：员工
     */
    private Integer userRole;
}
