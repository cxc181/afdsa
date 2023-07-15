package com.yuqian.itax.common.base.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class CustomerWorker implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String useraccount;

    /**
     * 公司code
     */
    private String oemCode;

    /**
     * 团队id
     */
    private Integer groupId;

    /**
     * 用户类型：0-超级管理员，1-系统管理员，9-普通用户
     */
    private String usertype;

    /**
     * 角色id
     */
    private List<Long> roleIds;

    /**
     * 用户权限
     */
    private Set<String> permissions = new HashSet<>();

    public CustomerWorker() {
        super();
    }

    public CustomerWorker(Long userId, String useraccount, String oemCode, Integer groupId) {
        super();
        this.userId = userId;
        this.useraccount = useraccount;
        this.oemCode = oemCode;
        this.groupId = groupId;
    }
}
