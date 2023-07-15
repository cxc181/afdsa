package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/6/5 16:35
 *  @Description: 直推用户列表查询BEAN
 */
@Getter
@Setter
public class ExtendUserQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 指定用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 当前用户ID
     */
    private Long currUserId;

    /**
     * 推广角色类型 1-散客 2-直客 3-顶级直客
     */
    private Integer extendType;

    /**
     * 关键字
     */
    private String keyword;
}