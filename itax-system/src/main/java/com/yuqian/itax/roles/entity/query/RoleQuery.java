package com.yuqian.itax.roles.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleQuery extends BaseQuery {

    private static final long serialVersionUID = 3117013156113006743L;

    /**
     * 组织Id
     */
    private  Long orgId;
    /**
     * 角色名称
     */
    private  String roleName;

    /**
     * 角色类型 0：系统角色  不可删除  1：用户创建
     */
    private  Integer type;

}
