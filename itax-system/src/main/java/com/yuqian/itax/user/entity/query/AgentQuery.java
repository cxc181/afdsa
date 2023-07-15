package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AgentQuery extends BaseQuery {
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
     * 代理等级       平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;
    /**
     * 推广账号手机号
     */
    private String bindingAccount;
    /**
     * 账户状态 0-禁用 1-可用 2-注销
     */
    private Integer status;

    /**
     * 数据权限
     */
    private  String tree;
    /**
     * 用户ID
     */
    private  Long userId;
}
