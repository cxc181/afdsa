package com.yuqian.itax.roles.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MenuDetailVO implements Serializable {

    private static final long serialVersionUID = -7459081692802671039L;
    private Long id;

    /**
     * 父菜单ID，一级菜单为0
     */
    private Long parentId;
    /**
     * 路由名称
     */
    private String name;
    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    private Integer type;
    /**
     * 菜单URL
     */
    private String url;
    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer orderNum;
    /**
     * vue组件名称
     */
    private String component;
    /**
     *重定向地址
     */
    private String redirect;

    /**
     * 菜单名称
     */
    private String title;
    /**
     *0 隐藏 1不隐藏
     */
    private Integer hidden;
    /**
     * 权限接口id
     */
    private List<Long> apiIdList;

    private String level;

    private Date addTime;

    private Long addUser;

    /**
     * 父菜单名称
     */
    private String parentName;
}
