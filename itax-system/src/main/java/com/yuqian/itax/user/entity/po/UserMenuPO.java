package com.yuqian.itax.user.entity.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserMenuPO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 用户Id
     */
    private  Long userId;

    /**
     * 所属描述
     */
    private  String remark;
    /**
     * 角色状态  状态 0-不可用 1-可用
     */
    private  Integer status;
    /**
     * 用户菜单联系表  菜单id
     */
    private List<Long> menuIdList;

}
