package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class UserOrderStatisticsDayEmployeeQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 员工姓名
     */
    private String realName;
    /**
     * 员工账号
     */
    private String memberAccount;
    /**
     * 员工姓名
     */
    private String upRealName;
    /**
     * 员工账号
     */
    private String upMemberAccount;
}
