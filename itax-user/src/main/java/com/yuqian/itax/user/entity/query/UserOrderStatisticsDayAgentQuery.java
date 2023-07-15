package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserOrderStatisticsDayAgentQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 等级
     */
    private Integer platformType;
    /**
     * 姓名
     */
    private String nickname;
    /**
     * 注册账号
     */
    private String username;
    /**
     * 绑定手机号
     */
    private String bindingAccount;
}
