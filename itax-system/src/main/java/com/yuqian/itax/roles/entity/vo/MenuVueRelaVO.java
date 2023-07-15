package com.yuqian.itax.roles.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class MenuVueRelaVO implements Serializable {

    private static final long serialVersionUID = -7244649549577861789L;
    /**
     *
     */
    private Long id;
    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 菜单父id
     */
    private Long parentMenuId;
    /**
     * 路径
     */
    private String path;
    /**
     * vue组件名称
     */
    private String component;
    /**
     * 重定向地址
     */
    private String redirect;
    /**
     * 名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer orderNum;
    /**
     * 样式
     */
    private String meta;

    /**
     * 是否隐藏
     */
    private Integer hidden;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 菜单类型
     */
    private Integer type;
}
