package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;
    /**
     * 账号
     */
    private String username;
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
     * 所属组织ID
     */
    private Long orgId;

    /**
     * 数据权限
     */
    private String tree;

    /**
     * 状态 0-禁用 1-可用 2-注销
     */
    private Integer status;

}
