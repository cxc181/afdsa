package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户菜单返回实体
 */
@Getter
@Setter
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 4540550830322974991L;

    private Long userId;
    /**
     * 角色名称
     */
    private String name;

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
