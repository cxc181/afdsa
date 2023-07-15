package com.yuqian.itax.roles.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 4540550830322974991L;
    private Long id;
    /**
     * 角色名称
     */
    private String name;

    /**
     * 类型 0：系统角色  不可删除  1：用户创建
     */
    private Integer type;
    /**
     * 状态 0 禁用 1 启用
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 菜单关联id
     */
    private Long[] menuIdList;

}
