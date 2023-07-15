package com.yuqian.itax.roles.entity.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class RolePO implements Serializable {
    private static final long serialVersionUID = 3117013156113006743L;

    /**
     * 角色Id
     */
    private  Long id;
    /**
     * 所属组织Id
     */
    private  Long orgId;

    /**
     * 角色名称
     */
    private  String roleName;
    /**
     * 角色Code
     */
    private  String roleCode;
    /**
     * 所属描述
     */
    private  String remark;
    /**
     * 角色状态  状态 0-不可用 1-可用
     */
    private  Integer status;
    /**
     * 角色菜单联系表  菜单id
     */
    private List<Long> menuIdList;

}
